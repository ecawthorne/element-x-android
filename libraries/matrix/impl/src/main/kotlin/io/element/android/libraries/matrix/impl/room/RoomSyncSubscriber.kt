/*
 * Copyright (c) 2023 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.element.android.libraries.matrix.impl.room

import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.timeline.item.event.EventType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.matrix.rustcomponents.sdk.RequiredState
import org.matrix.rustcomponents.sdk.RoomListServiceInterface
import org.matrix.rustcomponents.sdk.RoomSubscription
import timber.log.Timber

private const val DEFAULT_TIMELINE_LIMIT = 20u

class RoomSyncSubscriber(
    private val roomListService: RoomListServiceInterface,
    private val dispatchers: CoroutineDispatchers,
) {
    private val subscribedRoomIds = mutableSetOf<RoomId>()
    private val mutex = Mutex()

    private val settings = RoomSubscription(
        requiredState = listOf(
            RequiredState(key = EventType.STATE_ROOM_NAME, value = ""),
            RequiredState(key = EventType.STATE_ROOM_TOPIC, value = ""),
            RequiredState(key = EventType.STATE_ROOM_AVATAR, value = ""),
            RequiredState(key = EventType.STATE_ROOM_CANONICAL_ALIAS, value = ""),
            RequiredState(key = EventType.STATE_ROOM_JOIN_RULES, value = ""),
            RequiredState(key = EventType.STATE_ROOM_POWER_LEVELS, value = ""),
        ),
        timelineLimit = DEFAULT_TIMELINE_LIMIT,
        // We don't need heroes here as they're already included in the `all_rooms` list
        includeHeroes = false,
    )

    suspend fun subscribe(roomId: RoomId) = mutex.withLock {
        withContext(dispatchers.io) {
            try {
                subscribeToRoom(roomId)
            } catch (exception: Exception) {
                Timber.e("Failed to subscribe to room $roomId")
            }
        }
    }

    suspend fun batchSubscribe(roomIds: List<RoomId>) = mutex.withLock {
        withContext(dispatchers.io) {
            for (roomId in roomIds) {
                try {
                    subscribeToRoom(roomId)
                } catch (cancellationException: CancellationException) {
                    throw cancellationException
                } catch (exception: Exception) {
                    Timber.e("Failed to subscribe to room $roomId")
                }
            }
        }
    }

    private fun subscribeToRoom(roomId: RoomId) {
        if (!isSubscribedTo(roomId)) {
            Timber.d("Subscribing to room $roomId}")
            roomListService.room(roomId.value).use { roomListItem ->
                roomListItem.subscribe(settings)
            }
        }
        subscribedRoomIds.add(roomId)
    }

    fun isSubscribedTo(roomId: RoomId): Boolean {
        return subscribedRoomIds.contains(roomId)
    }
}

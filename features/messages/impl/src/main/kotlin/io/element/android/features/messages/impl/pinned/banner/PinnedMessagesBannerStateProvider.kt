/*
 * Copyright (c) 2024 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.element.android.features.messages.impl.pinned.banner

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

internal class PinnedMessagesBannerStateProvider : PreviewParameterProvider<PinnedMessagesBannerState> {
    override val values: Sequence<PinnedMessagesBannerState>
        get() = sequenceOf(
            aPinnedMessagesBannerState(pinnedMessagesCount = 1, currentPinnedMessageIndex = 0),
            aPinnedMessagesBannerState(pinnedMessagesCount = 2, currentPinnedMessageIndex = 0),
            aPinnedMessagesBannerState(pinnedMessagesCount = 4, currentPinnedMessageIndex = 0),
            aPinnedMessagesBannerState(pinnedMessagesCount = 4, currentPinnedMessageIndex = 1),
            aPinnedMessagesBannerState(pinnedMessagesCount = 4, currentPinnedMessageIndex = 2),
            aPinnedMessagesBannerState(pinnedMessagesCount = 4, currentPinnedMessageIndex = 3),
        )
}

internal fun aPinnedMessagesBannerState(
    pinnedMessagesCount: Int = 0,
    currentPinnedMessageIndex: Int = -1,
    eventSink: (PinnedMessagesBannerEvents) -> Unit = {}
) = PinnedMessagesBannerState(
    pinnedMessagesCount = pinnedMessagesCount,
    currentPinnedMessageIndex = currentPinnedMessageIndex,
    eventSink = eventSink
)

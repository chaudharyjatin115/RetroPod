package com.maxrave.simpmusic.ui.ipod

import com.maxrave.domain.data.model.browse.album.Track

/**
 * State snapshot representing current media playback details for the iPod LCD display.
 */
data class IpodPlaybackState(
    val currentSong: Track? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val lyricsText: String? = null
)

/**
 * Callbacks triggered by user interactions with the iPod Click Wheel.
 */
data class IpodWheelActions(
    val onScroll: (steps: Int) -> Unit = {},
    val onMenuClick: () -> Unit = {},
    val onSelectClick: () -> Unit = {},
    val onPlayPauseClick: () -> Unit = {},
    val onNextClick: () -> Unit = {},
    val onPrevClick: () -> Unit = {}
)

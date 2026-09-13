package com.maxrave.simpmusic.ui.ipod

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maxrave.domain.data.model.SongModel

@Composable
fun IpodHardwareShell(
    finish: IpodFinish,
    lcdTheme: LcdTheme,
    screen: IpodScreen,
    canGoBack: Boolean,
    items: List<IpodMenuItem>,
    selectedIndex: Int,
    currentSong: SongModel?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    lyricsText: String?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onWheelScroll: (steps: Int) -> Unit,
    onMenuClick: () -> Unit,
    onSelectClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F11)),
        contentAlignment = Alignment.Center
    ) {
        // Authentic iPod Classic Hardware Chassis Box
        Box(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxSize()
                .padding(16.dp)
                .shadow(elevation = 24.dp, shape = RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            finish.bodyGradientTop,
                            finish.bodyGradientBottom
                        )
                    )
                )
                .border(width = 2.dp, color = finish.rimColor, shape = RoundedCornerShape(28.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // TOP SECTION: LCD Display Window
                IpodLcdDisplay(
                    lcdTheme = lcdTheme,
                    screen = screen,
                    canGoBack = canGoBack,
                    items = items,
                    selectedIndex = selectedIndex,
                    currentSong = currentSong,
                    isPlaying = isPlaying,
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    lyricsText = lyricsText,
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // BOTTOM SECTION: Click Wheel Hardware Controls
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    ClickWheel(
                        finish = finish,
                        isPlaying = isPlaying,
                        onScroll = onWheelScroll,
                        onMenuClick = onMenuClick,
                        onSelectClick = onSelectClick,
                        onPlayPauseClick = onPlayPauseClick,
                        onNextClick = onNextClick,
                        onPrevClick = onPrevClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

package com.maxrave.simpmusic.ui.ipod

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxrave.domain.data.model.browse.album.Track

@Composable
fun IpodHardwareShell(
    hardwareTheme: IpodHardwareTheme,
    lcdTheme: LcdTheme,
    screen: IpodScreen,
    canGoBack: Boolean,
    items: List<IpodMenuItem>,
    selectedIndex: Int,
    currentSong: Track?,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    lyricsText: String?,
    searchQuery: String,
    isHoldEnabled: Boolean,
    stickersEnabled: Boolean = true,
    onHoldToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onYoutubeLoginDone: (String) -> Unit = {},
    onWheelScroll: (steps: Int) -> Unit,
    onMenuClick: () -> Unit,
    onSelectClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bodyBrush = if (hardwareTheme.bodyGradientBottom != null) {
        Brush.verticalGradient(
            colors = listOf(
                hardwareTheme.bodyColor,
                hardwareTheme.bodyGradientBottom
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                hardwareTheme.bodyColor,
                hardwareTheme.bodyColor
            )
        )
    }

    // Physical iPod Shell spans the entire application screen
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bodyBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. TOP SECTION: Hold Switch Pill (Upper Right)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp, end = 4.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                HoldSwitch(
                    isHoldEnabled = isHoldEnabled,
                    onHoldToggle = onHoldToggle
                )
            }

            // 2. MIDDLE SECTION: Embedded Rectangular Display Window with Black Bezel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(hardwareTheme.bezelColor)
                    .border(width = 2.5.dp, color = Color(0xFF0D0D0E), shape = RoundedCornerShape(12.dp))
                    .padding(6.dp)
            ) {
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
                    onYoutubeLoginDone = onYoutubeLoginDone,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. LOWER SECTION: Large Perfect Circle Click Wheel
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .aspectRatio(1f)
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                ClickWheel(
                    theme = hardwareTheme,
                    isPlaying = isPlaying,
                    isHoldEnabled = isHoldEnabled,
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

        // Whimsical & Modern Aesthetic Overlay Stickers
        if (stickersEnabled) {
            Text("🎧", fontSize = 28.sp, modifier = Modifier.align(Alignment.TopStart).offset(x = 20.dp, y = 38.dp))
            Text("🎀", fontSize = 28.sp, modifier = Modifier.align(Alignment.TopStart).offset(x = 60.dp, y = 38.dp))
            Text("🩰", fontSize = 26.sp, modifier = Modifier.align(Alignment.TopEnd).offset(x = (-100).dp, y = 38.dp))
            Text("⚡", fontSize = 24.sp, modifier = Modifier.align(Alignment.TopEnd).offset(x = (-60).dp, y = 38.dp))
            Text("💖", fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterStart).offset(x = 12.dp, y = (-120).dp))
            Text("💿", fontSize = 26.sp, modifier = Modifier.align(Alignment.CenterEnd).offset(x = (-12).dp, y = (-120).dp))
            Text("🪐", fontSize = 26.sp, modifier = Modifier.align(Alignment.BottomStart).offset(x = 24.dp, y = (-32).dp))
            Text("🦋", fontSize = 26.sp, modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-24).dp, y = (-32).dp))
            Text("✨", fontSize = 22.sp, modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-12).dp))
        }
    }
}

@Composable
fun HoldSwitch(
    isHoldEnabled: Boolean,
    onHoldToggle: () -> Unit
) {
    val bgColor by animateColorAsState(
        if (isHoldEnabled) Color(0xFFDC2626) else Color(0x66FFFFFF)
    )

    Box(
        modifier = Modifier
            .shadow(elevation = 2.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(bgColor)
            .border(width = 0.5.dp, color = Color.White.copy(alpha = 0.4f), shape = CircleShape)
            .clickable { onHoldToggle() }
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isHoldEnabled) "HOLD ON" else "HOLD OFF",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(if (isHoldEnabled) Color(0xFFFEF08A) else Color.White)
            )
        }
    }
}

package com.maxrave.simpmusic.ui.ipod

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxrave.domain.data.model.browse.album.Track

/**
 * Root container rendering the physical iPod hardware chassis, Hold switch, LCD screen bezel, and Click Wheel.
 */
@Composable
fun IpodHardwareShell(
    hardwareTheme: IpodHardwareTheme,
    lcdTheme: LcdTheme,
    screen: IpodScreen,
    canGoBack: Boolean,
    items: List<IpodMenuItem>,
    selectedIndex: Int,
    playbackState: IpodPlaybackState,
    wheelActions: IpodWheelActions,
    searchQuery: String,
    isHoldEnabled: Boolean,
    stickersEnabled: Boolean = true,
    onHoldToggle: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onYoutubeLoginDone: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    IpodHardwareShell(
        hardwareTheme = hardwareTheme,
        lcdTheme = lcdTheme,
        screen = screen,
        canGoBack = canGoBack,
        items = items,
        selectedIndex = selectedIndex,
        currentSong = playbackState.currentSong,
        isPlaying = playbackState.isPlaying,
        currentPositionMs = playbackState.currentPositionMs,
        durationMs = playbackState.durationMs,
        lyricsText = playbackState.lyricsText,
        searchQuery = searchQuery,
        isHoldEnabled = isHoldEnabled,
        stickersEnabled = stickersEnabled,
        onHoldToggle = onHoldToggle,
        onSearchQueryChange = onSearchQueryChange,
        onYoutubeLoginDone = onYoutubeLoginDone,
        onWheelScroll = wheelActions.onScroll,
        onMenuClick = wheelActions.onMenuClick,
        onSelectClick = wheelActions.onSelectClick,
        onPlayPauseClick = wheelActions.onPlayPauseClick,
        onNextClick = wheelActions.onNextClick,
        onPrevClick = wheelActions.onPrevClick,
        modifier = modifier
    )
}

/**
 * Overload accepting direct primitive parameters for backwards compatibility.
 */
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
                hardwareTheme.bodyColor.copy(alpha = 0.92f)
            )
        )
    }

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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. TOP SECTION: Physical Hold Switch + Laser Engraved Chassis Badge
            ChassisHeader(
                stickersEnabled = stickersEnabled,
                badgeColor = hardwareTheme.wheelTextColor,
                isHoldEnabled = isHoldEnabled,
                onHoldToggle = onHoldToggle
            )

            // 2. MIDDLE SECTION: Embedded Screen Window with Premium Bezel
            BezelScreenWindow(
                bezelColor = hardwareTheme.bezelColor,
                modifier = Modifier.fillMaxWidth()
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

            Spacer(modifier = Modifier.height(16.dp))

            // 3. LOWER SECTION: Click Wheel
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.83f)
                    .aspectRatio(1f)
                    .padding(bottom = 16.dp),
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
    }
}

@Composable
private fun ChassisHeader(
    stickersEnabled: Boolean,
    badgeColor: Color,
    isHoldEnabled: Boolean,
    onHoldToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, top = 8.dp, end = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (stickersEnabled) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "RETROPOD CLASSIC • 128GB",
                    color = badgeColor.copy(alpha = 0.75f),
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
            }
        } else {
            Spacer(modifier = Modifier.width(1.dp))
        }

        HoldSwitch(
            isHoldEnabled = isHoldEnabled,
            onHoldToggle = onHoldToggle
        )
    }
}

@Composable
private fun BezelScreenWindow(
    bezelColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = Color.Black.copy(alpha = 0.7f)
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        bezelColor,
                        bezelColor.copy(alpha = 0.95f)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.Black.copy(alpha = 0.6f)
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(8.dp)
    ) {
        content()
    }
}

@Composable
fun HoldSwitch(
    isHoldEnabled: Boolean,
    onHoldToggle: () -> Unit
) {
    val sliderOffset by animateDpAsState(
        targetValue = if (isHoldEnabled) 18.dp else 2.dp,
        animationSpec = tween(durationMillis = 180)
    )

    val trackColor by animateColorAsState(
        targetValue = if (isHoldEnabled) Color(0xFFDC2626) else Color(0x40FFFFFF),
        animationSpec = tween(durationMillis = 180)
    )

    Box(
        modifier = Modifier
            .clickable { onHoldToggle() }
            .padding(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "HOLD",
                color = if (isHoldEnabled) Color(0xFFEF4444) else Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.5.sp
            )

            // Switch Track Container
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(18.dp)
                    .shadow(elevation = 2.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(trackColor)
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                // Sliding Metallic Knob
                Box(
                    modifier = Modifier
                        .offset { IntOffset(sliderOffset.roundToPx(), 0) }
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White,
                                    Color(0xFFE2E8F0)
                                )
                            )
                        )
                        .border(0.5.dp, Color.Gray.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isHoldEnabled) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDC2626))
                        )
                    }
                }
            }
        }
    }
}

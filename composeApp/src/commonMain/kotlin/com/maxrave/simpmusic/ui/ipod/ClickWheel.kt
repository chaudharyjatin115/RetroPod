package com.maxrave.simpmusic.ui.ipod

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxrave.simpmusic.ui.icon.Pause
import com.maxrave.simpmusic.ui.icon.PlayArrow
import com.maxrave.simpmusic.ui.icon.SimpIcons
import com.maxrave.simpmusic.ui.icon.SkipNext
import com.maxrave.simpmusic.ui.icon.SkipPrevious
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

@Composable
fun ClickWheel(
    theme: IpodHardwareTheme,
    isPlaying: Boolean,
    isHoldEnabled: Boolean,
    onScroll: (steps: Int) -> Unit,
    onMenuClick: () -> Unit,
    onSelectClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var accumulatedAngle by remember { mutableFloatStateOf(0f) }
    var pressedSector by remember { mutableStateOf<WheelSector?>(null) }

    val angleStepThreshold = 14f // 14 degrees per scroll step (~25 steps per full rotation)

    val centerInteractionSource = remember { MutableInteractionSource() }
    val isCenterPressed by centerInteractionSource.collectIsPressedAsState()

    val centerScale by animateFloatAsState(
        targetValue = if (isCenterPressed) 0.94f else 1f,
        animationSpec = tween(durationMillis = 100)
    )

    val wheelBrush = when (theme.finishStyle) {
        HardwareFinish.TRANSPARENT -> Brush.radialGradient(
            colors = listOf(
                theme.wheelColor.copy(alpha = 0.88f),
                theme.wheelColor.copy(alpha = 0.72f),
                theme.wheelColor.copy(alpha = 0.95f)
            )
        )
        HardwareFinish.GLOSSY_PLASTIC -> Brush.radialGradient(
            colors = listOf(
                theme.wheelColor,
                theme.wheelColor,
                Color.White.copy(alpha = 0.22f),
                theme.wheelColor.copy(alpha = 0.95f)
            )
        )
        HardwareFinish.BRUSHED_ALUMINUM, HardwareFinish.POLISHED_METAL -> Brush.radialGradient(
            colors = listOf(
                theme.wheelColor,
                theme.wheelColor.copy(alpha = 0.96f),
                Color.White.copy(alpha = 0.15f),
                theme.wheelColor.copy(alpha = 0.88f)
            )
        )
        else -> Brush.radialGradient(
            colors = listOf(
                theme.wheelColor,
                theme.wheelColor,
                theme.wheelColor.copy(alpha = 0.92f)
            )
        )
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(
                elevation = if (theme.finishStyle == HardwareFinish.TRANSPARENT) 3.dp else 12.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 0.6f)
            )
            .clip(CircleShape)
            .background(wheelBrush)
            .border(
                width = 1.dp,
                color = theme.wheelTextColor.copy(alpha = 0.18f),
                shape = CircleShape
            )
            .pointerInput(isHoldEnabled) {
                if (isHoldEnabled) return@pointerInput

                detectDragGestures(
                    onDragStart = {
                        accumulatedAngle = 0f
                        pressedSector = null
                    },
                    onDragEnd = { pressedSector = null },
                    onDragCancel = { pressedSector = null },
                    onDrag = { change, dragAmount ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val pos = change.position
                        val prevPos = pos - dragAmount

                        val currentAngle = (atan2((pos.y - center.y).toDouble(), (pos.x - center.x).toDouble()) * (180.0 / PI)).toFloat()
                        val prevAngle = (atan2((prevPos.y - center.y).toDouble(), (prevPos.x - center.x).toDouble()) * (180.0 / PI)).toFloat()

                        var delta = currentAngle - prevAngle
                        if (delta > 180f) delta -= 360f
                        if (delta < -180f) delta += 360f

                        if (abs(delta) < 60f) {
                            accumulatedAngle += delta
                            if (abs(accumulatedAngle) >= angleStepThreshold) {
                                val steps = (accumulatedAngle / angleStepThreshold).toInt()
                                onScroll(steps)
                                accumulatedAngle -= steps * angleStepThreshold
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                        change.consume()
                    }
                )
            }
            .pointerInput(isHoldEnabled) {
                if (isHoldEnabled) return@pointerInput

                detectTapGestures { tapOffset ->
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val dx = tapOffset.x - center.x
                    val dy = tapOffset.y - center.y

                    var angle = (atan2(dy.toDouble(), dx.toDouble()) * (180.0 / PI)).toFloat()
                    if (angle < 0) angle += 360f

                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    when {
                        angle >= 225f && angle < 315f -> {
                            pressedSector = WheelSector.MENU
                            onMenuClick()
                        }
                        angle >= 45f && angle < 135f -> {
                            pressedSector = WheelSector.PLAY_PAUSE
                            onPlayPauseClick()
                        }
                        angle >= 135f && angle < 225f -> {
                            pressedSector = WheelSector.PREV
                            onPrevClick()
                        }
                        else -> {
                            pressedSector = WheelSector.NEXT
                            onNextClick()
                        }
                    }
                }
            }
    ) {
        // Subtle sector press overlay highlight
        if (pressedSector != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.08f))
            )
        }

        // TOP LABEL: MENU
        Text(
            text = "MENU",
            color = theme.wheelTextColor.copy(alpha = if (pressedSector == WheelSector.MENU) 1f else 0.9f),
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 0.5.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 22.dp)
        )

        // BOTTOM LABEL: PLAY / PAUSE ICON
        Icon(
            imageVector = if (isPlaying) SimpIcons.Pause else SimpIcons.PlayArrow,
            contentDescription = "Play Pause",
            tint = theme.wheelTextColor.copy(alpha = if (pressedSector == WheelSector.PLAY_PAUSE) 1f else 0.9f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 22.dp)
                .size(16.dp)
        )

        // RIGHT LABEL: FAST FORWARD / NEXT ICON
        Icon(
            imageVector = SimpIcons.SkipNext,
            contentDescription = "Next",
            tint = theme.wheelTextColor.copy(alpha = if (pressedSector == WheelSector.NEXT) 1f else 0.9f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 22.dp)
                .size(18.dp)
        )

        // LEFT LABEL: REWIND / PREVIOUS ICON
        Icon(
            imageVector = SimpIcons.SkipPrevious,
            contentDescription = "Previous",
            tint = theme.wheelTextColor.copy(alpha = if (pressedSector == WheelSector.PREV) 1f else 0.9f),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 22.dp)
                .size(18.dp)
        )

        // CENTER SELECT BUTTON
        val centerButtonBrush = Brush.radialGradient(
            colors = listOf(
                theme.centerButtonColor,
                theme.centerButtonColor,
                theme.centerButtonColor.copy(alpha = 0.88f)
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize(0.38f)
                .align(Alignment.Center)
                .scale(centerScale)
                .shadow(
                    elevation = if (isCenterPressed) 2.dp else 6.dp,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .background(centerButtonBrush)
                .border(
                    width = 1.dp,
                    color = theme.wheelTextColor.copy(alpha = 0.18f),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = centerInteractionSource,
                    indication = null
                ) {
                    if (!isHoldEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSelectClick()
                    }
                }
        )
    }
}

private enum class WheelSector {
    MENU, PLAY_PAUSE, PREV, NEXT
}

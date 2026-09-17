package com.maxrave.simpmusic.ui.ipod

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    val angleStepThreshold = 14f // 14 degrees per step (~25 steps per 360 deg circle)

    val wheelBrush = when (theme.finishStyle) {
        HardwareFinish.TRANSPARENT -> Brush.radialGradient(
            colors = listOf(
                theme.wheelColor.copy(alpha = 0.85f),
                theme.wheelColor.copy(alpha = 0.70f)
            )
        )
        HardwareFinish.GLOSSY_PLASTIC -> Brush.radialGradient(
            colors = listOf(
                theme.wheelColor,
                theme.wheelColor,
                Color.White.copy(alpha = 0.25f),
                theme.wheelColor
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
            .shadow(elevation = if (theme.finishStyle == HardwareFinish.TRANSPARENT) 2.dp else 10.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(wheelBrush)
            .border(
                width = 1.dp,
                color = theme.wheelTextColor.copy(alpha = 0.2f),
                shape = CircleShape
            )
            .pointerInput(isHoldEnabled) {
                if (isHoldEnabled) return@pointerInput

                detectDragGestures(
                    onDragStart = {
                        accumulatedAngle = 0f
                    },
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
                        angle >= 225f && angle < 315f -> onMenuClick()          // TOP = MENU
                        angle >= 45f && angle < 135f -> onPlayPauseClick()       // BOTTOM = PLAY/PAUSE
                        angle >= 135f && angle < 225f -> onPrevClick()          // LEFT = PREV/REW
                        else -> onNextClick()                                    // RIGHT = NEXT/FF
                    }
                }
            }
    ) {
        // TOP LABEL: MENU
        Text(
            text = "MENU",
            color = theme.wheelTextColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 22.dp)
        )

        // BOTTOM LABEL: PLAY / PAUSE ICON
        Icon(
            imageVector = if (isPlaying) SimpIcons.Pause else SimpIcons.PlayArrow,
            contentDescription = "Play Pause",
            tint = theme.wheelTextColor,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 22.dp)
                .size(16.dp)
        )

        // RIGHT LABEL: FAST FORWARD / NEXT ICON
        Icon(
            imageVector = SimpIcons.SkipNext,
            contentDescription = "Next",
            tint = theme.wheelTextColor,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 22.dp)
                .size(18.dp)
        )

        // LEFT LABEL: REWIND / PREVIOUS ICON
        Icon(
            imageVector = SimpIcons.SkipPrevious,
            contentDescription = "Previous",
            tint = theme.wheelTextColor,
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
                theme.centerButtonColor.copy(alpha = 0.90f)
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize(0.38f)
                .align(Alignment.Center)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(centerButtonBrush)
                .border(
                    width = 1.dp,
                    color = theme.wheelTextColor.copy(alpha = 0.20f),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
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

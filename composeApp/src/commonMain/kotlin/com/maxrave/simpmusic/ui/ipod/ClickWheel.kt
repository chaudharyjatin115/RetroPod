package com.maxrave.simpmusic.ui.ipod

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Pause
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

@Composable
fun ClickWheel(
    finish: IpodFinish,
    isPlaying: Boolean,
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
    var lastAngle by remember { mutableFloatStateOf(0f) }

    val angleStepThreshold = 18f // 18 degrees per scroll step (20 steps per rotation)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(elevation = 12.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        finish.wheelColor,
                        finish.wheelColor.copy(alpha = 0.95f),
                        finish.rimColor.copy(alpha = 0.4f)
                    )
                )
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val dx = offset.x - center.x
                        val dy = offset.y - center.y
                        lastAngle = (atan2(dy, dx) * (180f / PI.toFloat()))
                        accumulatedAngle = 0f
                    },
                    onDrag = { change, _ ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val touch = change.position
                        val distFromCenter = sqrt((touch.x - center.x) * (touch.x - center.x) + (touch.y - center.y) * (touch.y - center.y))
                        val outerRadius = size.width / 2f
                        val innerRadius = outerRadius * 0.35f

                        // Process rotary gesture if touch is in wheel ring zone
                        if (distFromCenter in innerRadius..outerRadius) {
                            val dx = touch.x - center.x
                            val dy = touch.y - center.y
                            var currentAngle = atan2(dy, dx) * (180f / PI.toFloat())

                            var delta = currentAngle - lastAngle
                            if (delta > 180f) delta -= 360f
                            if (delta < -180f) delta += 360f

                            accumulatedAngle += delta
                            lastAngle = currentAngle

                            if (accumulatedAngle >= angleStepThreshold) {
                                val steps = (accumulatedAngle / angleStepThreshold).toInt()
                                onScroll(steps)
                                accumulatedAngle %= angleStepThreshold
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            } else if (accumulatedAngle <= -angleStepThreshold) {
                                val steps = (accumulatedAngle / angleStepThreshold).toInt()
                                onScroll(steps)
                                accumulatedAngle %= angleStepThreshold
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val dx = offset.x - center.x
                    val dy = offset.y - center.y
                    val dist = sqrt(dx * dx + dy * dy)
                    val outerRadius = size.width / 2f
                    val innerRadius = outerRadius * 0.35f

                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    if (dist < innerRadius) {
                        onSelectClick()
                    } else if (dist <= outerRadius) {
                        var angle = atan2(dy, dx) * (180f / PI.toFloat())
                        if (angle < 0) angle += 360f

                        when {
                            angle in 225f..315f -> onMenuClick()          // TOP = MENU
                            angle in 45f..135f -> onPlayPauseClick()       // BOTTOM = PLAY/PAUSE
                            angle > 315f || angle < 45f -> onNextClick()   // RIGHT = NEXT/FF
                            else -> onPrevClick()                         // LEFT = PREV/REW
                        }
                    }
                }
            }
    ) {
        // Outer Button Labels
        // MENU (Top)
        Text(
            text = "MENU",
            color = finish.wheelTextColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 22.dp)
        )

        // PLAY / PAUSE (Bottom)
        Icon(
            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            contentDescription = "Play/Pause",
            tint = finish.wheelTextColor,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 22.dp)
                .size(20.dp)
        )

        // NEXT / FAST FORWARD (Right)
        Icon(
            imageVector = Icons.Rounded.FastForward,
            contentDescription = "Next",
            tint = finish.wheelTextColor,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 22.dp)
                .size(20.dp)
        )

        // PREVIOUS / REWIND (Left)
        Icon(
            imageVector = Icons.Rounded.FastRewind,
            contentDescription = "Previous",
            tint = finish.wheelTextColor,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 22.dp)
                .size(20.dp)
        )

        // CENTER SELECT BUTTON
        Box(
            modifier = Modifier
                .fillMaxSize(0.35f)
                .align(Alignment.Center)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(finish.centerButtonColor)
        )
    }
}

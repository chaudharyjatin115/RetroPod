package com.maxrave.simpmusic.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import com.maxrave.simpmusic.expect.ui.PlatformBackdrop

fun Modifier.liquidGlass(
    backdrop: PlatformBackdrop,
    shape: Shape = CircleShape,
    interactive: Boolean = true,
    highlight: Float = 0.5f,
): Modifier = this

@Composable
fun LiquidGlassContainer(
    backdrop: PlatformBackdrop,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    interactive: Boolean = true,
    highlight: Float = 0.5f,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.clip(shape).background(MaterialTheme.colorScheme.surfaceVariant),
        content = content,
    )
}

@Composable
fun LiquidGlassIconButton(
    backdrop: PlatformBackdrop,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    highlight: Float = 0.5f,
    content: @Composable BoxScope.() -> Unit,
) {
    LiquidGlassContainer(
        backdrop = backdrop,
        modifier = modifier,
        shape = shape,
        interactive = enabled,
        highlight = highlight,
        content = content,
    )
}

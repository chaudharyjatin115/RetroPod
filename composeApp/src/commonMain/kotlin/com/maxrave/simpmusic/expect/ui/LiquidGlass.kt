package com.maxrave.simpmusic.expect.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.layer.GraphicsLayer

class PlatformBackdrop

fun Modifier.layerBackdrop(backdrop: PlatformBackdrop): Modifier = this

@Composable
fun rememberBackdrop(color: Color): PlatformBackdrop = PlatformBackdrop()

fun Modifier.drawBackdropCustomShape(
    backdrop: PlatformBackdrop,
    layer: GraphicsLayer,
    luminanceAnimation: Float,
    shape: Shape,
): Modifier = this

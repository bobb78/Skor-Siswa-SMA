package com.example.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val TerminalGreen = Color(0xFF00FF66)
val TerminalGreenDim = Color(0xFF00AA44)
val TerminalGreenBright = Color(0xFF39FF14)
val TerminalDarkBg = Color(0xFF080D0A)
val TerminalBlack = Color(0xFF020403)

/**
 * Global CRT scanline effect simulating physical hardware limitations
 * with a repeating horizontal scanline pattern across the entire interface.
 */
fun Modifier.crtScanlines(
    lineSpacing: Dp = 3.dp,
    scanlineColor: Color = Color(0x15000000)
): Modifier = this.drawWithContent {
    drawContent()
    val pxSpacing = lineSpacing.toPx()
    val step = pxSpacing * 2f
    var y = 0f
    val w = size.width
    while (y < size.height) {
        drawRect(
            color = scanlineColor,
            topLeft = Offset(0f, y),
            size = Size(w, pxSpacing)
        )
        y += step
    }
}

/**
 * Utilitarian soft border
 */
fun Modifier.terminalBorder(
    width: Dp = 1.dp,
    color: Color = CardBorderLight
): Modifier = this.border(
    width = width,
    color = color,
    shape = RoundedCornerShape(10.dp)
)

/**
 * Subtle blinking underscore cursor animation to enhance the retro terminal aesthetic
 */
@Composable
fun BlinkingUnderscoreCursor(
    color: Color = TerminalGreen,
    fontSize: TextUnit = 14.sp,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "cursor_blink")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 530, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )
    Text(
        text = "_",
        color = color.copy(alpha = alpha),
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Black,
        fontSize = fontSize,
        modifier = modifier
    )
}



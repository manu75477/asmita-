package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

enum class FrameStyle(val displayName: String, val description: String) {
    GOLDEN("Golden Sparkle", "Radiant luxury gold border with stars"),
    FLORAL("Floral Garland", "Romantic floral blossoms & pastel elegance"),
    POLAROID("Polaroid Memories", "Vintage retro instant camera style"),
    CONFETTI("Confetti Fiesta", "Vibrant festive streamers & celebration confetti"),
    NEON("Neon Radiance", "Futuristic vibrant neon birthday glow"),
    LOVE("Love & Bestie", "Sweet friendship hearts & rose blush")
}

@Composable
fun CustomPhotoFrame(
    frameStyle: FrameStyle,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    when (frameStyle) {
        FrameStyle.GOLDEN -> GoldenFrame(modifier, content)
        FrameStyle.FLORAL -> FloralFrame(modifier, content)
        FrameStyle.POLAROID -> PolaroidFrame(modifier, content)
        FrameStyle.CONFETTI -> ConfettiFrame(modifier, content)
        FrameStyle.NEON -> NeonFrame(modifier, content)
        FrameStyle.LOVE -> LoveFrame(modifier, content)
    }
}

@Composable
fun GoldenFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val goldGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFD700),
            Color(0xFFFFA000),
            Color(0xFFFFF8E1),
            Color(0xFFFFC107),
            Color(0xFFFF8F00)
        )
    )

    Box(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(16.dp))
            .background(goldGradient, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1428), RoundedCornerShape(10.dp))
                .border(2.dp, Color(0xFFFFD54F), RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            content()
            // Corner Star Decors
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gold = Color(0xFFFFD700)
                // Draw stars at corners
                drawCircle(gold, radius = 6f, center = Offset(16f, 16f))
                drawCircle(gold, radius = 6f, center = Offset(size.width - 16f, 16f))
                drawCircle(gold, radius = 6f, center = Offset(16f, size.height - 16f))
                drawCircle(gold, radius = 6f, center = Offset(size.width - 16f, size.height - 16f))
            }
        }
    }
}

@Composable
fun FloralFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val floralGradient = Brush.linearGradient(
        listOf(Color(0xFFF8BBD0), Color(0xFFE1BEE7), Color(0xFFFFCDD2))
    )

    Box(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(20.dp))
            .background(floralGradient, RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFCF7F9), RoundedCornerShape(12.dp))
                .border(2.dp, Color(0xFFF48FB1), RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            content()
            // Floral corner petals
            Canvas(modifier = Modifier.fillMaxSize()) {
                val petalColor = Color(0xFFE91E63)
                drawCircle(petalColor, radius = 8f, center = Offset(16f, 16f))
                drawCircle(Color(0xFFFF4081), radius = 5f, center = Offset(24f, 16f))
                drawCircle(Color(0xFFFF4081), radius = 5f, center = Offset(16f, 24f))

                drawCircle(petalColor, radius = 8f, center = Offset(size.width - 16f, 16f))
                drawCircle(Color(0xFFFF4081), radius = 5f, center = Offset(size.width - 24f, 16f))
                drawCircle(Color(0xFFFF4081), radius = 5f, center = Offset(size.width - 16f, 24f))
            }
        }
    }
}

@Composable
fun PolaroidFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(14.dp, RoundedCornerShape(4.dp))
            .background(Color(0xFFFAF9F6), RoundedCornerShape(4.dp))
            .padding(start = 14.dp, top = 14.dp, end = 14.dp, bottom = 44.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF212121), RoundedCornerShape(2.dp))
                .clip(RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun ConfettiFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val rainbowGradient = Brush.sweepGradient(
        listOf(
            Color(0xFFFF5252),
            Color(0xFFFFD740),
            Color(0xFF69F0AE),
            Color(0xFF40C4FF),
            Color(0xFFE040FB),
            Color(0xFFFF5252)
        )
    )

    Box(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(18.dp))
            .background(rainbowGradient, RoundedCornerShape(18.dp))
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF181528), RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            content()
            Canvas(modifier = Modifier.fillMaxSize()) {
                val dots = listOf(
                    Offset(20f, 20f) to Color(0xFFFF5252),
                    Offset(size.width - 20f, 20f) to Color(0xFFFFD740),
                    Offset(size.width / 2f, 12f) to Color(0xFF69F0AE),
                    Offset(20f, size.height - 20f) to Color(0xFF40C4FF),
                    Offset(size.width - 20f, size.height - 20f) to Color(0xFFE040FB)
                )
                dots.forEach { (pos, color) ->
                    drawCircle(color, radius = 5f, center = pos)
                }
            }
        }
    }
}

@Composable
fun NeonFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val neonBrush = Brush.linearGradient(
        listOf(Color(0xFF00E5FF), Color(0xFFD500F9), Color(0xFF00E5FF))
    )

    Box(
        modifier = modifier
            .shadow(16.dp, RoundedCornerShape(16.dp))
            .background(Color(0xFF0B0A12), RoundedCornerShape(16.dp))
            .border(3.dp, neonBrush, RoundedCornerShape(16.dp))
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF141224), RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun LoveFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val loveGradient = Brush.verticalGradient(
        listOf(Color(0xFFFF4081), Color(0xFFF50057), Color(0xFFC51162))
    )

    Box(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .background(loveGradient, RoundedCornerShape(20.dp))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF0F5), RoundedCornerShape(12.dp))
                .border(2.dp, Color(0xFFFF80AB), RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

data class ConfettiParticle(
    val xRatio: Float,
    val initialY: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    val isCircle: Boolean
)

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    particleCount: Int = 45,
    onFinished: () -> Unit = {}
) {
    val progress = remember { Animatable(0f) }

    val colors = listOf(
        Color(0xFFFF1744),
        Color(0xFFFFEA00),
        Color(0xFF00E676),
        Color(0xFF2979FF),
        Color(0xFFFF4081),
        Color(0xFF7C4DFF),
        Color(0xFFFF9100)
    )

    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                xRatio = Random.nextFloat(),
                initialY = Random.nextFloat() * -300f,
                speed = Random.nextFloat() * 800f + 600f,
                size = Random.nextFloat() * 14f + 8f,
                color = colors[Random.nextInt(colors.size)],
                rotationSpeed = Random.nextFloat() * 720f - 360f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3200, easing = LinearEasing)
        )
        onFinished()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles.forEach { p ->
            val curY = p.initialY + (p.speed * progress.value)
            val curX = (p.xRatio * width) + (kotlin.math.sin((progress.value * 10f + p.xRatio * 5f).toDouble()).toFloat() * 30f)
            val currentRotation = progress.value * p.rotationSpeed

            if (curY in -50f..height + 50f) {
                rotate(degrees = currentRotation, pivot = Offset(curX, curY)) {
                    if (p.isCircle) {
                        drawCircle(
                            color = p.color,
                            radius = p.size / 2,
                            center = Offset(curX, curY)
                        )
                    } else {
                        drawRect(
                            color = p.color,
                            topLeft = Offset(curX - p.size / 2, curY - p.size),
                            size = Size(p.size, p.size * 1.6f)
                        )
                    }
                }
            }
        }
    }
}

package eternal.future.tefmodloader.easteregg

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

object Screen {
    @Composable
    fun Rotation(content: @Composable () -> Unit) {
        val rotationState = remember { mutableStateOf(180f) }
        Box(
            modifier = Modifier
                .rotate(rotationState.value)
                .then(
                    if (rotationState.value == 180f) {
                        Modifier.padding(bottom = with(LocalDensity.current) { 100.dp })
                    } else {
                        Modifier
                    }
                )
        ) {
            content()
        }
    }

    @Composable
    fun ClockwiseRotatingContent(durationMillis: Int, content: @Composable () -> Unit) {
        var angle by remember { mutableStateOf(0f) }

        if (durationMillis == -1) {
            val infiniteTransition = rememberInfiniteTransition()
            val infiniteAngle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 5000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            angle = infiniteAngle
        } else {
            val rotationState = remember { Animatable(initialValue = 0f) }

            LaunchedEffect(key1 = rotationState) {
                while (rotationState.value < 360f) {
                    rotationState.snapTo(rotationState.value + 1f)
                    delay((durationMillis / 360).toLong())
                }
            }

            angle = rotationState.value
        }

        Box(
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = angle
                }
        ) {
            content()
        }
    }

    @Composable
    fun CounterClockwiseRotatingContent(durationMillis: Int, content: @Composable () -> Unit) {
        var angle by remember { mutableStateOf(0f) }

        if (durationMillis == -1) {
            val infiniteTransition = rememberInfiniteTransition()
            val infiniteAngle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 5000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            angle = infiniteAngle
        } else {
            val rotationState = remember { Animatable(initialValue = 0f) }

            LaunchedEffect(key1 = rotationState) {
                while (rotationState.value > -360f) {
                    rotationState.snapTo(rotationState.value - 1f)
                    delay((durationMillis / 360).toLong())
                }
            }

            angle = rotationState.value
        }

        Box(
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = angle
                }
        ) {
            content()
        }
    }

    @Composable
    fun BouncingContent(durationMillis: Int, content: @Composable () -> Unit) {
        val infiniteTransition = rememberInfiniteTransition()
        val duration = if (durationMillis == -1) Long.MAX_VALUE else durationMillis.toLong()

        val offsetY by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 50f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    0f at 0 with LinearEasing
                    50f at (duration / 2).toInt() with LinearEasing
                    0f at duration.toInt() with LinearEasing
                },
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationY = offsetY
                }
        ) {
            content()
        }
    }
}
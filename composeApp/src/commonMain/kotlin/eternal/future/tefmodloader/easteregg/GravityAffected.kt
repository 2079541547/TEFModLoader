package eternal.future.tefmodloader.easteregg

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.sign

@Composable
fun GravityAffectedContent(
    modifier: Modifier = Modifier,
    containerWidth: Float = 500f, // 默认宽度
    containerHeight: Float = 500f, // 默认高度
    gravity: Float = 9.8f,
    elasticity: Float = 0.7f, // 弹性系数
    frictionCoefficient: Float = 0.1f, // 摩擦系数，用于计算空气阻力
    mass: Float = 1f, // 质量
    velocityDecay: Float = 0.95f, // 碰撞后速度的衰减因子 (0 < decay < 1)
    maxVelocity: Float = 20f, // 最大速度限制
    dragSensitivity: Float = 0.05f, // 拖动灵敏度因子
    initialPositionX: Float = 500f,
    initialPositionY: Float = 500f,
    content: @Composable () -> Unit
) {
    var time by remember { mutableStateOf(0f) }
    var velocityY by remember { mutableStateOf(0f) }
    var positionY by remember { mutableStateOf(initialPositionY) }
    var velocityX by remember { mutableStateOf(0f) }
    var positionX by remember { mutableStateOf(initialPositionX) }

    val offsetY by animateFloatAsState(targetValue = positionY)
    val offsetX by animateFloatAsState(targetValue = positionX)

    LaunchedEffect(key1 = true) {
        while (true) {
            delay(16L)
            time += 0.016f

            val gravitationalForce = gravity * mass
            val dragForceY = -frictionCoefficient * velocityY.sign * velocityY * velocityY
            val netForceY = gravitationalForce + dragForceY
            val accelerationY = netForceY / mass

            velocityY += accelerationY * 0.016f
            positionY += velocityY * 0.016f

            velocityX *= (1 - frictionCoefficient * 0.016f)

            if (positionY > 0) {
                positionY = 0f
                velocityY = -(velocityY * elasticity).coerceAtLeast(-0.1f)
                velocityY *= velocityDecay
                if (abs(velocityY) < 0.1f) {
                    velocityY = 0f
                }
            } else if (positionY < -containerHeight + 100) {
                positionY = -containerHeight + 100f
                velocityY = -(velocityY * elasticity).coerceAtLeast(-0.1f)
                velocityY *= velocityDecay
                if (abs(velocityY) < 0.1f) {
                    velocityY = 0f
                }
            }

            if (positionX < 0) {
                positionX = 0f
                velocityX = -(velocityX * elasticity).coerceAtLeast(-0.1f)
                velocityX *= velocityDecay
                if (abs(velocityX) < 0.1f) {
                    velocityX = 0f
                }
            } else if (positionX > containerWidth - 100) {
                positionX = containerWidth - 100f
                velocityX = -(velocityX * elasticity).coerceAtLeast(-0.1f)
                velocityX *= velocityDecay
                if (abs(velocityX) < 0.1f) {
                    velocityX = 0f
                }
            }

            positionX += velocityX * 0.016f
        }
    }

    Box(
        modifier = modifier
            .size(containerWidth.dp, containerHeight.dp)
    ) {
        Box(
            modifier = Modifier
                .offset(x = offsetX.dp, y = offsetY.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {},
                        onDragEnd = {
                            if (positionY > 0) {
                                positionY = 0f
                                velocityY = 0f
                            } else if (positionY < -containerHeight + 100) {
                                positionY = -containerHeight + 100f
                                velocityY = 0f
                            }

                            if (positionX < 0) {
                                positionX = 0f
                                velocityX = 0f
                            } else if (positionX > containerWidth - 100) {
                                positionX = containerWidth - 100f
                                velocityX = 0f
                            }
                        },
                        onDragCancel = {},
                        onDrag = { change, dragAmount ->
                            change.consume()

                            val dragForceX = dragAmount.x * dragSensitivity
                            val dragForceY = dragAmount.y * dragSensitivity

                            velocityX += dragForceX / mass
                            velocityY += dragForceY / mass

                            velocityX = velocityX.coerceIn(-maxVelocity, maxVelocity)
                            velocityY = velocityY.coerceIn(-maxVelocity, maxVelocity)

                            positionX += dragAmount.x / 4
                            positionY += dragAmount.y / 4

                            // 边界检查
                            if (positionY > 0) {
                                positionY = 0f
                                velocityY = 0f
                            } else if (positionY < -containerHeight + 100) {
                                positionY = -containerHeight + 100f
                                velocityY = 0f
                            }

                            if (positionX < 0) {
                                positionX = 0f
                                velocityX = 0f
                            } else if (positionX > containerWidth - 100) {
                                positionX = containerWidth - 100f
                                velocityX = 0f
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}
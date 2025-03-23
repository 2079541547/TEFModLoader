package eternal.future.tefmodloader.ui.screen.welcome

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import eternal.future.tefmodloader.easteregg.GravityAffectedContent
import tefmodloader.composeapp.generated.resources.Res
import tefmodloader.composeapp.generated.resources.tefmodloader


@Composable
fun welcomeScreen(onAnimationEnd: () -> Unit) {
    val usePhysics = remember { (0..99).random() == 0 }
    var isAnimationDone by remember { mutableStateOf(false) }

    val scale = remember { Animatable(initialValue = 0.5f) }
    val alpha = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = LinearEasing)
            )
            if (isAnimationDone) onAnimationEnd()
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 200, delayMillis = 200, easing = FastOutLinearInEasing)
            )
            isAnimationDone = true
            if (scale.isRunning.not()) onAnimationEnd()
        }
    }
        if (usePhysics) {
            Scaffold {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        GravityAffectedContent(
                            gravity = 1000f,
                            mass = 420f,
                            elasticity = 5f,
                            containerWidth = 200f,
                            containerHeight= 220f
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.tefmodloader),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(200.dp)
                            )
                        }
                    }
                }
            }
        } else {
            Scaffold {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.tefmodloader),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .scale(scale.value)
                                .alpha(alpha.value)
                        )
                    }
                }
            }
        }
}
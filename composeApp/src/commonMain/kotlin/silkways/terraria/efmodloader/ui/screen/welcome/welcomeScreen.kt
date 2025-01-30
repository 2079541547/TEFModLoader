package silkways.terraria.efmodloader.ui.screen.welcome

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import tefmodloader.composeapp.generated.resources.Res
import tefmodloader.composeapp.generated.resources.compose_multiplatform


@Composable
fun welcomeScreen(onAnimationEnd: () -> Unit) {
    val scale = remember { Animatable(initialValue = 0.5f) }
    val alpha = remember { Animatable(initialValue = 0f) }
    var isScaleAnimationDone by remember { mutableStateOf(false) }
    var isAlphaAnimationDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = LinearEasing)
            )
            isScaleAnimationDone = true
            if (isAlphaAnimationDone) onAnimationEnd()
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 200, delayMillis = 200, easing = FastOutLinearInEasing)
            )
            isAlphaAnimationDone = true
            if (isScaleAnimationDone) onAnimationEnd()
        }
    }

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
                    painter = painterResource(Res.drawable.compose_multiplatform),
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
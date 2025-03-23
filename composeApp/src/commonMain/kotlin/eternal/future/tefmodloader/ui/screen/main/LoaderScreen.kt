package eternal.future.tefmodloader.ui.screen.main

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InstallDesktop
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import eternal.future.tefmodloader.State
import eternal.future.tefmodloader.data.EFModLoader
import eternal.future.tefmodloader.ui.widget.main.LoaderScreen
import eternal.future.tefmodloader.utility.Locales
import kotlin.math.roundToInt

object LoaderScreen {

    var loaders = mutableStateOf(listOf<EFModLoader>())
    val locale = Locales()

    @Composable
    fun LoaderScreen_r(
        installOnBack: () -> Unit
    ) {

        loaders.value = eternal.future.tefmodloader.utility.EFModLoader.loadLoadersFromDirectory(State.EFModLoaderPath)

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { _ ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopStart
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(loaders.value.size) { index ->
                        val loader = loaders.value[index]
                        LoaderScreen.LoaderCard(loader)
                    }
                }

                var offsetX by remember { mutableStateOf(0f) }
                var offsetY by remember { mutableStateOf(0f) }

                ExtendedFloatingActionButton(
                    text = { Text(locale.getString("install")) },
                    icon = { Icon(Icons.Default.InstallDesktop, contentDescription = "Install Loader") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = installOnBack,
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                offsetX.roundToInt(),
                                offsetY.roundToInt()
                            )
                        }
                        .align(Alignment.BottomEnd)
                        .pointerInput(Unit) {
                            detectDragGestures { _, dragAmount ->
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        }
                        .padding(20.dp)
                )
            }
        }
    }
}

@Composable
expect fun eternal.future.tefmodloader.ui.screen.main.LoaderScreen.LoaderScreen()
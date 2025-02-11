package eternal.future.efmodloader.ui.screen.main

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
import eternal.future.efmodloader.State
import eternal.future.efmodloader.data.EFMod
import kotlin.math.roundToInt

object EFModScreen {

    var mods = mutableStateOf(listOf<EFMod>())

    @Composable
    fun EFModScreen_r(
        onBack: () -> Unit
    ) {

        mods.value = eternal.future.efmodloader.utility.EFMod.loadModsFromDirectory(State.EFModPath)

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
                    items(mods.value.size) { index ->
                        val mod = mods.value[index]
                        eternal.future.efmodloader.ui.widget.main.EFModScreen.EFModCard(mod)
                    }
                }

                var offsetX by remember { mutableStateOf(0f) }
                var offsetY by remember { mutableStateOf(0f) }

                ExtendedFloatingActionButton(
                    text = { Text("Install EFMod") },
                    icon = { Icon(Icons.Default.InstallDesktop, contentDescription = "Install EFMod") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = onBack,
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
expect fun EFModScreen.EFModScreen()
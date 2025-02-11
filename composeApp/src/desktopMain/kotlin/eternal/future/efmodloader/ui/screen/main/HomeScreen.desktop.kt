package eternal.future.efmodloader.ui.screen.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eternal.future.efmodloader.State.loaderNumber
import eternal.future.efmodloader.State.selectedPath
import eternal.future.efmodloader.ui.widget.main.HomeScreen


actual object HomeScreen {
    @Composable
    actual fun HomeScreen() {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { _ ->
            Box(
                contentAlignment = Alignment.TopStart
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        if (selectedPath.value == "") {
                            eternal.future.efmodloader.ui.widget.main.HomeScreen.stateCard(
                                title = "Unable to find the game",
                                description = "If you don't select the game folder, you won't be able to use any features about mods",
                                isActive = false,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {

                                }
                            )
                        } else if (loaderNumber.value == 0) {
                            eternal.future.efmodloader.ui.widget.main.HomeScreen.stateCard(
                                title = "No loader",
                                description = "You won't be able to use mods!",
                                isActive = false,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {

                                }
                            )
                        } else {
                            eternal.future.efmodloader.ui.widget.main.HomeScreen.stateCard(
                                title = "Activated",
                                description = "Version: 1.4.4.9\nPath: ${selectedPath.value}\nNumber of mods enabled: 0\nNumber of loaders: 0",
                                isActive = true,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {

                                }
                            )
                        }
                    }

                    item {
                        eternal.future.efmodloader.ui.widget.main.HomeScreen.updateLogCard(
                            title = "Changelog",
                            confirmButton = "close",
                            modifier = Modifier.fillMaxWidth().padding(15.dp),
                            data = listOf(
                                HomeScreen.UpdateLogData(
                                    versionTitle = "v1.0.0.0",
                                    content = "Updated content"
                                )
                            ),
                            onClick = {}
                        )
                    }
                }

                if (selectedPath.value != "") {
                    val fabXOffset: Dp by animateDpAsState(
                        targetValue = 0.dp,
                        animationSpec = tween(durationMillis = 300)
                    )
                    var dragOffset by remember { mutableStateOf(0f) }

                    ExtendedFloatingActionButton(
                        text = { Text("Launch the game") },
                        icon = {
                            Icon(
                                Icons.Default.Games,
                                contentDescription = "Launch the game"
                            )
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        onClick = { /*launch()*/ },
                        modifier = Modifier
                            .offset(x = with(LocalDensity.current) { (fabXOffset.value + dragOffset).toDp() })
                            .align(Alignment.BottomEnd)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    dragOffset += dragAmount.x
                                    change.consume()
                                }
                            }
                            .graphicsLayer(
                                translationX = dragOffset
                            )
                            .padding(20.dp)
                    )
                }
            }
        }
    }
}
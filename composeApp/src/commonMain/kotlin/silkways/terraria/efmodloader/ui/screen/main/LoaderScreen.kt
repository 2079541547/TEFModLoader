package silkways.terraria.efmodloader.ui.screen.main

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
import silkways.terraria.efmodloader.data.EFModLoader
import silkways.terraria.efmodloader.ui.widget.main.LoaderScreen
import kotlin.math.roundToInt

object LoaderScreen {
    @Composable
    fun LoaderScreen() {
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

                    item {
                        LoaderScreen.LoaderCard(
                            loader = EFModLoader(
                                info = EFModLoader.LoaderInfo(
                                    name = "Name",
                                    author = "EternalFuture",
                                    version = "v1.0.0",
                                    introduce = "Hello",
                                    github = EFModLoader.GithubInfo(
                                        overview = "",
                                        url = "TODO()"
                                    ),
                                    loader = EFModLoader.LoaderDetails(
                                        platform = EFModLoader.PlatformSupport(
                                            Windows = EFModLoader.PlatformArchitectures(
                                                arm64 = true,
                                                arm32 = true,
                                                x86_64 = true,
                                                x86_32 = true
                                            ),
                                            Android = EFModLoader.PlatformArchitectures(
                                                arm64 = true,
                                                arm32 = true,
                                                x86_64 = true,
                                                x86_32 = true
                                            )
                                        )
                                    )
                                ),
                                filePath = "TODO()",
                                icon = null,
                                isSelected = true
                            ),
                            onEnabledChange = {a, b ->  },
                            onRemoveClick = {  },
                            onUpdateModClick = {  }
                        )
                    }

                }

                var offsetX by remember { mutableStateOf(0f) }
                var offsetY by remember { mutableStateOf(0f) }

                ExtendedFloatingActionButton(
                    text = { Text("Install EFModLoader") },
                    icon = { Icon(Icons.Default.InstallDesktop, contentDescription = "Install EFMod") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = { /* Handle click */ },
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
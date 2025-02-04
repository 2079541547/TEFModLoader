package silkways.terraria.efmodloader.ui.screen.welcome

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import silkways.terraria.efmodloader.MainActivity
import silkways.terraria.efmodloader.ui.widget.main.SettingScreen
import java.io.File
import java.io.FileOutputStream
import java.nio.file.FileAlreadyExistsException


@Composable
actual fun GuideScreen.disposition() {

    showNext_disposition.value = true

    SettingScreen.ModernCheckBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        title = "Auto-provisioning",
        contentDescription = "Automatically selects the best patching scenario",
        isChecked = autoPatch.value,
        onCheckedChange = { check ->
            autoPatch.value = check
        },
        icon = Icons.Default.AutoFixNormal
    )
}

private var apkPath: String = ""

@Composable
actual fun GuideScreen.disposition_2() {

    val selectFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { url ->
        copyAndPath(url)
    }


    val ModeMap = mapOf(
        0 to "Exterior",
        1 to "Share",
        2 to "Inline",
        3 to "Root(risky)",
    )

    val killerMap = mapOf(
        0 to "None",
        1 to "MT Manager",
        2 to "LSPatch"
    )

    var showSelector by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        Column {
            SettingScreen.Selector(
                title = "Select Mode",
                defaultSelectorId = 0,
                ModeMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                onClick = { select ->
                    showSelector = select != 3
                }
            )

            if (showSelector) {
                SettingScreen.ModernCheckBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    title = "Override the version code",
                    contentDescription = "Convenient downgrade operation",
                    isChecked = OverrideVersion.value,
                    onCheckedChange = { select ->
                        OverrideVersion.value = select
                    }
                )

                SettingScreen.ModernCheckBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    title = "Debugging",
                    contentDescription = "Install package debugging",
                    isChecked = Debugging.value,
                    onCheckedChange = { select ->
                        Debugging.value = select
                    }
                )

                SettingScreen.Selector(
                    title = "Signature Killer",
                    defaultSelectorId = 0,
                    killerMap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    onClick = { select ->
                        SignatureKiller.value = select
                    }
                )


                Text(
                    "If you don't want to patch the fixation package",
                    modifier = Modifier.padding(10.dp)
                )

                SettingScreen.PathInputWithFilePicker(
                    title = "Select an APK",
                    path = ApkPath.value,
                    onPathChange = {},
                    onFolderSelect = {
                        selectFileLauncher.launch("application/vnd.android.package-archive")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
        }

        val fabXOffset: Dp by animateDpAsState(
            targetValue = 0.dp,
            animationSpec = tween(durationMillis = 300)
        )
        var dragOffset by remember { mutableFloatStateOf(0f) }

        ExtendedFloatingActionButton(
            text = { Text(locales.getString("next")) },
            icon = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next") },
            containerColor = MaterialTheme.colorScheme.primary,
            onClick = { viewModel.navigateTo("agreement") },
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

private fun copyAndPath(url: Uri?) {
    url?.let {
        apkPath = it.path.toString()
        MainActivity.getContext().contentResolver.openInputStream(it)?.use { inputStream ->
            File(MainActivity.getContext().getExternalFilesDir(null), "patch/game.apk").let {
                it.parentFile?.mkdirs()
                FileOutputStream(it).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }
}
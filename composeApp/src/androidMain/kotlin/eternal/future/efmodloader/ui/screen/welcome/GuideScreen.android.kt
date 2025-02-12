package eternal.future.efmodloader.ui.screen.welcome

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
import eternal.future.efmodloader.State
import eternal.future.efmodloader.State.ApkPath
import eternal.future.efmodloader.State.Debugging
import eternal.future.efmodloader.State.Mode
import eternal.future.efmodloader.State.OverrideVersion
import eternal.future.efmodloader.State.SignatureKiller
import eternal.future.efmodloader.State.autoPatch
import eternal.future.efmodloader.State.gamePack
import eternal.future.efmodloader.configuration
import eternal.future.efmodloader.ui.widget.main.SettingScreen
import eternal.future.efmodloader.utility.Locales


@Composable
actual fun GuideScreen.disposition() {

    showNext_disposition.value = true

    SettingScreen.ModernCheckBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        title = disposition.getString("auto_provisioning"),
        contentDescription = disposition.getString("auto_provisioning_content"),
        isChecked = autoPatch.value,
        onCheckedChange = { check ->
            autoPatch.value = check
        },
        icon = Icons.Default.AutoFixNormal
    )
}


@Composable
actual fun GuideScreen.disposition_2() {

    val disposition = Locales()
    disposition.loadLocalization("Screen/GuideScreen/disposition_2.toml", Locales.getLanguage(State.language.value))

    val selectFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { url ->
        url?.let {
            ApkPath.value = it.toString()
        }
    }

    val ModeMap = mapOf(
        0 to disposition.getString("external"),
        // 1 to disposition.getString("share"),
        // 2 to disposition.getString("inline"),
        // 3 to disposition.getString("root"),
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
                defaultSelectorId = SignatureKiller.value,
                ModeMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                onClick = { select ->
                    Mode.value = select
                    showSelector = Mode.value != 3 && Mode.value != 2
                    configuration.setInt("Mode", select)
                }
            )

            if (showSelector) {
                SettingScreen.ModernCheckBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    title = disposition.getString("override"),
                    contentDescription = disposition.getString("override_content"),
                    isChecked = OverrideVersion.value,
                    onCheckedChange = { select ->
                        OverrideVersion.value = select
                    }
                )

                SettingScreen.ModernCheckBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    title = disposition.getString("coexistence"),
                    contentDescription = disposition.getString("coexistence_content"),
                    isChecked = gamePack.value,
                    onCheckedChange = { select ->
                        gamePack.value = select
                    }
                )

                SettingScreen.ModernCheckBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    title = disposition.getString("debug"),
                    contentDescription = disposition.getString("debug_content"),
                    isChecked = Debugging.value,
                    onCheckedChange = { select ->
                        Debugging.value = select
                    }
                )


                /*
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
                 */


                Text(
                    disposition.getString("select_apk_content"),
                    modifier = Modifier.padding(10.dp)
                )

                SettingScreen.PathInputWithFilePicker(
                    title = disposition.getString("select_apk"),
                    path = ApkPath.value.toString(),
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
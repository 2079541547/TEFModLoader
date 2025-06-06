package eternal.future.tefmodloader.ui.screen.welcome

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import eternal.future.tefmodloader.State
import eternal.future.tefmodloader.State.ApkPath
import eternal.future.tefmodloader.State.Debugging
import eternal.future.tefmodloader.State.Mode
import eternal.future.tefmodloader.State.OverrideVersion
import eternal.future.tefmodloader.State.autoPatch
import eternal.future.tefmodloader.State.isBypass
import eternal.future.tefmodloader.ui.navigation.NavigationViewModel
import eternal.future.tefmodloader.ui.widget.main.SettingScreen
import eternal.future.tefmodloader.utility.Locales
import kotlin.math.roundToInt


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
fun GuideScreen.Patch() {
    val selectFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { url ->
        url?.let {
            ApkPath.value = it.toString()
        }
    }
    var showSelector by remember { mutableStateOf(true) }

    val disposition = Locales()
    disposition.loadLocalization("Screen/GuideScreen/disposition_2.toml", Locales.getLanguage(State.language.value))

    val ModeMap = mapOf(
        0 to disposition.getString("external"),
        // 1 to disposition.getString("share"),
        // 2 to disposition.getString("inline"),
        // 3 to disposition.getString("root"),
    )

    LazyColumn {
        item {
            SettingScreen.Selector(
                title = disposition.getString("select_mode"),
                defaultSelectorId = Mode.value,
                ModeMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                onClick = { select ->
                    Mode.value = select
                    showSelector = Mode.value != 3 && Mode.value != 2
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
                    title = disposition.getString("bypass"),
                    contentDescription = disposition.getString("bypass_content"),
                    isChecked = isBypass.value,
                    onCheckedChange = { select ->
                        isBypass.value = select
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

                Text(
                    disposition.getString("select_apk_content"),
                    modifier = Modifier.padding(10.dp)
                )

                SettingScreen.GeneralTextInput(
                    title = disposition.getString("select_apk"),
                    value = ApkPath.value,
                    onValueChange = {},
                    trailingIcon = {
                        IconButton(onClick = {
                            selectFileLauncher.launch("application/vnd.android.package-archive")
                        }) {
                            Icon(Icons.Default.Folder, contentDescription = "选择文件夹")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
        }
    }
}

@Composable
actual fun GuideScreen.disposition_2(mainViewModel: NavigationViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        Column {
            GuideScreen.Patch()
        }
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }
        val scope = rememberCoroutineScope()
        ExtendedFloatingActionButton(
            text = { Text(locales.getString("next")) },
            icon = { Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Next") },
            containerColor = MaterialTheme.colorScheme.primary,
            onClick = {
                start(scope, mainViewModel)
            },
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
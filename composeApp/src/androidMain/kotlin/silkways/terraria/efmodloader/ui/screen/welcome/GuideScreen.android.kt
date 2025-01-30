package silkways.terraria.efmodloader.ui.screen.welcome

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import silkways.terraria.efmodloader.ui.screen.welcome.GuideScreen.autoPatch
import silkways.terraria.efmodloader.ui.widget.main.SettingScreen
import silkways.terraria.efmodloader.ui.screen.welcome.GuideScreen.showNext_disposition


@Composable
actual fun disposition() {

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
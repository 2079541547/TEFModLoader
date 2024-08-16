package silkways.terraria.toolbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import silkways.terraria.toolbox.databinding.WelcomeMainBinding
import silkways.terraria.toolbox.logic.mod.ModJsonManager


class Welcome : AppCompatActivity() {

    private lateinit var binding: WelcomeMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WelcomeMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ModJsonManager.JSON_adjustment("${this.getExternalFilesDir(null)}/ToolBoxData/ModData/mod_data.json")
        //ModJsonManager.removeEntriesByAuthorAndModName("${this.getExternalFilesDir(null)}/ToolBoxData/ModData/mod_data.json", "Name", "MyMod")
        //ModJsonManager.updateEnableByAuthorAndModName("${this.getExternalFilesDir(null)}/ToolBoxData/ModData/mod_data.json", "Name", "MyMod", true)
        ModJsonManager.modifyArrays("${this.getExternalFilesDir(null)}/ToolBoxData/ModData/mod_data.json", "Name", "MyMod", "Assembly-CSharp.Terraria.Main.DamageVar", 1)
    }


}

package silkways.terraria.efmodloader.ui.fragment.manage.mod

data class ModDetail(
    val author: String,
    val modName: String,
    val build: Int,
    val modIntroduce: String,
    val enable: Boolean,
    val functions: List<FunctionHook>,
    val libname: String,
    val Opencode: Boolean,
    val OpencodeUrl: String
)

data class FunctionHook(
    val position: String,
    val functions: List<String>,
    val type: String,
    val arrays: Int
)
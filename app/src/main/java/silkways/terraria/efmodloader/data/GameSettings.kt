package silkways.terraria.efmodloader.data

object GameSettings {
    val jsonPath = "ToolBoxData/game_settings.json"

    //键
    val suspended_window = "suspended_window"
    val debug = "debug"
    val savePath = "savePath"
    val worldPath = "worldPath"

    //json数据
    val Data = mapOf(
        suspended_window to false,
        debug to false
    )
}
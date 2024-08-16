package silkways.terraria.toolbox.data

object GameSettings {
    val jsonPath = "ToolBoxData/game_settings.json"

    //键
    val suspended_window = "suspended_window"
    val debug = "debug"

    //json数据
    val Data = mapOf(
        suspended_window to false,
        debug to false,
    )
}
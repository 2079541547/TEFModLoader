package silkways.terraria.toolbox.data

object Settings {
    val jsonPath = "ToolBoxData/settings.json"

    //键
    val themeKey = "theme" //主题
    val languageKey = "language" //语言
    val agreement = "agreement" //是否同意协议
    val autoClean = "autoClean" //是否自动清除缓存
    val CleanDialog = "CleanDialog" //是否弹出清除缓存弹窗
    val autoUpdateApk = "autoUpdateApk" //自动更新apk
    val CoveringFiles = "CoveringFiles" //文件存在时是否覆盖


    //json数据
    val Data = mapOf(
        themeKey to 0,
        languageKey to 0,
        agreement to false,
        autoClean to false,
        CleanDialog to true,
        autoUpdateApk to true,
        CoveringFiles to false,
    )
}
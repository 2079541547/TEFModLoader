package eternal.future.tefmodloader.utility

actual object Net {
    actual fun openUrlInBrowser(url: String) {
        try {
            when (val osName = System.getProperty("os.name").lowercase()) {
                "linux" -> ProcessBuilder("xdg-open", url).start()
                "mac os x" -> ProcessBuilder("open", url).start()
                "windows" -> ProcessBuilder("cmd", "/c", "start", url).start()
                else -> throw RuntimeException("Unsupported operating system: $osName")
            }
        } catch (e: Exception) {
            EFLog.e("无法打开链接: $url, 错误: ", e)
        }
    }
}
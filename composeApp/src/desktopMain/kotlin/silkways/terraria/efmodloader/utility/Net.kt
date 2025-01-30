package silkways.terraria.efmodloader.utility

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
            e.printStackTrace()
            println("无法打开URL: $url")
        }
    }
}
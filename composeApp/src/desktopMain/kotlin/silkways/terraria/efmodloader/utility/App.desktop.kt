package silkways.terraria.efmodloader.utility

import silkways.terraria.efmodloader.exitApp

actual object App {
    actual fun exit() {
        exitApp()
    }

    actual fun getCurrentArchitecture(): String {
        val osName = System.getProperty("os.name")
        val arch = System.getProperty("os.arch")

        return when {
            osName.contains("win") -> {
                if (arch == "amd64" || arch == "x86_64") "x86_64" else "x86"
            }
            osName.contains("linux") -> {
                when {
                    arch.contains("64") -> "x86_64"
                    arch.contains("arm64") || arch.contains("arm64-v8a") -> "arm64"
                    else -> "x86"
                }
            }
            else -> "x86_64"
        }
    }

    actual fun getPrivate(): String {
        return when {
            System.getProperty("os.name").contains("win") -> (System.getenv("LOCALAPPDATA") + "\\TEFModLoader")
            else -> (System.getProperty("user.home") + "/TEFModLoader")
        }
    }
}
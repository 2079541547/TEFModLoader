package eternal.future.efmodloader

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
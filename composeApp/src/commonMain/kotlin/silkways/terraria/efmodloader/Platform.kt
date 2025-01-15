package silkways.terraria.efmodloader

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
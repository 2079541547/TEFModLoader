package eternal.future.efmodloader.utility

expect object App {
    fun exit()
    fun getCurrentArchitecture(): String
    fun getPrivate(): String
}
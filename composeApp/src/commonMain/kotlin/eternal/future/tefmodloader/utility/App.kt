package eternal.future.tefmodloader.utility

expect object App {
    fun exit()
    fun getCurrentArchitecture(): String
    fun getPrivate(): String
}
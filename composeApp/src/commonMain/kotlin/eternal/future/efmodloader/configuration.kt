package eternal.future.efmodloader

expect object configuration {
    fun getString(key: String, default: String = ""): String
    fun setString(key: String, value: String)

    fun getBoolean(key: String, default: Boolean = false): Boolean
    fun setBoolean(key: String, value: Boolean)

    fun getInt(key: String, default: Int = 0): Int
    fun setInt(key: String, value: Int)
}
package silkways.terraria.efmodloader

import org.json.JSONException
import org.json.JSONObject
import silkways.terraria.efmodloader.utility.App
import java.io.File

private val configFile = File(App.getPrivate(), "config.json")

private fun readConfig(): JSONObject {
    if (!configFile.exists()) {
        saveConfig(JSONObject())
    }
    return try {
        JSONObject(configFile.readText())
    } catch (e: JSONException) {
        e.printStackTrace()
        JSONObject()
    }
}

private fun saveConfig(jsonObject: JSONObject) {
    configFile.writeText(jsonObject.toString(4))
}

private fun ensureKeyExists(jsonObject: JSONObject, key: String, defaultValue: Any): JSONObject {
    var currentObject = jsonObject
    val keys = key.split(".")
    for (i in keys.indices) {
        val currentKey = keys[i]
        if (i == keys.lastIndex) {
            if (!currentObject.has(currentKey)) {
                currentObject.put(currentKey, defaultValue)
            }
        } else {
            if (!currentObject.has(currentKey)) {
                currentObject.put(currentKey, JSONObject())
            }
            currentObject = currentObject.getJSONObject(currentKey)
        }
    }
    return jsonObject
}

private fun getValue(jsonObject: JSONObject, key: String): Any? {
    val keys = key.split(".")
    var currentObject: Any? = jsonObject

    for (keyPart in keys) {
        if (currentObject is JSONObject) {
            currentObject = (currentObject as JSONObject).opt(keyPart)
        } else {
            return null
        }
    }

    return currentObject
}

private fun setValue(jsonObject: JSONObject, key: String, value: Any): JSONObject {
    val keys = key.split(".")
    var currentObject = jsonObject

    for (i in keys.indices) {
        val currentKey = keys[i]
        if (i == keys.lastIndex) {
            currentObject.put(currentKey, value)
        } else {
            if (!currentObject.has(currentKey)) {
                currentObject.put(currentKey, JSONObject())
            }
            currentObject = currentObject.getJSONObject(currentKey)
        }
    }
    return jsonObject
}


actual object configuration {
    actual fun getString(key: String, default: String): String {
        val jsonObject = readConfig()
        ensureKeyExists(jsonObject, key, default)
        return getValue(jsonObject, key) as String
    }

    actual fun setString(key: String, value: String) {
        val jsonObject = readConfig()
        val updatedJsonObject = setValue(jsonObject, key, value)
        saveConfig(updatedJsonObject)
    }

    actual fun getBoolean(key: String, default: Boolean): Boolean {
        val jsonObject = readConfig()
        ensureKeyExists(jsonObject, key, default)
        return getValue(jsonObject, key) as Boolean
    }

    actual fun setBoolean(key: String, value: Boolean) {
        val jsonObject = readConfig()
        val updatedJsonObject = setValue(jsonObject, key, value)
        saveConfig(updatedJsonObject)
    }

    actual fun getInt(key: String, default: Int): Int {
        val jsonObject = readConfig()
        ensureKeyExists(jsonObject, key, default)
        return getValue(jsonObject, key) as Int
    }

    actual fun setInt(key: String, value: Int) {
        val jsonObject = readConfig()
        val updatedJsonObject = setValue(jsonObject, key, value)
        saveConfig(updatedJsonObject)
    }
}
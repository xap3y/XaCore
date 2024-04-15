package me.xap3y.xacore.api.config

import me.clip.placeholderapi.PlaceholderAPI
import me.xap3y.xacore.Main
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

class StorageManager(private val plugin: Main) {

    private var langYML: FileConfiguration? = null
    var storageYML: FileConfiguration? = null

    fun loadConfig() {
        if (!plugin.isConfigFileInit()) return
        if (!plugin.configFile.exists()) {
            plugin.saveResource(plugin.configFile.name, false)
        }
        plugin.config.load(plugin.configFile)
        plugin.debugMode = plugin.config.getBoolean("debug", false)

        logInfo(
            "[CFG] LOAD > ${plugin.config}",
            true)
    }

    fun loadLang() {
        if (!plugin.isLangFileInit()) return
        if (!plugin.messageFile.exists()) {
            plugin.saveResource(plugin.messageFile.name, false)
        }
        langYML = YamlConfiguration.loadConfiguration(plugin.messageFile)

        logInfo(
            "[LANG] LOAD > $langYML",
            true)
    }

    fun loadStorage() {
        if (!plugin.isStorageFileInit()) return
        if (!plugin.storageFile.exists()) {
            plugin.storageFile.createNewFile()
        }

        storageYML = YamlConfiguration.loadConfiguration(plugin.storageFile)

        logInfo(
            "[STORAGE-YML] LOAD > $storageYML",
            true)
    }

    fun getLocationFromStorage(key: String): Location? {
        if (storageYML === null) return null

        val selection: ConfigurationSection = storageYML?.getConfigurationSection(key) ?: return null
        val world = selection.getString("world") ?: return null
        val x = selection.getDouble("x")
        val y = selection.getDouble("y")
        val z = selection.getDouble("z")
        val yaw = selection.getDouble("yaw", 0.0).toFloat()
        val pitch = selection.getDouble("pitch", 0.0).toFloat()
        val realWorld = plugin.server.getWorld(world) ?: return null

        return Location(realWorld, x, y, z, yaw, pitch)
    }


    fun setLocationToStorage(key: String, location: Location): Boolean {
        if (storageYML === null) return false

        storageYML?.set("$key.world", location.world.name)
        storageYML?.set("$key.x", location.x)
        storageYML?.set("$key.y", location.y)
        storageYML?.set("$key.z", location.z)
        storageYML?.set("$key.yaw", location.yaw)
        storageYML?.set("$key.pitch", location.pitch)

        try {
            storageYML?.save(plugin.storageFile)
            return true
        } catch (e: Exception) {
            logInfo("[ERR-C1] ${e.message} ${e.stackTrace.joinToString("\n")}", true)
        }

        return false
    }

    fun getMessage(value: String, default: String, player: Player? = null): String {
        var key = langYML?.getString(value) ?: default
        val prefix = plugin.config.getString("prefix")

        plugin.storageManager.logInfo(
            "[STORAGE-MAN] getMessage - VAL: $value  DEF: $default  P: $player GOT: $key", true
        )

        key = key.replace("<prefix>", prefix ?: "")
        if (plugin.usePapi && player !== null) {
            plugin.textApi.console("Converting..")
            key = PlaceholderAPI.setPlaceholders(player, key)
        }

        return key.replace("%", "%%")
    }

    fun getList(value: String, player: Player? = null): MutableList<*>? {
        val list = langYML?.getList(value)?.toMutableList()
        if (list === null) return null
        val prefix = plugin.config.getString("prefix")
        list.replaceAll { it?.toString()?.replace("<prefix>", prefix ?: "") }

        if (plugin.usePapi && player !== null)
            list.replaceAll { PlaceholderAPI.setPlaceholders(player, it.toString()) }

        return list.map { it.toString().replace("%", "%%") }.toMutableList()
    }

    fun logInfo(message: String, debug: Boolean = false) {
        if (debug)  {
            if (!plugin.debugMode) return

            if (!plugin.logFileDebug.exists())
                plugin.logFileDebug.createNewFile()

        } else {
            if (!plugin.isLogFileInit()) return
            if (!plugin.config.getBoolean("logToFile", false)) return

            if (!plugin.logFile.exists())
                plugin.logFile.createNewFile()
        }

        plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable {
            if (!debug) plugin.logFile.appendText("$message\n")
            else {
                val time = System.currentTimeMillis()
                val date = java.util.Date(time)
                val humanDate = date.toString()
                plugin.logFileDebug.appendText("[$humanDate] $message\n")
            }
        })
    }

}
package me.xap3y.xacore.api.config

import me.clip.placeholderapi.PlaceholderAPI
import me.xap3y.xacore.Main
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

class StorageManager(private val plugin: Main) {

    private var langYML: FileConfiguration? = null

    fun loadConfig() {
        if (!plugin.isConfigFileInit()) return
        if (!plugin.configFile.exists()) {
            plugin.saveResource(plugin.configFile.name, false)
        }
        plugin.config.load(plugin.configFile)
    }

    fun loadLang() {
        if (!plugin.isLangFileInit()) return
        if (!plugin.messageFile.exists()) {
            plugin.saveResource(plugin.messageFile.name, false)
        }
        langYML = YamlConfiguration.loadConfiguration(plugin.messageFile)
    }

    fun getMessage(value: String, default: String, player: Player? = null): String {
        var key = langYML?.getString(value) ?: default
        val prefix = plugin.config.getString("prefix")

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

    fun logInfo(message: String) {
        if (!plugin.isLogFileInit()) return
        if (!plugin.config.getBoolean("logToFile", false)) return

        if (!plugin.logFile.exists())
            plugin.logFile.createNewFile()

        plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable {
            plugin.logFile.appendText("$message\n")
        })

    }

}
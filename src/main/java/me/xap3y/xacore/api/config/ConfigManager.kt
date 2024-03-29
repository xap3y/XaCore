package me.xap3y.xacore.api.config

import me.xap3y.xacore.Main
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration

class ConfigManager(private val plugin: Main) {

    private var langYML: FileConfiguration? = null

    fun loadConfig() {
        if (!plugin.isConfigFileInit()) return
        if (!plugin.configFile.exists()) {
            plugin.saveResource(plugin.configFile.name, false)
        }
        plugin.reloadConfig()
    }

    fun loadLang() {
        if (!plugin.isLangFileInit()) return
        if (!plugin.messageFile.exists()) {
            plugin.saveResource(plugin.messageFile.name, false)
        }
        langYML = YamlConfiguration.loadConfiguration(plugin.messageFile)
    }

    fun getMessage(value: String, default: String): String {
        val key = langYML?.getString(value) ?: default
        val prefix = plugin.config.getString("prefix")

        return key.replace("<prefix>", prefix ?: "")
    }

    fun getList(value: String): MutableList<*>? {
        val list = langYML?.getList(value)?.toMutableList()
        val prefix = plugin.config.getString("prefix")
        list?.replaceAll { it?.toString()?.replace("<prefix>", prefix ?: "") }
        //list?.let { plugin.textApi.console(it.joinToString(", ")) }
        return list
    }

}
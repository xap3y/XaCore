@file:Suppress("DEPRECATION")

package me.xap3y.xacore.api.text

import me.xap3y.xacore.Main
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("DEPRECATION")
class Texter(private val plugin: Main) {

    fun debugMessage(msg: String): String =
        coloredMessage("&8[&6D&8] &r$msg")

    fun coloredMessage(msg: String): String =
        ChatColor.translateAlternateColorCodes('&', msg)

    fun console(msg: String) =
        Bukkit.getConsoleSender().sendMessage(coloredMessage(msg.replace("<prefix>", plugin.config.getString("prefix") ?: "&7[&6XaCore&7]")))

    fun replace(msg: String, map: HashMap<String, String>, wPrefix: Boolean = false): String {
        var message = msg
        map.forEach { (key, value) ->
            message = message.replace("<$key>", value)
        }
        if (wPrefix) message = message.replace("<prefix>", plugin.config.getString("prefix") ?: "&7[&6XaCore&7]")
        return message
    }

    fun commandReply(commandSender: CommandSender, key: String, map: HashMap<String, String>? = null, wPrefix: Boolean = false, default: String? = null) =
        commandSender.sendMessage(
            plugin.textApi.coloredMessage(
                plugin.textApi.replace(
                    plugin.configManager.getMessage(key, default ?: "<prefix> &cNo message found!"),
                    map ?: hashMapOf(),
                    wPrefix
                )
            )
        )

    fun getPrefix(p: Player): String = plugin.chat?.getPlayerPrefix(p) ?: ""

    fun getSuffix(p: Player): String = plugin.chat?.getPlayerSuffix(p) ?: ""
    fun getGroup(p: Player): String = plugin.permission?.getPrimaryGroup(p) ?: ""
}
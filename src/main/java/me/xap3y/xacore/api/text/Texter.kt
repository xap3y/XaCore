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
        /*val regex = Regex("""<([^>]+)>""")
        map.keys.forEach { // &7From &6<player> &7>> &r<message>
            val placeholder = "<$it>"
            val replacement = map[it]
            message = message.replace(placeholder, replacement?: "")
            val replacedValuePlaceholder = "<$replacement>"
            message = message.replace(replacedValuePlaceholder, placeholder)
        }*/
        /*map.forEach { (key, value) ->
            message = message.replace("<$key>", value)
        }*/

        // This is piece of motherf***ing art
        val original = msg
        val regex = Regex("""<(?<tag>[^<>]+)>""")
        var result = regex.replace(original) { matchResult ->
            val tag = matchResult.groups["tag"]!!.value
            val replacement = map[tag]
            if (replacement != null && !original.substring(0, matchResult.range.first).contains("<$tag>")) {
                replacement
            } else {
                matchResult.value
            }
        }

        if (wPrefix) result = result.replace("<prefix>", plugin.config.getString("prefix") ?: "&7[&6XaCore&7]")
        return result
    }

    fun commandReply(commandSender: CommandSender, key: String, map: HashMap<String, String>? = null, wPrefix: Boolean = false, default: String? = null) =
        commandSender.sendMessage(
            plugin.textApi.coloredMessage(
                plugin.textApi.replace(
                    plugin.storageManager.getMessage(key, default ?: "<prefix> &cNo message found!"),
                    map ?: hashMapOf(),
                    wPrefix
                )
            )
        )

    fun getPrefix(p: Player): String {
        return if (plugin.isVaultPairInit())
            plugin.vaultPair.first.getPlayerPrefix(p)
        else ""
    }
    fun getSuffix(p: Player): String {
        return if (plugin.isVaultPairInit())
            plugin.vaultPair.first.getPlayerSuffix(p)
        else ""
    }
    fun getGroup(p: Player): String {
        return if (plugin.isVaultPairInit())
            plugin.vaultPair.second.getPrimaryGroup(p)
        else ""
    }
}
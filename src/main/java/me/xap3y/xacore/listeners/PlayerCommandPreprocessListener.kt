package me.xap3y.xacore.listeners

import me.xap3y.xacore.Main
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class PlayerCommandPreprocessListener(private val plugin: Main): Listener {

    @EventHandler
    fun onPlayerCommandProcess(e: PlayerCommandPreprocessEvent) {

        val commandBlackListEnabled: Boolean = plugin.config.getBoolean("commandBlackList")
        if (!commandBlackListEnabled) return

        // Get command without the slash
        val command = e.message.split(" ")[0].substring(1)

        val blackList = plugin.configManager.getList("commands.blacklist", e.player) ?: return

        if (blackList.any { it == command || it.toString().endsWith("*") && command.startsWith(it.toString().substringBefore("*")) }) {
            if (e.player.isOp || e.player.hasPermission("xacore.bypass") || e.player.hasPermission("xacore.*")) return
            e.isCancelled = true
            plugin.helper.noPermMessage(
                e.player,
                hashMapOf("command" to command)
            )
        }
    }
}
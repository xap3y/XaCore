package me.xap3y.xacore.listeners

import me.xap3y.xacore.Main
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class PlayerCommandPreprocessListener(private val plugin: Main): Listener {

    @EventHandler
    fun onPlayerCommandProcess(e: PlayerCommandPreprocessEvent) {
        val command = e.message.split(" ")[0].substring(1)

        plugin.storageManager.logInfo(
            "[EVENT] PlayerCommandPreprocessEvent > FIRED - ${e.player.name} : ${e.message}",
            true)

        if (plugin.cmdSpyToggles.isNotEmpty()) {
            val message = plugin.textApi.coloredMessage(plugin.textApi.replace(
                plugin.storageManager.getMessage("messages.cmdSpyFormat", ""),
                hashMapOf("player" to e.player.name, "command" to command),
                true
            ))
            plugin.cmdSpyToggles.forEach {
                if (it != e.player) {
                    if (!it.isOp && !(it.hasPermission("xacore.*") || it.hasPermission("xacore.cmdspy")))
                        plugin.cmdSpyToggles.remove(it)
                    else
                        it.sendMessage(message)
                }
            }
        }

        val commandBlackListEnabled: Boolean = plugin.config.getBoolean("commandBlackList", false)
        if (!commandBlackListEnabled)
            return plugin.storageManager.logInfo("[CMD] ${e.player.name} |>> ${e.message}")

        // Get command without the slash

        val blackList = plugin.storageManager.getList("commands.blacklist", e.player) ?:
            return plugin.storageManager.logInfo("[CMD] ${e.player.name} |>> ${e.message}")

        if (blackList.any { it == command || it.toString().endsWith("*") && command.startsWith(it.toString().substringBefore("*")) }) {

            if (e.player.isOp || e.player.hasPermission("xacore.bypass") || e.player.hasPermission("xacore.*"))
                return plugin.storageManager.logInfo("[CMD] ${e.player.name} |>> ${e.message}")

            e.isCancelled = true
            plugin.helper.noPermMessage(
                e.player,
                hashMapOf("command" to command)
            )

            plugin.storageManager.logInfo("[CMD-BLOCKED] ${e.player.name} |>> ${e.message}")

        } else
            plugin.storageManager.logInfo("[CMD] ${e.player.name} |>> ${e.message}")
    }
}
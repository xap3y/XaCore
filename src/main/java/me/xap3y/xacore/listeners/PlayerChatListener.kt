@file:Suppress("DEPRECATION")

package me.xap3y.xacore.listeners

import me.xap3y.xacore.Main
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class PlayerChatListener(private val plugin: Main): Listener {

    @EventHandler
    fun onPlayerChatEvent(e: AsyncPlayerChatEvent) {

        val isChatLocked = plugin.chatLocked
        if (isChatLocked) {
            if (e.player.hasPermission("xacore.chat.bypass") || e.player.hasPermission("xacore.*") || e.player.isOp) return
            e.isCancelled = true
            return plugin.textApi.commandReply(e.player, "messages.chatDeny", wPrefix = true, default = "<prefix> &cChat is locked!")
        }

        val isEnabled = plugin.config.getBoolean("chatFormat")
        if (!isEnabled) return

        val message = plugin.configManager.getMessage("chatFormat", "&6<player> &7>> &r<message>", e.player)

        e.format = plugin.textApi.coloredMessage(
            plugin.textApi.replace(
                message,
                hashMapOf(
                    "player" to e.player.displayName,
                    "message" to e.message,
                    "vault_prefix" to plugin.textApi.getPrefix(e.player),
                    "vault_suffix" to plugin.textApi.getSuffix(e.player),
                    "vault_group" to plugin.textApi.getGroup(e.player)
                ),
                true
            )
        )
    }
}
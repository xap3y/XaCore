@file:Suppress("DEPRECATION")

package me.xap3y.xacore.listeners

import me.xap3y.xacore.Main
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class PlayerChatListener(private val plugin: Main): Listener {

    @EventHandler
    fun onPlayerChatEvent(e: AsyncPlayerChatEvent) {
        val isEnabled = plugin.config.getBoolean("chatFormat")
        if (!isEnabled) return

        val message = plugin.configManager.getMessage("chatFormat", "&6<player> &7>> &r<message>")

        e.format = plugin.textApi.coloredMessage(
            plugin.textApi.replace(
                message,
                hashMapOf(
                    "player" to e.player.displayName,
                    "message" to e.message
                ),
                true
            )
        )
    }
}
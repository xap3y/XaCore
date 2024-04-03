package me.xap3y.xacore.listeners

import me.xap3y.xacore.Main
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("DEPRECATION")
class PlayerQuitListener(private val plugin: Main): Listener {

    @EventHandler
    fun onPlayerQuitEvent(e: PlayerQuitEvent) {
        val isEnabled = plugin.config.getBoolean("quitMessage")
        if (!isEnabled) return

        val message = plugin.configManager.getMessage("messages.quitMessage", "&6<player> &fleft the game")

        e.quitMessage = plugin.textApi.coloredMessage(
            plugin.textApi.replace(
                message,
                hashMapOf("player" to e.player.displayName),
                true
            )
        )
    }
}
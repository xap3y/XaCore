package me.xap3y.xacore.listeners

import me.xap3y.xacore.Main
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

@Suppress("DEPRECATION")
class PlayerJoinListener(private val plugin: Main): Listener {

    @EventHandler
    fun onPlayerJoinEvent(e: PlayerJoinEvent) {
        val isEnabled = plugin.config.getBoolean("messages.joinMessage")
        if (!isEnabled) return

        val message = plugin.configManager.getMessage("messages.joinMessage", "&6<player> &fjoined the game")

        e.joinMessage = plugin.textApi.coloredMessage(
            plugin.textApi.replace(
                message,
                hashMapOf("player" to e.player.displayName),
                true
            )
        )
    }

}
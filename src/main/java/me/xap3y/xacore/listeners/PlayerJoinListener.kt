package me.xap3y.xacore.listeners

import me.xap3y.xacore.Main
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

@Suppress("DEPRECATION")
class PlayerJoinListener(private val plugin: Main): Listener {

    @EventHandler
    fun onPlayerJoinEvent(e: PlayerJoinEvent) {

        // Custom join message
        val joinMessageEnabled = plugin.config.getBoolean("joinMessage")
        if (joinMessageEnabled) {
            val message = plugin.configManager.getMessage("messages.joinMessage", "&6<player> &fjoined the game")

            e.joinMessage = plugin.textApi.coloredMessage(
                plugin.textApi.replace(
                    message,
                    hashMapOf("player" to e.player.displayName),
                    true
                )
            )
        }

        // Spawn on player join
        val spawnOnJoinEnabled = plugin.config.getBoolean("spawnOnJoin")

        if (spawnOnJoinEnabled) {

            val spawnLocation = plugin.helper.getSpawnLocation() ?: return

            // Sync
            plugin.server.scheduler.runTask(plugin, Runnable {
                e.player.teleport(spawnLocation)
            })
        }

        // TODO -- First join message
        // e.player.hasPlayedBefore()

        // TODO -- Scoreboard and maybe tab-list?


    }

}
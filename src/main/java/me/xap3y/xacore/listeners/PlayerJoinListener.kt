package me.xap3y.xacore.listeners

import me.xap3y.xacore.Main
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

@Suppress("DEPRECATION")
class PlayerJoinListener(private val plugin: Main): Listener {

    @EventHandler
    fun onPlayerJoinEvent(e: PlayerJoinEvent) {

        val joinMessageEnabled = plugin.config.getBoolean("joinMessage", false)
        val spawnOnJoinEnabled = plugin.config.getBoolean("spawnOnJoin", false)
        val gamemodeOnJoinToggle = plugin.config.getBoolean("gamemodeOnJoinToggle", false)

        plugin.storageManager.logInfo(
            "[EVENT] PlayerJoinEvent > FIRED - ${e.player.name} GMON: $gamemodeOnJoinToggle  JM: $joinMessageEnabled SON: $spawnOnJoinEnabled",
            true)

        // Custom join message
        if (joinMessageEnabled) {
            val message = plugin.storageManager.getMessage("messages.joinMessage", "&6<player> &fjoined the game", e.player)

            e.joinMessage = plugin.textApi.coloredMessage(
                plugin.textApi.replace(
                    message,
                    hashMapOf("player" to e.player.displayName),
                    true
                )
            )
        }

        // Spawn on player join
        if (spawnOnJoinEnabled) {

            val spawnLocation: Location = plugin.storageManager.getLocationFromStorage("spawn") ?: return

            // Sync
            plugin.server.scheduler.runTask(plugin, Runnable {
                e.player.teleport(spawnLocation)
            })
        }

        // TODO -- First join message
        // e.player.hasPlayedBefore()

        // TODO -- Scoreboard and maybe tab-list?

        // Gamemode on join
        if (gamemodeOnJoinToggle) {

            val gamemodeOnJoin = plugin.config.getString("gamemodeOnJoin") ?: "SURVIVAL"
            // get gamemode from string
            val gamemode = when (gamemodeOnJoin.uppercase()) {
                "SURVIVAL" -> org.bukkit.GameMode.SURVIVAL
                "CREATIVE" -> org.bukkit.GameMode.CREATIVE
                "ADVENTURE" -> org.bukkit.GameMode.ADVENTURE
                "SPECTATOR" -> org.bukkit.GameMode.SPECTATOR
                else -> org.bukkit.GameMode.SURVIVAL
            }
            Bukkit.getScheduler().runTask(plugin, Runnable {
                e.player.gameMode = gamemode
            })
        }

    }

}
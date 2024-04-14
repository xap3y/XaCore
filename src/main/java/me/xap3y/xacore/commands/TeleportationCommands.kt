package me.xap3y.xacore.commands

import me.xap3y.xacore.Main
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class TeleportationCommands(private val plugin: Main) {

    @Command("setspawn")
    @CommandDescription("Set the spawn location")
    @Permission(value = ["xacore.setspawn", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onSetSpawnCommand(commandSender: CommandSender) {
        if (commandSender !is Player)
            return plugin.textApi.commandReply(
                commandSender,
                "messages.onlyPlayers",
                wPrefix = true
            )

        plugin.config.set("spawn.world", commandSender.world.name)
        plugin.config.set("spawn.x", commandSender.location.x)
        plugin.config.set("spawn.y", commandSender.location.y)
        plugin.config.set("spawn.z", commandSender.location.z)
        plugin.config.set("spawn.yaw", commandSender.location.yaw)
        plugin.config.set("spawn.pitch", commandSender.location.pitch)
        plugin.config.save(plugin.configFile)

        plugin.textApi.commandReply(
            commandSender,
            "messages.setSpawn",
            wPrefix = true,
            default = "<prefix> &fSpawn set!"
        )
    }

    @Command("spawn")
    @CommandDescription("Teleport to the spawn location")
    //@Permission(value = ["xacore.spawn", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onSpawnCommand(commandSender: CommandSender) {
        if (commandSender !is Player)
            return plugin.textApi.commandReply(
                commandSender,
                "messages.onlyPlayers",
                wPrefix = true
            )

        val spawnLocation = plugin.helper.getSpawnLocation() ?: return plugin.textApi.commandReply(
            commandSender,
            "messages.noSpawn",
            wPrefix = true,
            default = "<prefix> &cSpawn not set"
        )

        // Sync
        plugin.server.scheduler.runTask(plugin, Runnable {
            commandSender.teleport(spawnLocation)
        })

        plugin.textApi.commandReply(
            commandSender,
            "messages.spawn",
            wPrefix = true,
            default = "<prefix> &fTeleported to spawn!"
        )
    }

    @Command("sethome [name]")
    @CommandDescription("Set your home location")
    @Permission(value = ["xacore.sethome", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onSetHomeCommand(
        commandSender: CommandSender,
        @Argument("name") homeName: String? = null,
    ) {
        if (commandSender !is Player)
            return plugin.textApi.commandReply(
                commandSender,
                "messages.onlyPlayers",
                wPrefix = true
            )

        return plugin.textApi.commandReply(
            commandSender,
            "messages.setHome",
            if (homeName != null) hashMapOf("home" to homeName) else hashMapOf("home" to "home"),
            wPrefix = true,
            default = "<prefix> &fHome set!"
        )
    }
}
@file:Suppress("DEPRECATION")

package me.xap3y.xacore.commands

import me.xap3y.xacore.Main
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class AdminCommands(private val plugin: Main) {
    @Command("fly")
    @CommandDescription("Toggle fly mode")
    @Permission(value = ["xacore.fly", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onFlyCommand(commandSender: CommandSender) {
        if (commandSender !is Player)
            return plugin.textApi.commandReply(
                commandSender,
                "messages.onlyPlayers",
                wPrefix = true
            )

        commandSender.allowFlight = !commandSender.allowFlight

        plugin.textApi.commandReply(
            commandSender,
            "messages.flyToggle",
            hashMapOf("status" to if (commandSender.allowFlight) "&aenabled" else "&cdisabled"),
            true,
            "<prefix> &fFly mode &r<status>&f!"
        )
    }

    @Command("speed <speed>")
    @CommandDescription("Change your speed")
    @Permission(value = ["xacore.speed", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onSpeedCommand(
        commandSender: CommandSender,
        @Argument("speed") speed: Int,
    ) {
        if (commandSender !is Player)
            return plugin.textApi.commandReply(
                commandSender,
                "messages.onlyPlayers",
                wPrefix = true
            )

        if (speed < 1 || speed > 10)
            return plugin.textApi.commandReply(
                commandSender,
                "messages.speedRange",
                hashMapOf("speed" to speed.toString()),
                true,
                "<prefix> &cSpeed must be between 1 and 10!"
            )

        if (commandSender.isFlying)
            commandSender.flySpeed = speed / 20f
        else
            commandSender.walkSpeed = speed / 20f

        plugin.textApi.commandReply(
            commandSender,
            "messages.speedMessage",
            hashMapOf("speed" to speed.toString()),
            true,
            "<prefix> &fYour speed has been set to &e<speed>&f!"
        )
    }

    @Command("heal [player]")
    @CommandDescription("Heal yourself or another player")
    @Permission(value = ["xacore.heal", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onHealCommand(
        commandSender: CommandSender,
        @Argument("player") player: Player? = null,
    ) {
        if (commandSender !is Player && player == null)
            return plugin.textApi.commandReply(
                commandSender,
                "messages.wrongUsage",
                hashMapOf("usage" to "/xc heal <player>"),
                true
            )

        val target = player ?: commandSender as Player

        target.health = target.maxHealth
        target.foodLevel = 20

        if (player == null || (commandSender is Player && commandSender.uniqueId.toString() == target.uniqueId.toString())) {
            plugin.textApi.commandReply(
                commandSender,
                "messages.healSelf",
                hashMapOf("player" to target.name),
                true,
                "<prefix> &fYou have healed &e<player>&f!"
            )
        } else {
            plugin.textApi.commandReply(
                commandSender,
                "messages.healOther",
                hashMapOf("player" to target.name),
                true,
                "<prefix> &fYou have healed &e<player>&f!"
            )
        }
    }

    @Command("feed [player]")
    @CommandDescription("Feed yourself or another player")
    @Permission(value = ["xacore.feed", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onFeedCommand(
        commandSender: CommandSender,
        @Argument("player") player: Player? = null,
    ) {
        if (commandSender !is Player && player == null)
            return plugin.textApi.commandReply(
                commandSender,
                "messages.wrongUsage",
                hashMapOf("usage" to "/xc feed <player>"),
                true
            )

        val target = player ?: commandSender as Player

        target.foodLevel = 20

        if (player == null || (commandSender is Player && commandSender.uniqueId.toString() == target.uniqueId.toString())) {
            plugin.textApi.commandReply(
                commandSender,
                "messages.feedSelf",
                hashMapOf("player" to target.name),
                true,
                "<prefix> &fYou have fed yourself!"
            )
        } else {
            plugin.textApi.commandReply(
                commandSender,
                "messages.feedOther",
                hashMapOf("player" to target.name),
                true,
                "<prefix> &fYou have fed &e<player>&f!"
            )
        }
    }

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


}
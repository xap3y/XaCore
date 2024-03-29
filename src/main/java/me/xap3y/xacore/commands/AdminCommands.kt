@file:Suppress("DEPRECATION")

package me.xap3y.xacore.commands

import me.xap3y.xacore.Main
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
                wPrefix = true,
                default = "<prefix> &cOnly players can use this command!"
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
                true,
                "<prefix> &cWrong usage! &7<usage>"
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



}
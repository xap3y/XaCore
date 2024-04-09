@file:Suppress("DEPRECATION")

package me.xap3y.xacore.commands

import me.xap3y.xacore.Main
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class Gamemodes(private val plugin: Main) {

    @Command("gamemode|gm <gamemode> [player]")
    @CommandDescription("Change the gamemode of a player")
    @Permission(value = ["xacore.gamemode.survival", "xacore.gamemode.creative", "xacore.gamemode.spectator", "xacore.gamemode.adventure", "xacore.gamemode.*", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onGamemodeCommand(
        commandSender: CommandSender,
        @Argument("player") player: Player? = null,
        @Argument("gamemode") gamemode: GameMode? = null,
    ) {
        if (gamemode == null)

            return plugin.helper.wrongUsageMessage(commandSender, "/xc gm <gamemode> [player]")

        else if (player == null && commandSender !is Player)

            return plugin.helper.wrongUsageMessage(commandSender, "/xc gm <gamemode> <player>")

        val target = player ?: commandSender as Player
        changeGamemode(target, gamemode, commandSender)
    }

    @Command("gmc [player]")
    @CommandDescription("Change the gamemode of a player to creative")
    @Permission(value = ["xacore.gamemode.creative", "xacore.gamemode.*", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onGamemodeCreative(commandSender: CommandSender, @Argument("player") player: Player? = null) {
        if (commandSender !is Player && player == null)
            return plugin.helper.wrongUsageMessage(commandSender, "/gmc <player>")

        val target = player ?: commandSender as Player

        changeGamemode(target, GameMode.CREATIVE, commandSender)
    }

    @Command("gma [player]")
    @CommandDescription("Change the gamemode of a player to adventure")
    @Permission(value = ["xacore.gamemode.adventure", "xacore.gamemode.*", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onGamemodeAdventure(commandSender: CommandSender, @Argument("player") player: Player? = null) {
        if (commandSender !is Player && player == null)
            return plugin.helper.wrongUsageMessage(commandSender, "/gma <player>")

        val target = player ?: commandSender as Player

        changeGamemode(target, GameMode.ADVENTURE, commandSender)
    }

    @Command("gmsp [player]")
    @CommandDescription("Change the gamemode of a player to spectator")
    @Permission(value = ["xacore.gamemode.spectator", "xacore.gamemode.*", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onGamemodeSpectator(commandSender: CommandSender, @Argument("player") player: Player? = null) {
        if (commandSender !is Player && player == null)
            return return plugin.helper.wrongUsageMessage(commandSender, "/gmsp <player>")
        val target = player ?: commandSender as Player

        changeGamemode(target, GameMode.SPECTATOR, commandSender)
    }

    @Command("gms [player]")
    @CommandDescription("Change the gamemode of a player to survival")
    @Permission(value = ["xacore.gamemode.survival", "xacore.gamemode.*", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onGamemodeSurvival(commandSender: CommandSender, @Argument("player") player: Player? = null) {
        if (commandSender !is Player && player == null)
            return return plugin.helper.wrongUsageMessage(commandSender, "/gms <player>")

        val target = player ?: commandSender as Player

        changeGamemode(target, GameMode.SURVIVAL, commandSender)
    }

    private fun changeGamemode(target: Player, gamemode: GameMode, commandSender: CommandSender) {

        val gamemodeName = gamemode.name.lowercase()

        if (!commandSender.hasPermission("xacore.gamemode.$gamemodeName") && !commandSender.hasPermission("xacore.gamemode.*") && !commandSender.hasPermission("xacore.*"))

            return plugin.helper.noPermMessage(
                commandSender,
                hashMapOf("permission" to "xacore.gamemode.$gamemodeName")
            )

        if (commandSender is Player && commandSender.uniqueId.toString() == target.uniqueId.toString())
            plugin.textApi.commandReply(
                commandSender,
                "messages.gamemodeChanged",
                hashMapOf("gamemode" to gamemodeName),
                true,
                "<prefix> &fGamemode changed to &6<gamemode>"
            )
        else
            plugin.textApi.commandReply(
                commandSender,
                "messages.gamemodeChangedOther",
                hashMapOf("player" to target.displayName, "gamemode" to gamemodeName),
                true,
                "<prefix> &fGamemode changed for &e<player> &fto &6<gamemode>"
            )

        Bukkit.getScheduler().runTask(plugin, Runnable {
            target.gameMode = gamemode
        })
    }
}
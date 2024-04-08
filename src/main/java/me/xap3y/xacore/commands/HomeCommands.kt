package me.xap3y.xacore.commands

import me.xap3y.xacore.Main
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class HomeCommands(private val plugin: Main) {

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
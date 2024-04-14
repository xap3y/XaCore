package me.xap3y.xacore.commands

import me.xap3y.xacore.Main
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class ChatCommands(private val plugin: Main) {

    @Command("cmdspy")
    @CommandDescription("Spy player's commands")
    @Permission(value = ["xacore.cmdspy", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onCmdSpyCommand(commandSender: CommandSender) {

        if (commandSender !is Player)
            return plugin.helper.onlyPlayersMessage(commandSender)

        if (!plugin.cmdSpyToggles.contains(commandSender)) {
            plugin.textApi.commandReply(
                commandSender,
                "messages.cmdSpyEnable",
                hashMapOf("toggle" to "&aenabled"),
                true
            )
            plugin.cmdSpyToggles.add(commandSender)
        }
        else {
            plugin.textApi.commandReply(
                commandSender,
                "messages.cmdSpyDisable",
                hashMapOf("toggle" to "&cdisabled"),
                true
            )
            plugin.cmdSpyToggles.remove(commandSender)
        }
    }

    @Command("lockchat|chatlock")
    @CommandDescription("Lock the chat")
    @Permission(value = ["xacore.lockchat", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onLockChatCommand(commandSender: CommandSender) {
        plugin.chatLocked = !plugin.chatLocked

        plugin.server.onlinePlayers.forEach {
            plugin.textApi.commandReply(
                it,
                if (plugin.chatLocked) "messages.chatLocked" else "messages.chatUnlocked",
                wPrefix = true,
                default = if (plugin.chatLocked) "<prefix> &fChat locked!" else "<prefix> &fChat unlocked!"
            )
        }
        if (commandSender !is Player)
            plugin.textApi.commandReply(
                commandSender,
                if (plugin.chatLocked) "messages.chatLocked" else "messages.chatUnlocked",
                wPrefix = true,
                default = if (plugin.chatLocked) "<prefix> &fChat locked!" else "<prefix> &fChat unlocked!"
            )
    }

    @Command("clearchat|cc")
    @CommandDescription("Clear the chat")
    @Permission(value = ["xacore.clearchat", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onClearChatCommand(commandSender: CommandSender) {

        plugin.server.onlinePlayers.forEach {
            for (i in 0..100) {
                it.sendMessage("    ")
                it.sendMessage(" ")
            }
            if (it != commandSender)
                plugin.textApi.commandReply(
                    it,
                    "messages.clearChat",
                    wPrefix = true,
                    default = "<prefix> &fChat cleared!"
                )
        }

        plugin.textApi.commandReply(
            commandSender,
            "messages.chatCleared",
            wPrefix = true,
            default = "<prefix> &fChat cleared!"
        )
    }
}
package me.xap3y.xacore.commands

import me.xap3y.xacore.Main
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
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

    @Command("whisper|msg|tell|w <player> <message>")
    @CommandDescription("Send a private message")
    fun onWhisperCommand(
        commandSender: CommandSender,
        @Argument("player") target: Player,
        @Argument("message") message: Array<String>
    ) {
        if (target == commandSender)
            return plugin.textApi.commandReply(commandSender, "messages.whisperSelf", wPrefix = true)

        processWhisper(commandSender, target, message.joinToString(" "))

        plugin.whisperPlayers[commandSender] = target
    }

    @Command("reply|r <message>")
    @CommandDescription("Reply to a private message")
    fun onReplyCommand(
        commandSender: CommandSender,
        @Argument("message") message: Array<String>
    ) {
        val target = plugin.whisperPlayers[commandSender]

        if (target === null)
            return plugin.textApi.commandReply(commandSender, "messages.whisperNobody", default = "<prefix> &cNobody to reply!", wPrefix = true)

        else if (target is Player && !target.isOnline) {
            plugin.textApi.commandReply(
                commandSender,
                "messages.whisperOffline",
                hashMapOf("player" to target.name),
                default = "<prefix> &cNobody to reply!",
                wPrefix = true)

            plugin.whisperPlayers.remove(commandSender)
            return
        }

        processWhisper(commandSender, target, message.joinToString(" "))
    }

    private fun processWhisper(from: CommandSender, to: CommandSender, content: String) {
        val formats = plugin.helper.getWhisperFormats()

        plugin.storageManager.logInfo(
            "[WHISPER] FORMAT1: ${formats.first} FORMAT2: ${formats.second}" +
            "FROM: $from  |  TO: $to  |  CONTENT: $content", true
        )
        val messageToSend = plugin.textApi.coloredMessage(content)

        from.sendMessage(
            plugin.textApi.coloredMessage(
                plugin.textApi.replace(
                    formats.second,
                    hashMapOf("player" to to.name, "message" to messageToSend),
                    false
                )
            )
        )

        to.sendMessage(
            plugin.textApi.coloredMessage(
                plugin.textApi.replace(
                    formats.first,
                    hashMapOf("player" to from.name, "message" to messageToSend),
                    false
                )
            )
        )

        plugin.whisperPlayers[from] = to
        plugin.whisperPlayers[to] = from

        plugin.storageManager.logInfo("[PM] ${from.name} -> ${to.name} : $content")
    }
}
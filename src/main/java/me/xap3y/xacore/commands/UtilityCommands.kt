package me.xap3y.xacore.commands

import me.xap3y.xacore.Main
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class UtilityCommands(private val plugin: Main) {

    @Command("enderchest|ec")
    @CommandDescription("Opens your enderchest")
    @Permission(value = ["xacore.enderchest", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onEnderChestCommand(commandSender: org.bukkit.command.CommandSender) {

        if (commandSender !is Player)
            return plugin.helper.onlyPlayersMessage(commandSender)

        Bukkit.getScheduler().runTask(plugin, Runnable {
            commandSender.openInventory(commandSender.enderChest)
        })
        commandSender.playSound(commandSender.location, org.bukkit.Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f)
    }

    @Command("workbench|craft|wb")
    @CommandDescription("Opens a workbench")
    @Permission(value = ["xacore.workbench", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onWorkbenchCommand(commandSender: org.bukkit.command.CommandSender) {

        if (commandSender !is Player)
            return plugin.helper.onlyPlayersMessage(commandSender)

        Bukkit.getScheduler().runTask(plugin, Runnable {
            commandSender.openWorkbench(null, true)
        })
        commandSender.playSound(commandSender.location, org.bukkit.Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1f, 1f)
    }

    @Command("anvil")
    @CommandDescription("Opens an anvil")
    @Permission(value = ["xacore.anvil", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onAnvilCommand(commandSender: org.bukkit.command.CommandSender) {

        if (commandSender !is Player)
            return plugin.helper.onlyPlayersMessage(commandSender)


        Bukkit.getScheduler().runTask(plugin, Runnable {
            commandSender.openAnvil(null, true)
        })
        commandSender.playSound(commandSender.location, org.bukkit.Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }

    @Command("enchanttable|et")
    @CommandDescription("Opens an enchanting table")
    @Permission(value = ["xacore.enchanttable", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onEnchantTableCommand(commandSender: org.bukkit.command.CommandSender) {

        if (commandSender !is Player)
            return plugin.helper.onlyPlayersMessage(commandSender)

        Bukkit.getScheduler().runTask(plugin, Runnable {
            commandSender.openEnchanting(null, true)
        })
        commandSender.playSound(commandSender.location, org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f)
    }

    @Command("ping [player]")
    @CommandDescription("Check ping")
    fun onPingCommand(
        commandSender: org.bukkit.command.CommandSender,
        @Argument("player") player: Player? = null
    ) {

        if (player == null && commandSender !is Player) {
            return plugin.helper.wrongUsageMessage(commandSender, "/ping <player>")
        }

        val target = player ?: commandSender as Player
        val ping = target.ping

        if (commandSender is Player && target.uniqueId == commandSender.uniqueId)
            return plugin.textApi.commandReply(
                commandSender,
                "messages.ping",
                hashMapOf("ping" to ping.toString()),
                true,
                "<prefix> &fYour ping is &r<ping>ms"
            )
        else
            return plugin.textApi.commandReply(
                commandSender,
                "messages.pingOther",
                hashMapOf("player" to target.name, "ping" to ping.toString()),
                true,
                "<prefix> &fThe ping of &e<player> &fis &r<ping>ms"
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
    }
}
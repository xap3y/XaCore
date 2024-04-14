package me.xap3y.xacore.commands

import me.xap3y.xacore.Main
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class InventoryCommands(private val plugin: Main) {

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

    @Command("invsee <player>")
    @CommandDescription("View the inventory of another player")
    @Permission(value = ["xacore.invsee", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onInvseeCommand(
        commandSender: CommandSender,
        @Argument("player") player: Player,
    ) {
        if (commandSender !is Player)
            return plugin.helper.onlyPlayersMessage(commandSender)

        plugin.server.scheduler.runTask(plugin, Runnable {
            commandSender.openInventory(player.inventory)
        })
    }
}
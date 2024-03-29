@file:Suppress("DEPRECATION")

package me.xap3y.xacore.commands

import me.xap3y.xacore.Main
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class RootCommand(private val plugin: Main) {

    @Command("xacore|xc reload")
    @CommandDescription("Reloads the plugin configuration")
    @Permission(value = ["xacore.reload", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onReloadCommand(commandSender: CommandSender) {
        plugin.reloadConfig()
        plugin.configManager.loadLang()

        plugin.textApi.commandReply(commandSender, "reloadMessage", wPrefix = true, default = "<prefix> &aConfiguration reloaded!")
    }

    @Command("xacore|xc")
    @CommandDescription("Shows the plugin help")
    fun onRootCommand(commandSender: CommandSender) {

        plugin.textApi.commandReply(commandSender, "helpMessage", wPrefix = true, default = "<prefix> &fTo view command list, type: &e/xc help")
    }

    @Command("xacore|xc help")
    @CommandDescription("Shows the plugin help")
    fun onHelpCommand(commandSender: CommandSender) {

        val menu = plugin.configManager.getList("menus.helpMenu") ?:
            return commandSender.sendMessage(plugin.textApi.replace(plugin.textApi.coloredMessage("<prefix> &cNo help message found!"), hashMapOf(), true))


        menu.forEach {
            commandSender.sendMessage(plugin.textApi.coloredMessage(it.toString()))
        }

    }

    @Command("xacore|xc about")
    @CommandDescription("Shows the about menu")
    fun onAboutCommand(commandSender: CommandSender) {

        val menu = plugin.configManager.getList("menus.aboutMenu") ?:
            return commandSender.sendMessage(plugin.textApi.replace(plugin.textApi.coloredMessage("<prefix> &cNo help message found!"), hashMapOf(), true))

        menu.forEach {
            commandSender.sendMessage(
                plugin.textApi.coloredMessage(
                    plugin.textApi.replace(
                        it.toString(),
                        hashMapOf(
                            "version" to plugin.description.version,
                            "author" to plugin.description.authors.joinToString(", "),
                            "debug" to "false"
                        ),
                        true
                    )
                )
            )
        }
    }
}
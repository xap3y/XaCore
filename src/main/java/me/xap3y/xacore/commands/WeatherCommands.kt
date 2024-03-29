package me.xap3y.xacore.commands

import me.xap3y.xacore.Main
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class WeatherCommands(private val plugin: Main) {
    @Command("sun")
    @CommandDescription("Change the weather to sunny")
    @Permission(value = ["xacore.weather.sun", "xacore.weather.*", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onSunCommand(commandSender: CommandSender) {
        if (commandSender !is Player) {
            Bukkit.getScheduler().runTask(plugin, Runnable {
                commandSender.server.worlds.forEach {
                    it.setStorm(false)
                    it.isThundering = false
                }
            })
        } else
            Bukkit.getScheduler().runTask(plugin, Runnable {
                commandSender.world.setStorm(false)
                commandSender.world.isThundering = false
            })


        plugin.textApi.commandReply(
            commandSender,
            "messages.sunMessage",
            wPrefix = true,
            default = "<prefix> &fWeather changed to sunny!"
        )

    }

    @Command("rain")
    @CommandDescription("Change the weather to rainy")
    @Permission(value = ["xacore.weather.rain", "xacore.weather.*", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onRainCommand(commandSender: CommandSender) {
        if (commandSender !is Player) {
            Bukkit.getScheduler().runTask(plugin, Runnable {
                commandSender.server.worlds.forEach {
                    it.setStorm(true)
                    it.isThundering = false
                }
            })

        } else
            Bukkit.getScheduler().runTask(plugin, Runnable {
                commandSender.world.setStorm(true)
                commandSender.world.isThundering = false
            })

        plugin.textApi.commandReply(
            commandSender,
            "messages.rainMessage",
            wPrefix = true,
            default = "<prefix> &fWeather changed to rainy!"
        )
    }

    @Command("thunder")
    @CommandDescription("Change the weather to thunder")
    @Permission(value = ["xacore.weather.thunder", "xacore.weather.*", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onThunderCommand(commandSender: CommandSender) {
        if (commandSender !is Player) {
            Bukkit.getScheduler().runTask(plugin, Runnable {
                commandSender.server.worlds.forEach {
                    it.setStorm(true)
                    it.isThundering = true
                }
            })

        } else
            Bukkit.getScheduler().runTask(plugin, Runnable {
                commandSender.world.setStorm(true)
                commandSender.world.isThundering = true
            })

        plugin.textApi.commandReply(
            commandSender,
            "messages.thunderMessage",
            wPrefix = true,
            default = "<prefix> &fWeather changed to thunder!"
        )
    }

    @Command("day")
    @CommandDescription("Change the time to day")
    @Permission(value = ["xacore.weather.day", "xacore.weather.*", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onDayCommand(commandSender: CommandSender) {
        if (commandSender !is Player) {

            Bukkit.getScheduler().runTask(plugin, Runnable {
                commandSender.server.worlds.forEach {
                    it.time = 0
                }
            })
        } else
            Bukkit.getScheduler().runTask(plugin, Runnable {
                commandSender.world.time = 0
            })

        plugin.textApi.commandReply(
            commandSender,
            "messages.dayMessage",
            wPrefix = true,
            default = "<prefix> &fTime changed to day!"
        )
    }

    @Command("night")
    @CommandDescription("Change the time to night")
    @Permission(value = ["xacore.weather.night", "xacore.weather.*", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onNightCommand(commandSender: CommandSender) {
        if (commandSender !is Player) {

            Bukkit.getScheduler().runTask(plugin, Runnable {
                commandSender.server.worlds.forEach {
                    it.time = 13000
                }
            })
        } else
            Bukkit.getScheduler().runTask(plugin, Runnable {
                commandSender.world.time = 13000
            })

        plugin.textApi.commandReply(
            commandSender,
            "messages.nightMessage",
            wPrefix = true,
            default = "<prefix> &fTime changed to night!"
        )
    }
}
package me.xap3y.xacore.utils

import me.xap3y.xacore.Main
import org.bukkit.Location
import org.bukkit.command.CommandSender

// This class will prevent code duplication and make the code more readable
class Helper(private val plugin: Main) {

    fun getSpawnLocation(): Location? {

        val world = plugin.config.getString("spawn.world") ?: return null
        val x = plugin.config.getDouble("spawn.x")
        val y = plugin.config.getDouble("spawn.y")
        val z = plugin.config.getDouble("spawn.z")
        val yaw = plugin.config.getDouble("spawn.yaw")
        val pitch = plugin.config.getDouble("spawn.pitch")

        return Location(plugin.server.getWorld(world), x, y, z, yaw.toFloat(), pitch.toFloat())

    }

    fun noPermMessage(commandSender: CommandSender, hashMap: HashMap<String, String> = hashMapOf(), wPrefix: Boolean = true) {
        return plugin.textApi.commandReply(
            commandSender,
            "messages.noPermission",
            hashMap,
            wPrefix,
            "<prefix> &cNo permission!"
        )
    }

    fun onlyPlayersMessage(commandSender: CommandSender, wPrefix: Boolean = true) {
        return plugin.textApi.commandReply(
            commandSender,
            "messages.onlyPlayers",
            wPrefix = true,
            default = "<prefix> &cOnly players can use this command!"
        )
    }

    fun wrongUsageMessage(commandSender: CommandSender, usage: String, wPrefix: Boolean = true) {
        return plugin.textApi.commandReply(
            commandSender,
            "messages.wrongUsage",
            hashMapOf("usage" to usage),
            wPrefix,
            "<prefix> &cWrong usage! &7<usage>"
        )
    }

    fun getWhisperFormats(): Pair<String, String> {
        val formatFrom = plugin.storageManager.getMessage(
            "messages.whisperFormatFrom",
            "&7From &6<player> &7>> &r<message>",
            null
        )

        val formatTo = plugin.storageManager.getMessage(
            "messages.whisperFormatTo",
            "&7To &6<player> &7>> &r<message>",
            null
        )

        return Pair(formatFrom, formatTo)
    }

    fun interruptEventOnFlag(worldName: String, flag: String): Boolean {
        val cfgWorld = plugin.config.getConfigurationSection("perWorldSettings.$worldName")
        val cfgWildcard = plugin.config.getConfigurationSection("perWorldSettings.*")

        if (cfgWorld === null && cfgWildcard === null) return false

        val onFlagWorld = cfgWorld?.getBoolean(flag)
        val flagWildcard = cfgWildcard?.getBoolean(flag)

        return onFlagWorld == true || (onFlagWorld === null && flagWildcard == true)
    }
}
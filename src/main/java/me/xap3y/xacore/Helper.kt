package me.xap3y.xacore

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

}
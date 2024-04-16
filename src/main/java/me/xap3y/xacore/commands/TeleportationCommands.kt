package me.xap3y.xacore.commands

import me.xap3y.xacore.Main
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class TeleportationCommands(private val plugin: Main) {

    @Command("setspawn")
    @CommandDescription("Set the spawn location")
    @Permission(value = ["xacore.setspawn", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onSetSpawnCommand(commandSender: CommandSender) {
        if (commandSender !is Player)
            return plugin.helper.onlyPlayersMessage(commandSender)

        /*plugin.config.set("spawn.world", commandSender.world.name)
        plugin.config.set("spawn.x", commandSender.location.x)
        plugin.config.set("spawn.y", commandSender.location.y)
        plugin.config.set("spawn.z", commandSender.location.z)
        plugin.config.set("spawn.yaw", commandSender.location.yaw)
        plugin.config.set("spawn.pitch", commandSender.location.pitch)
        plugin.config.save(plugin.configFile)*/

        val saved: Boolean = plugin.storageManager.setLocationToStorage("spawn", commandSender.location)

        if (!saved) return plugin.helper.errorMessage(commandSender)

        plugin.textApi.commandReply(
            commandSender,
            "messages.setSpawn",
            wPrefix = true,
            default = "<prefix> &fSpawn set!"
        )
    }

    @Command("spawn")
    @CommandDescription("Teleport to the spawn location")
    //@Permission(value = ["xacore.spawn", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onSpawnCommand(commandSender: CommandSender) {
        if (commandSender !is Player)
            return plugin.helper.onlyPlayersMessage(commandSender)

        val spawnLocation = plugin.storageManager.getLocationFromStorage("spawn") ?: return plugin.textApi.commandReply(
            commandSender,
            "messages.noSpawn",
            wPrefix = true,
            default = "<prefix> &cSpawn not set"
        )

        // Sync
        plugin.server.scheduler.runTask(plugin, Runnable {
            commandSender.teleport(spawnLocation)
        })

        plugin.textApi.commandReply(
            commandSender,
            "messages.spawn",
            wPrefix = true,
            default = "<prefix> &fTeleported to spawn!"
        )
    }

    @Command("home [name]")
    @CommandDescription("Teleport to your home location")
    @Permission(value = ["xacore.home", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onHomeCommand(
        commandSender: CommandSender,
        @Argument("name") homeName: String? = null,
    ) {
        if (commandSender !is Player)
            return plugin.helper.onlyPlayersMessage(commandSender)

        /*val homeLocation: Location = plugin.storageManager.getLocationFromStorage("homes.${commandSender.uniqueId}.main") ?:
            return plugin.textApi.commandReply(
                commandSender,
                "messages.noHome",
                wPrefix = true,
                default = "<prefix> &cHome not set"
            )*/

        // To je v pici

        // This just check if player has already tried to teleport to the home location, and if not, it tries to load it from the file
        // If it's still null, it returns the error message and save the result in the playerHomes map, so it never tries to load it from the file again
        val homeName: String = if (homeName === null) "main" else "c-$homeName"
        val isHomeInit: Boolean = plugin.playerHomes[commandSender.uniqueId.toString()]?.containsKey(homeName) ?: false
        var homeLocation: Location? = plugin.playerHomes[commandSender.uniqueId.toString()]?.get(homeName)

        plugin.storageManager.logInfo(
            "[HOME] REQ! P: ${commandSender.name} HN: $homeName  IHI: $isHomeInit  LOC: $homeLocation",
            true
        )

        if (homeLocation == null && !isHomeInit) {

            plugin.storageManager.logInfo(
                "[HOME] HOME NOT FOUND! TRYING FROM FILE! P: ${commandSender.name} HN: $homeName",
                true
            )

            val homeLocationFromFile: Location? = plugin.storageManager.getLocationFromStorage("homes.${commandSender.uniqueId}.$homeName")

            plugin.playerHomes[commandSender.uniqueId.toString()]?.set(homeName, homeLocationFromFile)

            if (homeLocationFromFile !== null)
                homeLocation = homeLocationFromFile

        } else if (homeLocation === null)
            plugin.storageManager.logInfo(
                "[HOME] HOME NOT FOUND + ALREADY INITIALIZED! P: ${commandSender.name} HN: $homeName",
                true
            )

        if (homeLocation === null) {

            plugin.storageManager.logInfo(
                "[HOME] RETURNING (NULL CHECK)! P: ${commandSender.name} HN: $homeName",
                true
            )

            return plugin.textApi.commandReply(
                commandSender,
                if (homeName == "main") "messages.noHome" else "messages.noCustomHome",
                hashMapOf("home" to if (homeName == "main") "main" else homeName.substring(2)),
                wPrefix = true,
                default = "<prefix> &cHome not set"
            )
        }

        plugin.storageManager.logInfo(
            "[HOME] P: ${commandSender.name} LOC: $homeLocation  IHI: $isHomeInit  HN: $homeName",
            true
        )

        if (!homeLocation.isWorldLoaded)
            return plugin.helper.errorMessage(commandSender)

        // Sync
        plugin.server.scheduler.runTask(plugin, Runnable {
            commandSender.teleport(homeLocation)
        })

        plugin.textApi.commandReply(
            commandSender,
            if (homeName == "main") "messages.homeTeleport" else "messages.customHomeTeleport",
            hashMapOf("home" to if (homeName == "main") "main" else homeName.substring(2)),
            wPrefix = true,
            default = if (homeName == "main") "<prefix> &fTeleported to home" else "<prefix> &fTeleported to home named &e<home>"
        )
    }

    @Command("sethome [name]")
    @CommandDescription("Set your home location")
    @Permission(value = ["xacore.sethome", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onSetHomeCommand(
        commandSender: CommandSender,
        @Argument("name") homeName: String? = null,
    ) {
        if (commandSender !is Player)
            return plugin.helper.onlyPlayersMessage(commandSender)

        val homeLocation: Location = commandSender.location
        val homeName: String = if (homeName === null) "main" else "c-$homeName"

        val saved: Boolean = plugin.storageManager.setLocationToStorage("homes.${commandSender.uniqueId}.$homeName", homeLocation)

        if (!saved) return plugin.helper.errorMessage(commandSender)

        val player: HashMap<String, Location?>? = plugin.playerHomes[commandSender.uniqueId.toString()]

        if (player === null)
            plugin.playerHomes[commandSender.uniqueId.toString()] = hashMapOf(homeName to homeLocation)
        else
            player[homeName] = homeLocation

        plugin.textApi.commandReply(
            commandSender,
            if (homeName == "main") "messages.setHome" else "messages.setCustomHome",
            hashMapOf("home" to homeName),
            wPrefix = true,
            default = if (homeName == "main") "<prefix> &fHome set" else "<prefix> &fHome &e<home> &fset"
        )
    }

    @Command("delhome [name]")
    @CommandDescription("Delete your home location")
    @Permission(value = ["xacore.delhome", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onDelHomeCommand(
        commandSender: CommandSender,
        @Argument("name") homeName: String? = null,
    ) {
        if (commandSender !is Player)
            return plugin.helper.onlyPlayersMessage(commandSender)

        val homeName: String = if (homeName === null) "main" else "c-$homeName"

        val player: HashMap<String, Location?>? = plugin.playerHomes[commandSender.uniqueId.toString()]

        if (player === null || player[homeName] === null)
            return plugin.textApi.commandReply(
                commandSender,
                if (homeName == "main") "messages.noHome" else "messages.noCustomHome",
                hashMapOf("home" to homeName),
                wPrefix = true,
                default = "<prefix> &cHome not set"
            )

        player[homeName] = null

        if (plugin.storageManager.storageYML == null)
            return plugin.helper.errorMessage(commandSender)

        // Yo how is it still possibly null, I just checked it
        plugin.storageManager.storageYML?.set("homes.${commandSender.uniqueId}.$homeName", null)
        plugin.storageManager.storageYML?.save(plugin.storageFile)

        plugin.textApi.commandReply(
            commandSender,
            if (homeName == "main") "messages.delHome" else "messages.delCustomHome",
            hashMapOf("home" to homeName),
            wPrefix = true,
            default = if (homeName == "main") "<prefix> &fHome deleted" else "<prefix> &fHome &e<home> &fdeleted"
        )
    }
}
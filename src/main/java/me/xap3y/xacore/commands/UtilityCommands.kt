package me.xap3y.xacore.commands

import me.xap3y.xacore.Main
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class UtilityCommands(private val plugin: Main) {



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

    @Command("fly")
    @CommandDescription("Toggle fly mode")
    @Permission(value = ["xacore.fly", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onFlyCommand(commandSender: CommandSender) {
        if (commandSender !is Player)
            return plugin.textApi.commandReply(
                commandSender,
                "messages.onlyPlayers",
                wPrefix = true
            )

        commandSender.allowFlight = !commandSender.allowFlight

        plugin.textApi.commandReply(
            commandSender,
            "messages.flyToggle",
            hashMapOf("status" to if (commandSender.allowFlight) "&aenabled" else "&cdisabled"),
            true,
            "<prefix> &fFly mode &r<status>&f!"
        )
    }

    @Command("speed <speed>")
    @CommandDescription("Change your speed")
    @Permission(value = ["xacore.speed", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onSpeedCommand(
        commandSender: CommandSender,
        @Argument("speed") speed: Int,
    ) {
        if (commandSender !is Player)
            return plugin.textApi.commandReply(
                commandSender,
                "messages.onlyPlayers",
                wPrefix = true
            )

        if (speed < 0 || speed > 20)
            return plugin.textApi.commandReply(
                commandSender,
                "messages.speedRange",
                hashMapOf("speed" to speed.toString()),
                true,
                "<prefix> &cSpeed must be between 0 and 20!"
            )

        val realSpeed = if (speed == 0) if (commandSender.isFlying) 2/20f else 4/20f else speed/20f

        if (commandSender.isFlying)
            commandSender.flySpeed = realSpeed
        else
            commandSender.walkSpeed = realSpeed


        plugin.textApi.commandReply(
            commandSender,
            "messages.speedMessage",
            hashMapOf("speed" to realSpeed.toString()),
            true,
            "<prefix> &fYour speed has been set to &e<speed>&f!"
        )
    }

    @Command("heal [player]")
    @CommandDescription("Heal yourself or another player")
    @Permission(value = ["xacore.heal", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onHealCommand(
        commandSender: CommandSender,
        @Argument("player") player: Player? = null,
    ) {
        if (commandSender !is Player && player == null)
            return plugin.textApi.commandReply(
                commandSender,
                "messages.wrongUsage",
                hashMapOf("usage" to "/xc heal <player>"),
                true
            )

        val target = player ?: commandSender as Player

        target.health = target.maxHealth
        target.foodLevel = 20

        if (player == null || (commandSender is Player && commandSender.uniqueId.toString() == target.uniqueId.toString())) {
            plugin.textApi.commandReply(
                commandSender,
                "messages.healSelf",
                hashMapOf("player" to target.name),
                true,
                "<prefix> &fYou have healed &e<player>&f!"
            )
        } else {
            plugin.textApi.commandReply(
                commandSender,
                "messages.healOther",
                hashMapOf("player" to target.name),
                true,
                "<prefix> &fYou have healed &e<player>&f!"
            )
        }
    }

    @Command("feed [player]")
    @CommandDescription("Feed yourself or another player")
    @Permission(value = ["xacore.feed", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onFeedCommand(
        commandSender: CommandSender,
        @Argument("player") player: Player? = null,
    ) {
        if (commandSender !is Player && player == null)
            return plugin.textApi.commandReply(
                commandSender,
                "messages.wrongUsage",
                hashMapOf("usage" to "/xc feed <player>"),
                true
            )

        val target = player ?: commandSender as Player

        target.foodLevel = 20

        if (player == null || (commandSender is Player && commandSender.uniqueId.toString() == target.uniqueId.toString())) {
            plugin.textApi.commandReply(
                commandSender,
                "messages.feedSelf",
                hashMapOf("player" to target.name),
                true,
                "<prefix> &fYou have fed yourself!"
            )
        } else {
            plugin.textApi.commandReply(
                commandSender,
                "messages.feedOther",
                hashMapOf("player" to target.name),
                true,
                "<prefix> &fYou have fed &e<player>&f!"
            )
        }
    }

    @Command("whois <player>")
    @CommandDescription("Get information about a player")
    @Permission(value = ["xacore.whois", "xacore.*"], mode = Permission.Mode.ANY_OF)
    fun onWhoisCommand(
        commandSender: CommandSender,
        @Argument("player") player: Player,
    ) {
        val location = player.location
        val world = location.world.name
        val uuid = player.uniqueId.toString()
        val isFlying = player.isFlying
        val ip = player.address.hostString

        val list = plugin.storageManager.getList("menus.whois", player) ?: return // TODO

        list.forEach {
            commandSender.sendMessage(
                plugin.textApi.coloredMessage(
                    plugin.textApi.replace(
                        it.toString(),
                        hashMapOf(
                            "player" to player.name,
                            "world" to world,
                            "uuid" to uuid,
                            "isFlying" to isFlying.toString(),
                            "ip" to ip,
                            "location" to "${"%.2f".format(location.x)}, ${"%.2f".format(location.y)}, ${"%.2f".format(location.z)}",
                            "gamemode" to player.gameMode.name.lowercase(),
                            "health" to player.health.toString(),
                            "hunger" to player.foodLevel.toString(),
                            "speed" to if (player.isFlying) player.flySpeed.toString() else player.walkSpeed.toString(),
                        ),
                        true
                    )
                )
            )
        }
    }
}
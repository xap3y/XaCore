package me.xap3y.xacore.listeners

import me.xap3y.xacore.Main
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent

class FoodLevelChangeListener(private val plugin: Main): Listener {

    @EventHandler
    fun onHungerChange(e: FoodLevelChangeEvent) {

        val player = e.entity as Player
        val interrupt = plugin.helper.interruptEventOnFlag(player.world.name, "noHunger")

        plugin.storageManager.logInfo(
            "[EVENT] FoodLevelChangeEvent > FIRED - ${e.entity.name} INTERRUPT: $interrupt  FL: ${e.foodLevel}",
            true)

        if (interrupt){
            if (e.foodLevel < 20) e.foodLevel = 20
            e.isCancelled = true
        }
    }
}
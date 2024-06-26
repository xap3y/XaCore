package me.xap3y.xacore.listeners

import me.xap3y.xacore.Main
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class EntityDamageListener(private val plugin: Main): Listener {

    @EventHandler
    fun onEntityDamage(e: EntityDamageEvent) {

        plugin.storageManager.logInfo(
            "[EVENT] EntityDamageEvent > FIRED - ${e.entity.name}",
            true)

        if (e.entity !is Player) return

        val cause = e.cause
        if (cause == EntityDamageEvent.DamageCause.FALL) {
            val interrupt = plugin.helper.interruptEventOnFlag(e.entity.world.name, "nofall")
            plugin.storageManager.logInfo(
                "[EVENT] EntityDamageEvent > ${e.entity.name} -- INTERRUPT $interrupt",
                true)
            if (interrupt) e.isCancelled = true
        }
    }
}
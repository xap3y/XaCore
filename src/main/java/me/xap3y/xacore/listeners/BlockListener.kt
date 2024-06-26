package me.xap3y.xacore.listeners

import me.xap3y.xacore.Main
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class BlockListener(private val plugin: Main): Listener {

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {

        val interrupt = plugin.helper.interruptEventOnFlag(e.block.world.name, "blockPlace")
        plugin.storageManager.logInfo(
            "[EVENT] BlockPlaceEvent > ${e.player.name} -- INTERRUPT $interrupt",
            true)

        if (interrupt) e.isCancelled = true
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {

        val interrupt = plugin.helper.interruptEventOnFlag(e.block.world.name, "blockBreak")
        plugin.storageManager.logInfo(
            "[EVENT] BlockBreakEvent > ${e.player.name} -- INTERRUPT $interrupt",
            true)

        if (interrupt) e.isCancelled = true
    }
}
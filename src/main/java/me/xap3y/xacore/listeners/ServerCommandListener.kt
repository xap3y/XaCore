package me.xap3y.xacore.listeners

import me.xap3y.xacore.Main
import me.xap3y.xacore.api.config.StorageManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerCommandEvent

class ServerCommandListener(private val storageManager: StorageManager): Listener {

    @EventHandler
    fun onConsoleCommandPreprocess(e: ServerCommandEvent) {
        storageManager.logInfo("[CMD] CONSOLE |>> ${e.command}")
    }
}
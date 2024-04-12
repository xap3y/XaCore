package me.xap3y.xacore.hooks

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.xap3y.xacore.Main
import org.bukkit.OfflinePlayer

class PlaceholderAPI(private val plugin: Main): PlaceholderExpansion() {
    override fun getIdentifier(): String = "xacore"

    override fun getAuthor(): String = "XAP3Y"

    override fun getVersion(): String = "v0.1"

    override fun onRequest(player: OfflinePlayer?, identifier: String): String? {
        if (player == null) return null
        //val args = identifier.split("_")
        //val rank = args.getOrNull(2)?.toIntOrNull()

        return when (identifier) {
            "ver" -> getVersion()
            "author" -> getAuthor()
            else -> null
        }
    }
}
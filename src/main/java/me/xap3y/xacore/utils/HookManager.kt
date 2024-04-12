package me.xap3y.xacore.utils

import me.xap3y.xacore.Main
import me.xap3y.xacore.hooks.PlaceholderAPI
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.plugin.RegisteredServiceProvider

class HookManager(private val plugin: Main) {

    fun hookPAPI(): Boolean {
        if (plugin.server.pluginManager.getPlugin("PlaceholderAPI") !== null) {
            PlaceholderAPI(plugin).register()
            plugin.textApi.console("<prefix> &aHooked into PlaceholderAPI")
            return true
        } else {
            plugin.textApi.console("<prefix> &cPlaceholderAPI not found! Disabling support...")
        }
        return false
    }

    fun hookVault(): Pair<Chat, Permission>? {
        if (plugin.server.pluginManager.getPlugin("Vault") !== null) {
            //plugin.textApi.console("<prefix> &aVault found! Enabling support...")
            val chat = setupVaultChat()
            val permission = setupVaultPermissions()
            if (chat === null || permission === null) {
                plugin.textApi.console("<prefix> &cVault found but no permissions or chat plugin found! Disabling support...")
                plugin.useVault = false
            } else {
                plugin.textApi.console("<prefix> &aHooked into Vault")
                return Pair(chat, permission)
            }
        } else {
            plugin.useVault = false
            plugin.textApi.console("<prefix> &cVault not found! Disabling support...")
        }
        return null
    }

    private fun setupVaultChat(): Chat? {
        val rsp: RegisteredServiceProvider<Chat>? = Bukkit.getServer().servicesManager.getRegistration(Chat::class.java)
        return rsp?.provider
    }

    private fun setupVaultPermissions(): Permission? {
        val rsp: RegisteredServiceProvider<Permission>? = Bukkit.getServer().servicesManager.getRegistration(Permission::class.java)
        return rsp?.provider
    }
}
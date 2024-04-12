package me.xap3y.xacore

import me.xap3y.xacore.api.config.ConfigManager
import me.xap3y.xacore.api.text.Texter
import me.xap3y.xacore.commands.*
import me.xap3y.xacore.listeners.PlayerChatListener
import me.xap3y.xacore.listeners.PlayerCommandPreprocessListener
import me.xap3y.xacore.listeners.PlayerJoinListener
import me.xap3y.xacore.listeners.PlayerQuitListener
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.bukkit.CloudBukkitCapabilities
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import java.io.File

class Main : JavaPlugin() {

    lateinit var configManager: ConfigManager
    lateinit var configFile: File
    lateinit var messageFile: File

    var chatLocked = false
    val textApi: Texter by lazy { Texter(this) }
    val helper: Helper by lazy { Helper(this) }

    var chat: Chat? = null
    var permission: Permission? = null


    var useVault = false

    override fun onEnable() {

        if (!dataFolder.exists())
            dataFolder.mkdir()

        configFile = File(dataFolder, "config.yml")
        messageFile = File(dataFolder, "lang.yml")

        configManager = ConfigManager(this)
        configManager.loadConfig()
        configManager.loadLang()

        useVault = config.getBoolean("hookVault")

        val commandManager = createCommandManager()
        val annotationParser = createAnnotationParser(commandManager)

        annotationParser.parse(RootCommand(this))
        annotationParser.parse(Gamemodes(this))
        annotationParser.parse(WeatherCommands(this))
        annotationParser.parse(AdminCommands(this))
        annotationParser.parse(UtilityCommands(this))

        regiserListeners()

        if (useVault) {
            if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                textApi.console("<prefix> &aVault found! Enabling support...")
                if (!setupChat() || !setupPermissions()) {
                    textApi.console("<prefix> &cVault found but no permissions or chat plugin found! Disabling support...")
                    useVault = false
                }
            } else {
                useVault = false
                textApi.console("<prefix> &cVault not found! Disabling support...")
            }
        }

    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun regiserListeners() {
        val list: Set<Listener> = setOf(
            PlayerJoinListener(this),
            PlayerQuitListener(this),
            PlayerChatListener(this),
            PlayerCommandPreprocessListener(this)
        )

        val pm: PluginManager = server.pluginManager

        list.forEach {
            pm.registerEvents(it, this)
        }
    }

    private fun createCommandManager(): PaperCommandManager<CommandSender> {
        val executionCoordinatorFunction = ExecutionCoordinator.asyncCoordinator<CommandSender>()
        val mapperFunction = SenderMapper.identity<CommandSender>()
        val commandManager = PaperCommandManager(
            this,
            executionCoordinatorFunction,
            mapperFunction
        )
        if (commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            commandManager.registerBrigadier()
            commandManager.brigadierManager().setNativeNumberSuggestions(false)
        }
        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            (commandManager as PaperCommandManager<*>).registerAsynchronousCompletions()
        }
        return commandManager
    }

    private fun createAnnotationParser(commandManager: PaperCommandManager<CommandSender>): org.incendo.cloud.annotations.AnnotationParser<CommandSender> {
        return org.incendo.cloud.annotations.AnnotationParser(
            commandManager,
            CommandSender::class.java
        )
    }

    private fun setupChat(): Boolean {
        val rsp: RegisteredServiceProvider<Chat>? = Bukkit.getServer().servicesManager.getRegistration(Chat::class.java)
        chat = rsp?.provider
        return chat != null
    }

    private fun setupPermissions(): Boolean {
        val rsp: RegisteredServiceProvider<Permission>? = Bukkit.getServer().servicesManager.getRegistration(Permission::class.java)
        permission = rsp?.provider
        return permission != null
    }


    fun isConfigFileInit() = ::configFile.isInitialized
    fun isLangFileInit() = ::messageFile.isInitialized
}

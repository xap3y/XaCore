package me.xap3y.xacore

import me.xap3y.xacore.api.config.ConfigManager
import me.xap3y.xacore.api.text.Texter
import me.xap3y.xacore.commands.*
import me.xap3y.xacore.listeners.PlayerChatListener
import me.xap3y.xacore.listeners.PlayerCommandPreprocessListener
import me.xap3y.xacore.listeners.PlayerJoinListener
import me.xap3y.xacore.listeners.PlayerQuitListener
import me.xap3y.xacore.utils.Helper
import me.xap3y.xacore.utils.HookManager
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginManager
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

    val whisperPlayers: HashMap<CommandSender, CommandSender> = hashMapOf()

    lateinit var vaultPair: Pair<Chat, Permission>

    var useVault = false
    var usePapi = false

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

        registerListeners()

        // Hooks
        val hookManager = HookManager(this)

        if (useVault) {
            val pair = hookManager.hookVault()

            if (pair !== null)
                vaultPair = pair
            else
                if (useVault) useVault = false
        }

        if (config.getBoolean("hookPlaceholderAPI")) {
            if (hookManager.hookPAPI())
                usePapi = true
            else
                if (usePapi) usePapi = false
        }


    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun registerListeners() {
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


    fun isConfigFileInit() = ::configFile.isInitialized
    fun isLangFileInit() = ::messageFile.isInitialized
    fun isVaultPairInit() = ::vaultPair.isInitialized
}

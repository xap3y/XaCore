package me.xap3y.xacore

import me.xap3y.xacore.api.config.StorageManager
import me.xap3y.xacore.api.enums.CommandCategory
import me.xap3y.xacore.api.text.Texter
import me.xap3y.xacore.commands.*
import me.xap3y.xacore.listeners.*
import me.xap3y.xacore.utils.Helper
import me.xap3y.xacore.utils.HookManager
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.apache.logging.log4j.CloseableThreadContext.Instance
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

    lateinit var storageManager: StorageManager
    lateinit var configFile: File
    lateinit var messageFile: File
    lateinit var logFile: File
    lateinit var vaultPair: Pair<Chat, Permission>

    var chatLocked = false
    var useVault = false
    var usePapi = false

    val helper: Helper by lazy { Helper(this) }
    val textApi: Texter by lazy { Texter(this) }
    val whisperPlayers: HashMap<CommandSender, CommandSender> = hashMapOf()
    val cmdSpyToggles: MutableSet<Player> = mutableSetOf()


    override fun onEnable() {

        if (!dataFolder.exists())
            dataFolder.mkdir()

        configFile = File(dataFolder, "config.yml")
        messageFile = File(dataFolder, "lang.yml")
        logFile = File(dataFolder, "logs.txt")

        storageManager = StorageManager(this)
        storageManager.loadConfig()
        storageManager.loadLang()

        useVault = config.getBoolean("hookVault")

        val commandManager = createCommandManager()
        val annotationParser = createAnnotationParser(commandManager)


        val commandCategories: HashMap<CommandCategory, Any> =
            hashMapOf(
                CommandCategory.GAMEMODE to GamemodeCommands(this),
                CommandCategory.WEATHER to WeatherCommands(this),
                CommandCategory.CHAT to ChatCommands(this),
                CommandCategory.INVENTORY to InventoryCommands(this),
                CommandCategory.UTILS to UtilityCommands(this),
                CommandCategory.TELEPORT to TeleportationCommands(this)
            )

        val list = config.getList("commandCategoriesDisabled") ?: listOf()

        commandCategories.forEach { (k, v) ->
            if (list.contains(k.name.lowercase())) return@forEach
            annotationParser.parse(v)
        }

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

        val time = System.currentTimeMillis()
        val date = java.util.Date(time)
        val humanDate = date.toString()

        storageManager.logInfo("Plugin enabled at $humanDate")

    }

    override fun onDisable() {
        val time = System.currentTimeMillis()
        val date = java.util.Date(time)
        val humanDate = date.toString()

        storageManager.logInfo("Plugin disabled at $humanDate")
    }

    private fun registerListeners() {
        val list: Set<Listener> = setOf(
            PlayerJoinListener(this),
            PlayerQuitListener(this),
            PlayerChatListener(this),
            PlayerCommandPreprocessListener(this),
            EntityDamageListener(this),
            BlockListener(this),
            ServerCommandListener(this.storageManager),
            FoodLevelChangeListener(this)
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
    fun isLogFileInit() = ::logFile.isInitialized
}

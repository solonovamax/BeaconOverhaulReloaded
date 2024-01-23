package gay.solonovamax.beaconsoverhaul

import gay.solonovamax.beaconsoverhaul.beacon.screen.ScreenHandlerRegistry
import gay.solonovamax.beaconsoverhaul.config.BeaconOverhauledConfig
import gay.solonovamax.beaconsoverhaul.config.SerializedBeaconOverhauledConfig
import gay.solonovamax.beaconsoverhaul.effects.StatusEffectRegistry
import gay.solonovamax.beaconsoverhaul.integration.patchouli.PatchouliIntegration
import gay.solonovamax.beaconsoverhaul.mixin.BeaconBlockEntityAccessor
import gay.solonovamax.beaconsoverhaul.mixin.GameRulesAccessor
import gay.solonovamax.beaconsoverhaul.util.configDir
import gay.solonovamax.beaconsoverhaul.util.id
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import net.devtech.arrp.api.RRPCallback
import net.devtech.arrp.api.RuntimeResourcePack
import net.devtech.arrp.json.tags.JTag
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resource.ResourcePack
import net.minecraft.world.GameRules.Category
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.info
import kotlin.io.path.createDirectories
import kotlin.io.path.createDirectory
import kotlin.io.path.inputStream
import kotlin.io.path.notExists
import kotlin.io.path.outputStream
import gay.solonovamax.beaconsoverhaul.mixin.IntRuleAccessor as IntRule


object BeaconOverhaulReloaded : ModInitializer {
    val LONG_REACH_INCREMENT = GameRulesAccessor.register("longReachIncrement", Category.PLAYER, IntRule.create(2))

    val RESOURCE_PACK = RuntimeResourcePack.create(identifierOf("beacon-overhaul"))

    @JvmStatic
    lateinit var config: BeaconOverhauledConfig
        private set

    private val logger by getLogger()

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        encodeDefaults = true
        prettyPrint = true
        coerceInputValues = true
        decodeEnumsCaseInsensitive = true
    }

    private val configDir = FabricLoader.getInstance().configDir(BeaconConstants.NAMESPACE)

    private val modContainer = FabricLoader.getInstance().getModContainer(BeaconConstants.IDENTIFIER)
        .orElseThrow { error("Could not find mod with id ${BeaconConstants.IDENTIFIER}") }

    init {
        loadConfig()
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun loadConfig() {
        if (configDir.notExists())
            configDir.createDirectories()

        val configFile = configDir.resolve("config.json")

        val config = when {
            configFile.notExists() -> writeDefaultConfig()
            else -> try {
                json.decodeFromStream<SerializedBeaconOverhauledConfig>(configFile.inputStream())
            } catch (e: Exception) {
                writeDefaultConfig()
            }
        }

        this.config = BeaconOverhauledConfig.from(config)
    }

    override fun onInitialize() {
        logger.info { "Loading ${BeaconConstants.MOD_NAME}" }


        addStatusEffectsToBeacon()
        createRuntimeResourcepack()

        StatusEffectRegistry.register()
        ScreenHandlerRegistry.register()

        // PatchouliIntegration.multiblockForTier()
    }

    private fun addStatusEffectsToBeacon() {
        val effectsByLevel = arrayOf(
            config.beaconEffectsByTier.tierOne.toTypedArray(),
            config.beaconEffectsByTier.tierTwo.toTypedArray(),
            config.beaconEffectsByTier.tierThree.toTypedArray(),
            config.beaconEffectsByTier.secondaryEffects.toTypedArray(),
        )

        BeaconBlockEntityAccessor.setEffectsByLevel(effectsByLevel)

        BeaconBlockEntityAccessor.setEffects(effectsByLevel.flatMapTo(mutableSetOf()) { it.asIterable() })
    }

    private fun createRuntimeResourcepack() {
        val beaconBaseBlocksTag = JTag().apply {
            for (block in config.beaconBaseBlocks)
                add(block.id)

            RESOURCE_PACK.addTag(identifierOf("minecraft", "blocks/beacon_base_blocks"), this)
        }

        PatchouliIntegration.writePatchouliBook(RESOURCE_PACK)
        // loadPatchouliBook(RESOURCE_PACK)

        RRPCallback.AFTER_VANILLA.register { resourcePacks: MutableList<ResourcePack> ->
            resourcePacks.add(RESOURCE_PACK)
        }

        RESOURCE_PACK.dump()
    }

    // private fun loadPatchouliBook(resourcePack: RuntimeResourcePack) {
    //     val fabricXplatMod = FabricXplatModContainer(modContainer)
    //     val bookResourceSupplier = resourcePack.open(ResourceType.SERVER_DATA, PatchouliIntegration.PATCHOULI_BOOK_JSON)
    //     if (bookResourceSupplier != null) {
    //         logger.info { "Loading patchouli book" }
    //         BookRegistry.INSTANCE.loadBook(fabricXplatMod, PatchouliIntegration.GUIDE_IDENTIFIER, bookResourceSupplier.get(), false)
    //     } else {
    //         logger.info { "Patchouli book could not be loaded" }
    //     }
    // }

    @OptIn(ExperimentalSerializationApi::class)
    private fun writeConfig(config: SerializedBeaconOverhauledConfig) {
        val configDir = FabricLoader.getInstance().configDir(BeaconConstants.NAMESPACE)

        if (configDir.notExists())
            configDir.createDirectory()

        val configFile = configDir.resolve("config.json")

        json.encodeToStream<SerializedBeaconOverhauledConfig>(config, configFile.outputStream())
    }

    private fun writeDefaultConfig(): SerializedBeaconOverhauledConfig {
        return BeaconConstants.DEFAULT_CONFIG.also { config ->
            writeConfig(config)
        }
    }
}

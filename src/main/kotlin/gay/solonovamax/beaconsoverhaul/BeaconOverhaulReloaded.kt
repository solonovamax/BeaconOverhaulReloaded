package gay.solonovamax.beaconsoverhaul

import gay.solonovamax.beaconsoverhaul.config.BeaconOverhauledConfig
import gay.solonovamax.beaconsoverhaul.effects.StatusEffectRegistry
import gay.solonovamax.beaconsoverhaul.mixin.BeaconBlockEntityAccessor
import gay.solonovamax.beaconsoverhaul.mixin.GameRulesAccessor
import gay.solonovamax.beaconsoverhaul.mixin.IntRuleAccessor
import gay.solonovamax.beaconsoverhaul.util.configDir
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.world.GameRules
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.info
import kotlin.io.path.createDirectories
import kotlin.io.path.createDirectory
import kotlin.io.path.inputStream
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

object BeaconOverhaulReloaded : ModInitializer {
    val LONG_REACH_INCREMENT = GameRulesAccessor.register("longReachIncrement", GameRules.Category.PLAYER, IntRuleAccessor.create(2))
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

    init {
        loadConfig()

        logger.info { "Here is the config: $config" }
    }

    private fun addStatusEffectsToBeacon() {
        val effects = BeaconBlockEntity.EFFECTS_BY_LEVEL
        effects[0] = arrayOf(*effects[0], StatusEffects.NIGHT_VISION)
        effects[1] = arrayOf(*effects[1], StatusEffectRegistry.LONG_REACH)
        effects[2] = arrayOf(*effects[2], StatusEffectRegistry.NUTRITION)
        effects[3] = arrayOf(*effects[3], StatusEffects.FIRE_RESISTANCE, StatusEffects.SLOW_FALLING)

        BeaconBlockEntityAccessor.setEffects(effects.flatMapTo(mutableSetOf()) { it.asIterable() })
    }

    override fun onInitialize() {
        addStatusEffectsToBeacon()

        StatusEffectRegistry.register()
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun loadConfig() {

        if (configDir.notExists())
            configDir.createDirectories()

        val configFile = configDir.resolve("config.json")

        config = when {
            configFile.notExists() -> writeDefaultConfig()
            else -> try {
                json.decodeFromStream<BeaconOverhauledConfig>(configFile.inputStream())
            } catch (e: Exception) {
                writeDefaultConfig()
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun writeConfig(config: BeaconOverhauledConfig) {
        val configDir = FabricLoader.getInstance().configDir(BeaconConstants.NAMESPACE)

        if (configDir.notExists())
            configDir.createDirectory()

        val configFile = configDir.resolve("config.json")

        json.encodeToStream<BeaconOverhauledConfig>(config, configFile.outputStream())
    }

    private fun writeDefaultConfig(): BeaconOverhauledConfig {
        return BeaconOverhauledConfig.DEFAULT.also { config ->
            writeConfig(config)
        }
    }
}

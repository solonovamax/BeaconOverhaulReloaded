package gay.solonovamax.beaconsoverhaul.config

import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import dev.isxander.yacl3.dsl.YetAnotherConfigLib
import dev.isxander.yacl3.dsl.binding
import gay.solonovamax.beaconsoverhaul.BeaconConstants
import gay.solonovamax.beaconsoverhaul.config.BeaconConfig.AmplifierExpression
import gay.solonovamax.beaconsoverhaul.config.BeaconConfig.ModifierExpression
import gay.solonovamax.beaconsoverhaul.config.screen.BeaconExpressionController
import gay.solonovamax.beaconsoverhaul.config.screen.BlockController
import gay.solonovamax.beaconsoverhaul.config.screen.StatusEffectController
import gay.solonovamax.beaconsoverhaul.util.FabricLoader
import gay.solonovamax.beaconsoverhaul.util.binding
import gay.solonovamax.beaconsoverhaul.util.defaultDescription
import gay.solonovamax.beaconsoverhaul.util.registeringList
import io.github.xn32.json5k.Json5
import io.github.xn32.json5k.decodeFromStream
import io.github.xn32.json5k.encodeToStream
import net.minecraft.block.Blocks
import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.effect.StatusEffects
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.info
import java.nio.file.StandardCopyOption
import kotlin.io.path.copyTo
import kotlin.io.path.createDirectories
import kotlin.io.path.inputStream
import kotlin.io.path.name
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

object ConfigManager {
    private val logger by getLogger()

    @JvmStatic
    val beaconConfig: BeaconConfig by lazy {
        tryLoadConfig("beacon", BeaconConstants.DEFAULT_BEACON_CONFIG)
    }

    @JvmStatic
    val conduitConfig: ConduitConfig by lazy {
        tryLoadConfig("conduit", BeaconConstants.DEFAULT_CONDUIT_CONFIG)
    }

    fun createConfigScreen(parent: Screen): Screen {
        @Suppress("unused", "UnusedVariable")
        val config = YetAnotherConfigLib("beaconoverhaul") {
            val beaconCategory by categories.registering("beacon") {
                val expressionsGroup by groups.registering("expressions") {
                    val range by options.registering("range") {
                        binding(beaconConfig::range, BeaconConstants.DEFAULT_BEACON_CONFIG.range)
                        customController { option -> BeaconExpressionController(option, ::ModifierExpression) }
                        defaultDescription()
                    }
                    val duration by options.registering("duration") {
                        binding(beaconConfig::duration, BeaconConstants.DEFAULT_BEACON_CONFIG.duration)
                        customController { option -> BeaconExpressionController(option, ::ModifierExpression) }
                        defaultDescription()
                    }
                    val primaryAmplifier by options.registering("primary_amplifier") {
                        binding(beaconConfig::primaryAmplifier, BeaconConstants.DEFAULT_BEACON_CONFIG.primaryAmplifier)
                        customController { option -> BeaconExpressionController(option, ::AmplifierExpression) }
                        defaultDescription()
                    }
                    val secondaryAmplifier by options.registering("secondary_amplifier") {
                        binding(beaconConfig::secondaryAmplifier, BeaconConstants.DEFAULT_BEACON_CONFIG.secondaryAmplifier)
                        customController { option -> BeaconExpressionController(option, ::AmplifierExpression) }
                        defaultDescription()
                    }
                }

                val miscGroup by groups.registering("misc") {
                    val maxBeaconLayers by options.registering("max_beacon_layers") {
                        binding(beaconConfig::maxBeaconLayers, BeaconConstants.DEFAULT_BEACON_CONFIG.maxBeaconLayers)
                        controller { option -> IntegerSliderControllerBuilder.create(option).range(1, 10).step(1) }
                        defaultDescription()
                    }
                    val effectParticles by options.registering("effect_particles") {
                        binding(beaconConfig::effectParticles, BeaconConstants.DEFAULT_BEACON_CONFIG.effectParticles)
                        controller(TickBoxControllerBuilder::create)
                        defaultDescription()
                    }
                    val redirectionHorizontalMoveLimit by options.registering("redirection_horizontal_move_limit") {
                        binding(
                            beaconConfig::redirectionHorizontalMoveLimit,
                            BeaconConstants.DEFAULT_BEACON_CONFIG.redirectionHorizontalMoveLimit
                        )
                        controller { option -> IntegerSliderControllerBuilder.create(option).range(1, 256).step(1) }
                        defaultDescription()
                    }
                    val tintedGlassTransparency by options.registering("allow_tinted_glass_transparency") {
                        binding(
                            beaconConfig::allowTintedGlassTransparency,
                            BeaconConstants.DEFAULT_BEACON_CONFIG.allowTintedGlassTransparency
                        )
                        controller(TickBoxControllerBuilder::create)
                        defaultDescription()
                    }
                }

                val beamGroup by groups.registering("beam") {
                    val beamUpdateFrequency by options.registering("beam_update_frequency") {
                        binding(beaconConfig::beamUpdateFrequency, BeaconConstants.DEFAULT_BEACON_CONFIG.beamUpdateFrequency)
                        controller { option -> IntegerSliderControllerBuilder.create(option).range(1, 32).step(1) }
                        defaultDescription()
                    }
                    val beamRadius by options.registering("beam_radius") {
                        binding(beaconConfig::beamRadius, BeaconConstants.DEFAULT_BEACON_CONFIG.beamRadius)
                        controller { option -> DoubleSliderControllerBuilder.create(option).range(0.0, 1.0).step(0.01) }
                        defaultDescription()
                    }
                    val beamGlowRadius by options.registering("beam_glow_radius") {
                        binding(beaconConfig::beamGlowRadius, BeaconConstants.DEFAULT_BEACON_CONFIG.beamGlowRadius)
                        controller { option -> DoubleSliderControllerBuilder.create(option).range(0.0, 1.0).step(0.01) }
                        defaultDescription()
                    }
                    val beamGlowOpacity by options.registering("beam_glow_opacity") {
                        binding(beaconConfig::beamGlowOpacity, BeaconConstants.DEFAULT_BEACON_CONFIG.beamGlowOpacity)
                        controller { option -> DoubleSliderControllerBuilder.create(option).range(0.0, 1.0).step(0.001) }
                        defaultDescription()
                    }
                    val beamBlendPadding by options.registering("beam_blend_padding") {
                        binding(beaconConfig::beamBlendPadding, BeaconConstants.DEFAULT_BEACON_CONFIG.beamBlendPadding)
                        controller { option -> DoubleSliderControllerBuilder.create(option).range(0.0, 1.0).step(0.001) }
                        defaultDescription()
                    }
                }

                val levelOneStatusEffects by rootOptions.registeringList("level_one_status_effects") {
                    binding(beaconConfig::levelOneStatusEffects, BeaconConstants.DEFAULT_BEACON_CONFIG.levelOneStatusEffects)
                    customController { option -> StatusEffectController(option) }
                    initial(StatusEffects.SPEED)
                    defaultDescription()
                    collapsed(true)
                }
                val beaconBaseBlocks by rootOptions.registeringList("beacon_base_blocks") {
                    binding(beaconConfig::beaconBaseBlocks, BeaconConstants.DEFAULT_BEACON_CONFIG.beaconBaseBlocks)
                    customController { option -> BlockController(option) }
                    initial(Blocks.DIAMOND_BLOCK)
                    defaultDescription()
                    collapsed(true)
                }
                val tierOneBeaconEffects by rootOptions.registeringList("tier_one_beacon_effects") {
                    binding(beaconConfig.beaconEffectsByTier::tierOne, BeaconConstants.DEFAULT_BEACON_CONFIG.beaconEffectsByTier.tierOne)
                    customController { option -> StatusEffectController(option) }
                    initial(StatusEffects.SPEED)
                    defaultDescription()
                    collapsed(true)
                }
                val tierTwoBeaconEffects by rootOptions.registeringList("tier_two_beacon_effects") {
                    binding(beaconConfig.beaconEffectsByTier::tierTwo, BeaconConstants.DEFAULT_BEACON_CONFIG.beaconEffectsByTier.tierTwo)
                    customController { option -> StatusEffectController(option) }
                    initial(StatusEffects.SPEED)
                    defaultDescription()
                    collapsed(true)
                }
                val tierThreeBeaconEffects by rootOptions.registeringList("tier_three_beacon_effects") {
                    binding(
                        beaconConfig.beaconEffectsByTier::tierThree,
                        BeaconConstants.DEFAULT_BEACON_CONFIG.beaconEffectsByTier.tierThree
                    )
                    customController { option -> StatusEffectController(option) }
                    initial(StatusEffects.SPEED)
                    defaultDescription()
                    collapsed(true)
                }
                val secondaryBeaconEffects by rootOptions.registeringList("secondary_beacon_effects") {
                    binding(
                        beaconConfig.beaconEffectsByTier::secondaryEffects,
                        BeaconConstants.DEFAULT_BEACON_CONFIG.beaconEffectsByTier.secondaryEffects
                    )
                    customController { option -> StatusEffectController(option) }
                    initial(StatusEffects.SPEED)
                    defaultDescription()
                    collapsed(true)
                }
            }
            val conduitCategory by categories.registering("conduit") {
                val effectParticles by rootOptions.registering("effect_particles") {
                    binding(
                        conduitConfig::effectParticles,
                        BeaconConstants.DEFAULT_CONDUIT_CONFIG.effectParticles
                    )
                    controller(TickBoxControllerBuilder::create)
                }
            }

            save(::saveAllConfigs)
        }
        return config.generateScreen(parent)
    }

    @Suppress("unused", "UnusedVariable")
    private fun createStatusEffectsScreen(parent: Screen): Screen {
        val config = YetAnotherConfigLib("beaconoverhaul") {
            val conduitCategory by categories.registering("conduit") {
                val effectParticles by rootOptions.registering("effect_particles") {
                    binding(
                        conduitConfig::effectParticles,
                        BeaconConstants.DEFAULT_CONDUIT_CONFIG.effectParticles
                    )
                    controller(TickBoxControllerBuilder::create)
                }
            }

        }
        return config.generateScreen(parent)
    }

    fun saveAllConfigs() {
        writeConfig("beacon", beaconConfig)
        writeConfig("conduit", conduitConfig)
    }

    private val json = Json5 {
        encodeDefaults = true
        prettyPrint = true
    }

    private val configDir = FabricLoader.configDir(BeaconConstants.NAMESPACE)

    private inline fun <reified T> tryLoadConfig(name: String, defaultConfig: T): T {
        if (configDir.notExists())
            configDir.createDirectories()

        val configFile = configFile(name)
        if (configFile.notExists())
            writeConfig(name, defaultConfig)

        return try {
            json.decodeFromStream<T>(configFile.inputStream())
        } catch (e: Exception) {
            val backupFile = configFile.resolveSibling("${configFile.name}.bak")
            configFile.copyTo(backupFile, StandardCopyOption.REPLACE_EXISTING)

            logger.info(e) { "The config that was read is invalid config, writing the default, and backing up the original config to '${configFile.name}.tmp'" }
            writeConfig(name, defaultConfig)

            defaultConfig
        }
    }

    private fun configFile(name: String) = configDir.resolve("$name.json5")

    private inline fun <reified T> writeConfig(name: String, config: T) {
        if (configDir.notExists())
            configDir.createDirectories()

        val configFile = configFile(name)

        json.encodeToStream(config, configFile.outputStream())
    }
}

package gay.solonovamax.beaconsoverhaul.config

import gay.solonovamax.beaconsoverhaul.BeaconConstants
import gay.solonovamax.beaconsoverhaul.util.configDir
import gay.solonovamax.beaconsoverhaul.util.transparentBackground
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.info
import kotlin.io.path.createDirectories
import kotlin.io.path.inputStream
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

object BeaconOverhaulConfigManager {
    private val logger by getLogger()

    @JvmStatic
    val config: BeaconOverhauledConfig by lazy {
        loadConfig()
    }

    fun createConfigScreen(parent: Screen): Screen {
        val configBuilder = ConfigBuilder.create().apply {
            parentScreen = parent
            title = Text.translatable("title.beaconoverhaul.config")
            // savingRunnable = Runnable { writeConfig(config) }
            transparentBackground = true
        }

        val entryBuilder = configBuilder.entryBuilder()
        val generalCategory = configBuilder.getOrCreateCategory(Text.translatable("title.beaconoverhaul.category.general"))

        return configBuilder.build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        encodeDefaults = true
        prettyPrint = true
        coerceInputValues = true
        decodeEnumsCaseInsensitive = true
    }

    private val configDir = FabricLoader.getInstance().configDir(BeaconConstants.NAMESPACE)

    @OptIn(ExperimentalSerializationApi::class)
    fun loadConfig(): BeaconOverhauledConfig {
        logger.info { "Config dir is $configDir" }
        if (configDir.notExists())
            configDir.createDirectories()

        val configFile = configDir.resolve("config.json")

        val serializedConfig = when {
            configFile.notExists() -> {
                logger.info { "Writing default config" }
                writeDefaultConfig()
            }

            else -> try {
                json.decodeFromStream<BeaconOverhauledConfig>(configFile.inputStream())
            } catch (e: Exception) {
                logger.info { "Invalid config, writing default." }
                writeDefaultConfig()
            }
        }

        // return BeaconOverhauledConfig.from(serializedConfig)
        return serializedConfig
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun writeConfig(config: BeaconOverhauledConfig) {
        if (configDir.notExists())
            configDir.createDirectories()

        val configFile = configDir.resolve("config.json")

        json.encodeToStream(config, configFile.outputStream())

        // val writer = configFile.outputStream().bufferedWriter()
        // writer.write(
        //     """
        //     cum
        //     balls
        //     """.trimIndent()
        // )
        // writer.flush()
    }

    private fun writeDefaultConfig(): BeaconOverhauledConfig {
        return BeaconConstants.DEFAULT_CONFIG.also { config ->
            writeConfig(config)
        }
    }
}

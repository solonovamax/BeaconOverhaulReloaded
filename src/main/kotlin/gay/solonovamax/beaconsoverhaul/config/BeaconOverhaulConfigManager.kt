package gay.solonovamax.beaconsoverhaul.config

import gay.solonovamax.beaconsoverhaul.BeaconConstants
import gay.solonovamax.beaconsoverhaul.util.configDir
import gay.solonovamax.beaconsoverhaul.util.transparentBackground
import io.github.xn32.json5k.Json5
import io.github.xn32.json5k.decodeFromStream
import io.github.xn32.json5k.encodeToStream
import kotlinx.serialization.ExperimentalSerializationApi
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
    val beaconConfig: BeaconOverhauledConfig by lazy {
        tryLoadConfig("beacon", BeaconConstants.DEFAULT_BEACON_CONFIG)
    }

    @JvmStatic
    val conduitConfig: ConduitConfig by lazy {
        tryLoadConfig("conduit", BeaconConstants.DEFAULT_CONDUIT_CONFIG)
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
    private val json = Json5 {
        encodeDefaults = true
        prettyPrint = true
        // coerceInputValues = true
        // decodeEnumsCaseInsensitive = true
    }

    private val configDir = FabricLoader.getInstance().configDir(BeaconConstants.NAMESPACE)

    private inline fun <reified T> tryLoadConfig(name: String, defaultConfig: T): T {
        if (configDir.notExists())
            configDir.createDirectories()

        val configFile = configFile(name)
        if (configFile.notExists())
            writeConfig(name, defaultConfig)

        return try {
            json.decodeFromStream<T>(configFile.inputStream())
        } catch (e: Exception) {
            logger.info(e) { "Invalid config, writing default." }
            writeConfig(name, defaultConfig)

            defaultConfig
        }
    }

    private fun configFile(name: String) = configDir.resolve("$name.json5")

    @OptIn(ExperimentalSerializationApi::class)
    private inline fun <reified T> writeConfig(name: String, config: T) {
        if (configDir.notExists())
            configDir.createDirectories()

        val configFile = configFile(name)

        json.encodeToStream(config, configFile.outputStream())
    }
}

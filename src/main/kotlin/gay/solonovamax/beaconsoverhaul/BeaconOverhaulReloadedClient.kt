package gay.solonovamax.beaconsoverhaul

import gay.solonovamax.beaconsoverhaul.registry.LavenderRegistry
import gay.solonovamax.beaconsoverhaul.registry.OwoUIRegistry
import gay.solonovamax.beaconsoverhaul.registry.ScreenHandlerRegistry
import gay.solonovamax.beaconsoverhaul.screen.OverhauledBeaconScreen
import gay.solonovamax.beaconsoverhaul.screen.OverhauledConduitScreen
import net.fabricmc.api.ClientModInitializer
import org.slf4j.kotlin.debug
import org.slf4j.kotlin.getLogger

object BeaconOverhaulReloadedClient : ClientModInitializer {
    private val logger by getLogger()

    override fun onInitializeClient() {
        ScreenHandlerRegistry.registerClient()

        LavenderRegistry.register()
        OwoUIRegistry.register()

        BeaconOverhaulReloaded.updateBeaconPacket.receiveOnClient { beaconData, context ->
            val currentScreen = context.client.currentScreen
            if (currentScreen is OverhauledBeaconScreen) {
                logger.debug { "The current screen is OverhauledBeaconScreen, sending updated beacon data $beaconData, was previously ${currentScreen.screenHandler.data}" }
                currentScreen.screenHandler.data = beaconData
            }
        }

        BeaconOverhaulReloaded.updateConduitPacket.receiveOnClient { conduitData, context ->
            val currentScreen = context.client.currentScreen
            if (currentScreen is OverhauledConduitScreen) {
                logger.debug { "The current screen is OverhauledConduitScreen, sending updated conduit data $conduitData, was previously ${currentScreen.screenHandler.data}" }
                currentScreen.screenHandler.data = conduitData
            }
        }
    }
}

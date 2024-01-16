package gay.solonovamax.beaconsoverhaul

import gay.solonovamax.beaconsoverhaul.beacon.screen.OverhauledBeaconScreen
import gay.solonovamax.beaconsoverhaul.beacon.screen.ScreenHandlerRegistry
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.gui.screen.ingame.HandledScreens

object BeaconOverhaulReloadedClient : ClientModInitializer {
    override fun onInitializeClient() {
        HandledScreens.register(ScreenHandlerRegistry.OVERHAULED_BEACON_SCREEN_HANDLER, ::OverhauledBeaconScreen)
    }
}

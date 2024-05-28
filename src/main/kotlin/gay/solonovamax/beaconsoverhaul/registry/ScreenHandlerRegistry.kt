package gay.solonovamax.beaconsoverhaul.registry

import gay.solonovamax.beaconsoverhaul.screen.OverhauledBeaconScreen
import gay.solonovamax.beaconsoverhaul.screen.OverhauledBeaconScreenHandler
import gay.solonovamax.beaconsoverhaul.screen.OverhauledConduitScreen
import gay.solonovamax.beaconsoverhaul.screen.OverhauledConduitScreenHandler
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import gay.solonovamax.beaconsoverhaul.util.register
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.registry.Registries

object ScreenHandlerRegistry {
    val OVERHAULED_BEACON_SCREEN_HANDLER = ExtendedScreenHandlerType(::OverhauledBeaconScreenHandler)
    val OVERHAULED_CONDUIT_SCREEN_HANDLER = ExtendedScreenHandlerType(::OverhauledConduitScreenHandler)

    fun register() {
        Registries.SCREEN_HANDLER.register(identifierOf("beacon"), OVERHAULED_BEACON_SCREEN_HANDLER)
        Registries.SCREEN_HANDLER.register(identifierOf("conduit"), OVERHAULED_CONDUIT_SCREEN_HANDLER)
    }

    fun registerClient() {
        HandledScreens.register(OVERHAULED_BEACON_SCREEN_HANDLER, ::OverhauledBeaconScreen)
        HandledScreens.register(OVERHAULED_CONDUIT_SCREEN_HANDLER, ::OverhauledConduitScreen)
    }
}

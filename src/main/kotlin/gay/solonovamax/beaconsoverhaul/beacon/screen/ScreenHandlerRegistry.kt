package gay.solonovamax.beaconsoverhaul.beacon.screen

import gay.solonovamax.beaconsoverhaul.util.identifierOf
import gay.solonovamax.beaconsoverhaul.util.register
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.registry.Registries

object ScreenHandlerRegistry {
    val OVERHAULED_BEACON_SCREEN_HANDLER = ExtendedScreenHandlerType(::OverhauledBeaconScreenHandler)

    fun register() {
        Registries.SCREEN_HANDLER.register(
            identifierOf("beacon"),
            OVERHAULED_BEACON_SCREEN_HANDLER
        )
    }
}

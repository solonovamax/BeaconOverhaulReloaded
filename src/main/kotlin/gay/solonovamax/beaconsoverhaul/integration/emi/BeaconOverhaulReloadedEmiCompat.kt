package gay.solonovamax.beaconsoverhaul.integration.emi

import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.widget.Bounds
import gay.solonovamax.beaconsoverhaul.register.BlockRegistry
import gay.solonovamax.beaconsoverhaul.screen.OverhauledBeaconScreen

object BeaconOverhaulReloadedEmiCompat : EmiPlugin {
    override fun register(registry: EmiRegistry) {
        registry.addExclusionArea(OverhauledBeaconScreen::class.java) { screen, consumer ->
            val x = screen.x() + screen.backgroundWidth()
            val y = screen.y()
            consumer.accept(
                Bounds(
                    x + OverhauledBeaconScreen.PADDING,
                    y,
                    OverhauledBeaconScreen.BEACON_BLOCKS_SIDEBAR_WIDTH,
                    OverhauledBeaconScreen.BEACON_BLOCKS_SIDEBAR_HEIGHT,
                )
            )
            consumer.accept(
                Bounds(
                    x + OverhauledBeaconScreen.PADDING,
                    y + OverhauledBeaconScreen.BEACON_STATS_SIDEBAR_Y,
                    OverhauledBeaconScreen.BEACON_STATS_SIDEBAR_WIDTH,
                    OverhauledBeaconScreen.BEACON_STATS_SIDEBAR_HEIGHT,
                )
            )
        }

        registry.removeEmiStacks { s -> s.itemStack.item == BlockRegistry.FAKE_WITHER_SKELETON_SKULL.asItem() }
    }
}

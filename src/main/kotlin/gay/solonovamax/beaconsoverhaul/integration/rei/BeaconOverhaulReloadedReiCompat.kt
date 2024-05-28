package gay.solonovamax.beaconsoverhaul.integration.rei

import gay.solonovamax.beaconsoverhaul.screen.OverhauledBeaconScreen
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones

object BeaconOverhaulReloadedReiCompat : REIClientPlugin {
    override fun registerExclusionZones(zones: ExclusionZones) {
        zones.register(OverhauledBeaconScreen::class.java) { screen ->
            val x = screen.x() + screen.backgroundWidth()
            val y = screen.y()
            listOf(
                Rectangle(
                    x + OverhauledBeaconScreen.PADDING,
                    y,
                    OverhauledBeaconScreen.BEACON_BLOCKS_SIDEBAR_WIDTH,
                    OverhauledBeaconScreen.BEACON_BLOCKS_SIDEBAR_HEIGHT,
                ),
                Rectangle(
                    x + OverhauledBeaconScreen.PADDING,
                    y + OverhauledBeaconScreen.BEACON_STATS_SIDEBAR_Y,
                    OverhauledBeaconScreen.BEACON_STATS_SIDEBAR_WIDTH,
                    OverhauledBeaconScreen.BEACON_STATS_SIDEBAR_HEIGHT,
                )
            )
        }
    }
}

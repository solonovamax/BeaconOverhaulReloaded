package gay.solonovamax.beaconsoverhaul.integration.jei

import gay.solonovamax.beaconsoverhaul.BeaconConstants
import gay.solonovamax.beaconsoverhaul.screen.OverhauledBeaconScreen
import gay.solonovamax.beaconsoverhaul.util.EMI
import gay.solonovamax.beaconsoverhaul.util.REI
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import mezz.jei.api.IModPlugin
import mezz.jei.api.gui.handlers.IGuiContainerHandler
import mezz.jei.api.registration.IGuiHandlerRegistration
import net.minecraft.client.util.math.Rect2i

object BeaconOverhaulReloadedJeiCompat : IModPlugin {
    override fun getPluginUid() = identifierOf(BeaconConstants.NAMESPACE)

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        if (EMI.isPresent || REI.isPresent)
            return

        registration.addGuiContainerHandler(OverhauledBeaconScreen::class.java, OverhauledBeaconScreenContainerHandler)
    }

    object OverhauledBeaconScreenContainerHandler : IGuiContainerHandler<OverhauledBeaconScreen> {
        override fun getGuiExtraAreas(screen: OverhauledBeaconScreen): List<Rect2i> {
            val x = screen.x() + screen.backgroundWidth()
            val y = screen.y()

            return listOf(
                Rect2i(
                    x + OverhauledBeaconScreen.PADDING,
                    y,
                    OverhauledBeaconScreen.BEACON_BLOCKS_SIDEBAR_WIDTH,
                    OverhauledBeaconScreen.BEACON_BLOCKS_SIDEBAR_HEIGHT,
                ),
                Rect2i(
                    x + OverhauledBeaconScreen.PADDING,
                    y + OverhauledBeaconScreen.BEACON_STATS_SIDEBAR_Y,
                    OverhauledBeaconScreen.BEACON_STATS_SIDEBAR_WIDTH,
                    OverhauledBeaconScreen.BEACON_STATS_SIDEBAR_HEIGHT,
                )
            )
        }
    }
}

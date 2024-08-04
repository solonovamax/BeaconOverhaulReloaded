package gay.solonovamax.beaconsoverhaul.screen

import gay.solonovamax.beaconsoverhaul.block.conduit.data.OverhauledConduitData
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import org.slf4j.kotlin.getLogger

class OverhauledConduitScreen(
    handler: OverhauledConduitScreenHandler,
    inventory: PlayerInventory,
    title: Text,
) : OverhauledScreen<OverhauledConduitData, OverhauledConduitScreenHandler>(handler, inventory, title) {
    private val logger by getLogger()

    init {
        backgroundWidth = 230
        backgroundHeight = 219
    }
}

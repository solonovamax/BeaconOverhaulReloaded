package gay.solonovamax.beaconsoverhaul.register

import gay.solonovamax.beaconsoverhaul.block.FakeWitherSkeletonSkullBlock
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import gay.solonovamax.beaconsoverhaul.util.register
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.enums.Instrument
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier


object BlockRegistry {
    val FAKE_WITHER_SKELETON_SKULL = FakeWitherSkeletonSkullBlock(
        AbstractBlock.Settings.create().instrument(Instrument.WITHER_SKELETON).strength(1.0f).pistonBehavior(PistonBehavior.DESTROY)
    )

    // val CORRUPTED_BEACON = CorruptedBeaconBlock(
    //     AbstractBlock.Settings.create()
    //         .mapColor(MapColor.DIAMOND_BLUE)
    //         .instrument(Instrument.HAT)
    //         .strength(3.0F)
    //         .luminance { -15 }
    //         .nonOpaque()
    //         .solidBlock(Blocks::never)
    // )

    fun register() {
        FAKE_WITHER_SKELETON_SKULL.register(identifierOf("fake_wither_skeleton_skull"))
        // CORRUPTED_BEACON.register(identifierOf("corrupted_beacon"))

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register { content ->
            // content.addAfter(Items.BEACON, CORRUPTED_BEACON)
        }
    }

    fun <T : Block> T.register(id: Identifier): T {
        for (blockState in stateManager.states) {
            Block.STATE_IDS.add(blockState)
            blockState.initShapeCache()
        }

        return Registries.BLOCK.register(id, this)
    }
}

package gay.solonovamax.beaconsoverhaul.register

import gay.solonovamax.beaconsoverhaul.block.FakeWitherSkeletonSkullBlock
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import gay.solonovamax.beaconsoverhaul.util.register
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.WallBlock
import net.minecraft.block.enums.NoteBlockInstrument
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.item.ItemGroups
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier


object BlockRegistry : CommonRegistration {
    @JvmField
    val FAKE_WITHER_SKELETON_SKULL = FakeWitherSkeletonSkullBlock(blockSettings {
        instrument(NoteBlockInstrument.WITHER_SKELETON)
        strength(1.0f)
        pistonBehavior(PistonBehavior.DESTROY)
    })

    // val CORRUPTED_BEACON = CorruptedBeaconBlock(blockSettings {
    //     mapColor(MapColor.DIAMOND_BLUE)
    //     instrument(Instrument.HAT)
    //     strength(3.0F)
    //     luminance { -15 }
    //     nonOpaque()
    //     solidBlock(Blocks::never)
    // })

    @JvmField
    val PRISMARINE_BRICK_WALL = WallBlock(blockSettings(Blocks.PRISMARINE_BRICKS) {
        solid()
    })

    @JvmField
    val DARK_PRISMARINE_WALL = WallBlock(blockSettings(Blocks.DARK_PRISMARINE) {
        solid()
    })

    override fun register() {
        FAKE_WITHER_SKELETON_SKULL.register(identifierOf("fake_wither_skeleton_skull"))
        PRISMARINE_BRICK_WALL.register(identifierOf("prismarine_brick_wall"))
        DARK_PRISMARINE_WALL.register(identifierOf("dark_prismarine_wall"))
        // CORRUPTED_BEACON.register(identifierOf("corrupted_beacon"))

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register { content ->
            content.addAfter(Items.PRISMARINE_BRICK_SLAB, PRISMARINE_BRICK_WALL)
            content.addAfter(Items.DARK_PRISMARINE_SLAB, DARK_PRISMARINE_WALL)
            // content.addAfter(Items.BEACON, CORRUPTED_BEACON)
        }
    }

    private fun <T : Block> T.register(id: Identifier): T {
        for (blockState in stateManager.states) {
            Block.STATE_IDS.add(blockState)
            blockState.initShapeCache()
        }

        return Registries.BLOCK.register(id, this)
    }

    private inline fun blockSettings(
        block: AbstractBlock,
        builder: AbstractBlock.Settings.() -> Unit,
    ): AbstractBlock.Settings {
        return AbstractBlock.Settings.copy(block).apply(builder)
    }

    private inline fun blockSettings(
        builder: AbstractBlock.Settings.() -> Unit,
    ): AbstractBlock.Settings {
        return AbstractBlock.Settings.create().apply(builder)
    }
}

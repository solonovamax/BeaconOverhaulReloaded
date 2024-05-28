package gay.solonovamax.beaconsoverhaul.registry

import gay.solonovamax.beaconsoverhaul.block.FakeWitherSkeletonSkullBlock
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import gay.solonovamax.beaconsoverhaul.util.register
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.enums.Instrument
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier


object BlockRegistry {
    val FAKE_WITHER_SKELETON_SKULL = FakeWitherSkeletonSkullBlock(
        AbstractBlock.Settings.create().instrument(Instrument.WITHER_SKELETON).strength(1.0f).pistonBehavior(PistonBehavior.DESTROY)
    )

    fun register() {
        FAKE_WITHER_SKELETON_SKULL.register(identifierOf("fake_wither_skeleton_skull"), true)
    }

    fun <T : Block> T.register(id: Identifier, registerItem: Boolean = false): T {
        if (registerItem) {
            val blockItem = BlockItem(this, Item.Settings())
            Registries.ITEM.register(id, blockItem)
        }

        return Registries.BLOCK.register(id, this)
    }
}

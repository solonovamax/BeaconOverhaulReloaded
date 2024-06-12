package gay.solonovamax.beaconsoverhaul.register

import gay.solonovamax.beaconsoverhaul.BeaconConstants.NAMESPACE
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import gay.solonovamax.beaconsoverhaul.util.register
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.text.Text

object ItemGroupRegistry {
    val ITEM_GROUP = FabricItemGroup.builder()
        .icon { Blocks.BEACON.asItem().defaultStack }
        .displayName(Text.translatable("itemGroup.$NAMESPACE"))
        .entries { _, entries ->
            entries.add(Blocks.BEACON)
            // entries.add(BlockRegistry.CORRUPTED_BEACON)
            entries.add(Blocks.CONDUIT)
        }
        .build()

    fun register() {
        Registries.ITEM_GROUP.register(identifierOf("itemgroup"), ITEM_GROUP)
    }
}

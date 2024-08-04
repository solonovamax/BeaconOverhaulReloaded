package gay.solonovamax.beaconsoverhaul.datagen.model

import gay.solonovamax.beaconsoverhaul.datagen.util.registerWallModelTexturePool
import gay.solonovamax.beaconsoverhaul.register.ItemRegistry
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.Models
import net.minecraft.data.family.BlockFamilies

class ModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(generator: BlockStateModelGenerator) {
        // generator.registerSimpleState(BlockRegistry.CORRUPTED_BEACON)
        // generator.registerParentedItemModel(BlockRegistry.CORRUPTED_BEACON)

        for (family in listOf(BlockFamilies.PRISMARINE_BRICK, BlockFamilies.DARK_PRISMARINE)) {
            generator.registerWallModelTexturePool(family)
        }
    }

    override fun generateItemModels(generator: ItemModelGenerator) {
        generator.register(ItemRegistry.GUIDEBOOK, Models.GENERATED)
    }
}

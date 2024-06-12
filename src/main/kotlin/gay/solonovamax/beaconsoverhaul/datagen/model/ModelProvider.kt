package gay.solonovamax.beaconsoverhaul.datagen.model

import gay.solonovamax.beaconsoverhaul.register.ItemRegistry
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.block.Block
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.ModelIds
import net.minecraft.data.client.Models

class ModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(generator: BlockStateModelGenerator) {
        // generator.registerSimpleState(BlockRegistry.CORRUPTED_BEACON)
        // generator.registerParentedItemModel(BlockRegistry.CORRUPTED_BEACON)
    }

    override fun generateItemModels(generator: ItemModelGenerator) {
        generator.register(ItemRegistry.GUIDEBOOK, Models.GENERATED)
        // Models.GENERATED.upload(ModelIds.getItemModelId(item), TextureMap.layer0(item), this.writer)
    }
}

private fun BlockStateModelGenerator.registerParentedItemModel(block: Block) {
    return registerParentedItemModel(block, ModelIds.getBlockModelId(block))
}

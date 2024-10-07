package gay.solonovamax.beaconsoverhaul.datagen.loot

import gay.solonovamax.beaconsoverhaul.register.BlockRegistry
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class BlockDropLootTableProvider(
    output: FabricDataOutput,
    registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>,
) : FabricBlockLootTableProvider(output, registryLookup) {
    override fun generate() {
        addDrop(BlockRegistry.PRISMARINE_BRICK_WALL)
        addDrop(BlockRegistry.DARK_PRISMARINE_WALL)
    }
}

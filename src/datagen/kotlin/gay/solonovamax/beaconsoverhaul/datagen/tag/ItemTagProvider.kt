package gay.solonovamax.beaconsoverhaul.datagen.tag

import gay.solonovamax.beaconsoverhaul.register.ItemRegistry
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.ItemTags
import java.util.concurrent.CompletableFuture

class ItemTagProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>,
) : FabricTagProvider.ItemTagProvider(output, registriesFuture) {
    override fun configure(lookup: RegistryWrapper.WrapperLookup) {
        getOrCreateTagBuilder(ItemTags.WALLS)
            .add(ItemRegistry.PRISMARINE_BRICK_WALL)
            .add(ItemRegistry.DARK_PRISMARINE_WALL)
    }
}


package gay.solonovamax.beaconsoverhaul.datagen.recipe

import gay.solonovamax.beaconsoverhaul.datagen.util.offerStonecuttingRecipe
import gay.solonovamax.beaconsoverhaul.datagen.util.offerWallRecipe
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.family.BlockFamilies
import net.minecraft.data.family.BlockFamily.Variant
import net.minecraft.data.server.recipe.RecipeExporter
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class RecipeProvider(
    output: FabricDataOutput,
    registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>,
) : FabricRecipeProvider(output, registryLookup) {
    override fun generate(exporter: RecipeExporter) {
        exporter.offerWallRecipe(BlockFamilies.PRISMARINE_BRICK)
        exporter.offerWallRecipe(BlockFamilies.DARK_PRISMARINE)

        exporter.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, BlockFamilies.PRISMARINE_BRICK, Variant.WALL)
        exporter.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, BlockFamilies.DARK_PRISMARINE, Variant.WALL)
    }
}

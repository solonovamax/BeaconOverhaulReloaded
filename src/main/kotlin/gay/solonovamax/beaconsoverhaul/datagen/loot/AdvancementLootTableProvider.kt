package gay.solonovamax.beaconsoverhaul.datagen.loot

import gay.solonovamax.beaconsoverhaul.register.ItemRegistry
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.util.Identifier
import java.util.function.BiConsumer

class AdvancementLootTableProvider(
    output: FabricDataOutput,
) : SimpleFabricLootTableProvider(output, LootContextTypes.ADVANCEMENT_REWARD) {
    override fun accept(exporter: BiConsumer<Identifier, LootTable.Builder>) {
        // val nbt = nbtCompound {
        //     put("BookId", "beaconoverhaul:guidebook")
        // }

        val beaconGuideItem = ItemEntry.builder(ItemRegistry.GUIDEBOOK)
        // .apply(SetNbtLootFunction.builder(nbt))
        exporter.accept(
            identifierOf("beacon_guide"),
            LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f)).with(beaconGuideItem))
        )
    }
}

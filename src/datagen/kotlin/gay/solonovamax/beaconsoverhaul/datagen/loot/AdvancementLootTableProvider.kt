package gay.solonovamax.beaconsoverhaul.datagen.loot

import gay.solonovamax.beaconsoverhaul.datagen.util.LootTableExporter
import gay.solonovamax.beaconsoverhaul.datagen.util.buildLootPool
import gay.solonovamax.beaconsoverhaul.datagen.util.buildLootTable
import gay.solonovamax.beaconsoverhaul.datagen.util.constant
import gay.solonovamax.beaconsoverhaul.register.ItemRegistry
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class AdvancementLootTableProvider(
    output: FabricDataOutput,
    registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>,
) : SimpleFabricLootTableProvider(output, registryLookup, LootContextTypes.ADVANCEMENT_REWARD) {
    override fun accept(exporter: LootTableExporter) {
        val beaconGuideItem = ItemEntry.builder(ItemRegistry.GUIDEBOOK)
        exporter.accept(
            RegistryKey.of(RegistryKeys.LOOT_TABLE, identifierOf("beacon_guide")),
            buildLootTable {
                pool(buildLootPool {
                    rolls(constant(1f))

                    with(beaconGuideItem)
                })
            }
        )
    }
}

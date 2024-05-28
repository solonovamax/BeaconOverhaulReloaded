package gay.solonovamax.beaconsoverhaul.datagen

import gay.solonovamax.beaconsoverhaul.datagen.advancement.AdvancementProvider
import gay.solonovamax.beaconsoverhaul.datagen.loot.AdvancementLootTableProvider
import gay.solonovamax.beaconsoverhaul.datagen.tag.BlockTagProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

object BeaconOverhaulReloadedDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        val pack = generator.createPack()

        pack.addProvider(::AdvancementProvider)
        pack.addProvider(::AdvancementLootTableProvider)
        pack.addProvider(::BlockTagProvider)
    }
}

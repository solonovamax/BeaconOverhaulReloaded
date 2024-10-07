package gay.solonovamax.beaconsoverhaul.datagen.advancement

import gay.solonovamax.beaconsoverhaul.advancement.RedirectBeaconCriterion
import gay.solonovamax.beaconsoverhaul.datagen.util.AdvancementExporter
import gay.solonovamax.beaconsoverhaul.datagen.util.advancementOf
import gay.solonovamax.beaconsoverhaul.datagen.util.buildAdvancement
import gay.solonovamax.beaconsoverhaul.datagen.util.lootRewardOf
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.criterion.ConstructBeaconCriterion
import net.minecraft.block.Blocks
import net.minecraft.predicate.NumberRange.IntRange
import net.minecraft.registry.RegistryWrapper
import net.minecraft.text.Text
import java.util.concurrent.CompletableFuture

class AdvancementProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>,
) : FabricAdvancementProvider(output, registriesFuture) {
    override fun generateAdvancement(lookup: RegistryWrapper.WrapperLookup, exporter: AdvancementExporter) {
        val summonWither = advancementOf(identifierOf("minecraft:nether/summon_wither"))

        val createBeacon = exporter.buildAdvancement(identifierOf("minecraft:nether/create_beacon")) {
            parent(summonWither)

            display(
                Blocks.BEACON,
                Text.translatable("advancements.nether.create_beacon.title"),
                Text.translatable("advancements.nether.create_beacon.description"),
                null,
                AdvancementFrame.TASK,
                true,
                true,
                false
            )

            criterion("beacon", ConstructBeaconCriterion.Conditions.level(IntRange.atLeast(1)))
            rewards(lootRewardOf(identifierOf("beacon_guide")))
        }

        val createFullBeacon = exporter.buildAdvancement(identifierOf("minecraft:nether/create_full_beacon")) {
            parent(createBeacon)

            display(
                Blocks.BEACON,
                Text.translatable("advancements.nether.create_full_beacon.title"),
                Text.translatable("advancements.nether.create_full_beacon.description"),
                null,
                AdvancementFrame.GOAL,
                true,
                true,
                false
            )

            criterion("beacon", ConstructBeaconCriterion.Conditions.level(IntRange.atLeast(4)))

        }

        val redirectBeacon = exporter.buildAdvancement(identifierOf("nether/redirect_beacon")) {
            parent(createFullBeacon)

            display(
                Blocks.AMETHYST_CLUSTER,
                Text.translatable("advancements.nether.beaconoverhaul.redirect_beacon.title"),
                Text.translatable("advancements.nether.beaconoverhaul.redirect_beacon.description"),
                null,
                AdvancementFrame.GOAL,
                true,
                true,
                false
            )

            criterion("redirect_beacon", RedirectBeaconCriterion.Conditions.create())
        }

        val redirectBeaconMany = exporter.buildAdvancement(identifierOf("nether/redirect_beacon_many")) {
            parent(createFullBeacon)

            display(
                Blocks.AMETHYST_CLUSTER,
                Text.translatable("advancements.nether.beaconoverhaul.redirect_beacon_many.title"),
                Text.translatable("advancements.nether.beaconoverhaul.redirect_beacon_many.description"),
                null,
                AdvancementFrame.CHALLENGE,
                true,
                true,
                true
            )

            criterion("redirect_beacon", RedirectBeaconCriterion.Conditions.create(IntRange.atLeast(8)))
        }
    }
}

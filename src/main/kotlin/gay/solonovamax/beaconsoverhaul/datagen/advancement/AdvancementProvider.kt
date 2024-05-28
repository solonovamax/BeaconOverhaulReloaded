package gay.solonovamax.beaconsoverhaul.datagen.advancement

import gay.solonovamax.beaconsoverhaul.advancement.RedirectBeaconCriterion
import gay.solonovamax.beaconsoverhaul.util.build
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.ConstructBeaconCriterion
import net.minecraft.block.Blocks
import net.minecraft.predicate.NumberRange
import net.minecraft.text.Text
import java.util.function.Consumer

class AdvancementProvider(output: FabricDataOutput) : FabricAdvancementProvider(output) {
    override fun generateAdvancement(exporter: Consumer<Advancement>) {
        val summonWither = Advancement.Builder.createUntelemetered()
            .build(identifierOf("minecraft", "nether/summon_wither"))

        val createBeacon = Advancement.Builder.createUntelemetered()
            .parent(summonWither)
            .display(
                Blocks.BEACON,
                Text.translatable("advancements.nether.create_beacon.title"),
                Text.translatable("advancements.nether.create_beacon.description"),
                null,
                AdvancementFrame.TASK,
                true,
                true,
                false
            )
            .criterion("beacon", ConstructBeaconCriterion.Conditions.level(NumberRange.IntRange.atLeast(1)))
            .rewards(AdvancementRewards.Builder.loot(identifierOf("beacon_guide")))
            .build(exporter, identifierOf("minecraft", "nether/create_beacon"))

        val createFullBeacon = Advancement.Builder.createUntelemetered()
            .parent(createBeacon)
            .display(
                Blocks.BEACON,
                Text.translatable("advancements.nether.create_full_beacon.title"),
                Text.translatable("advancements.nether.create_full_beacon.description"),
                null,
                AdvancementFrame.GOAL,
                true,
                true,
                false
            )
            .criterion("beacon", ConstructBeaconCriterion.Conditions.level(NumberRange.IntRange.atLeast(4)))
            .build(exporter, identifierOf("minecraft", "nether/create_full_beacon"))

        val redirectBeacon: Advancement = Advancement.Builder.createUntelemetered()
            .parent(createFullBeacon)
            .display(
                Blocks.AMETHYST_CLUSTER,
                Text.translatable("advancements.nether.beaconoverhaul.redirect_beacon.title"),
                Text.translatable("advancements.nether.beaconoverhaul.redirect_beacon.description"),
                null,
                AdvancementFrame.CHALLENGE,
                true,
                true,
                false
            )
            .criterion("redirect_beacon", RedirectBeaconCriterion.Conditions.create())
            .build(exporter, identifierOf("nether/redirect_beacon"))
    }
}

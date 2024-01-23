package gay.solonovamax.beaconsoverhaul

import gay.solonovamax.beaconsoverhaul.config.SerializedBeaconOverhauledConfig
import gay.solonovamax.beaconsoverhaul.config.SerializedBeaconOverhauledConfig.AttributeModifier
import gay.solonovamax.beaconsoverhaul.config.SerializedBeaconOverhauledConfig.AttributeModifier.Operation
import gay.solonovamax.beaconsoverhaul.config.SerializedBeaconOverhauledConfig.BeaconTierEffects
import gay.solonovamax.beaconsoverhaul.effects.StatusEffectRegistry
import net.minecraft.block.Blocks
import net.minecraft.entity.effect.StatusEffects

object BeaconConstants {
    const val MOD_NAME = "Beacons Overhaul Reloaded"

    const val NAMESPACE = "beaconoverhaul"

    val DEFAULT_CONFIG = SerializedBeaconOverhauledConfig(
        pointsModifiers = listOf(
            AttributeModifier(Blocks.COPPER_BLOCK, "(blocks)^0.45 * 2", Operation.ADDITION),
            AttributeModifier(Blocks.IRON_BLOCK, "(blocks)^0.6 * 2", Operation.ADDITION),
            AttributeModifier(Blocks.GOLD_BLOCK, "(blocks)^0.95 * 0.5", Operation.ADDITION),
            AttributeModifier(Blocks.AMETHYST_BLOCK, "min(blocks, 8) * 8", Operation.ADDITION),
            AttributeModifier(Blocks.EMERALD_BLOCK, "(blocks)^0.95", Operation.ADDITION),
            AttributeModifier(Blocks.DIAMOND_BLOCK, "(blocks)^0.75 * 5", Operation.ADDITION),
            AttributeModifier(Blocks.NETHERITE_BLOCK, "1 + (blocks * 0.05)", Operation.MULTIPLICATION)
        ),
        range = "min(10 + pts * 2, 4096)",
        duration = "10 + pts / 15",
        primaryAmplifier = "if(pts > 256, if(pts > 512, 3, 2), 1) + isPotent",
        secondaryAmplifier = "1",
        maxBeaconLayers = 6,
        levelOneStatusEffects = listOf(
            StatusEffects.FIRE_RESISTANCE,
            StatusEffects.SLOW_FALLING,
            StatusEffects.NIGHT_VISION,
        ),
        beaconBaseBlocks = listOf(
            Blocks.COPPER_BLOCK,
            Blocks.IRON_BLOCK,
            Blocks.GOLD_BLOCK,
            Blocks.AMETHYST_BLOCK,
            Blocks.EMERALD_BLOCK,
            Blocks.DIAMOND_BLOCK,
            Blocks.NETHERITE_BLOCK,
        ),
        beaconEffectsByTier = BeaconTierEffects(
            tierOne = listOf(StatusEffects.SPEED, StatusEffects.HASTE, StatusEffects.NIGHT_VISION),
            tierTwo = listOf(StatusEffects.RESISTANCE, StatusEffects.JUMP_BOOST, StatusEffectRegistry.LONG_REACH),
            tierThree = listOf(StatusEffects.STRENGTH, StatusEffectRegistry.NUTRITION),
            secondaryEffects = listOf(StatusEffects.REGENERATION, StatusEffects.FIRE_RESISTANCE, StatusEffects.SLOW_FALLING),
        ),
    )
}

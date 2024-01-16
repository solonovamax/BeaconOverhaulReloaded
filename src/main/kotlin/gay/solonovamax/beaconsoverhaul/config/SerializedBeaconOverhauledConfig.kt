@file:UseSerializers(BlockSerializer::class, StatusEffectSerializer::class)

package gay.solonovamax.beaconsoverhaul.config

import gay.solonovamax.beaconsoverhaul.config.SerializedBeaconOverhauledConfig.AttributeModifier.Operation
import gay.solonovamax.beaconsoverhaul.config.serializer.BlockSerializer
import gay.solonovamax.beaconsoverhaul.config.serializer.StatusEffectSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffects


@Serializable
data class SerializedBeaconOverhauledConfig(
    val pointsModifiers: List<AttributeModifier> = listOf(
        AttributeModifier(Blocks.COPPER_BLOCK, "(blocks)^0.45 * 2", Operation.ADDITION),
        AttributeModifier(Blocks.IRON_BLOCK, "(blocks)^0.6 * 2", Operation.ADDITION),
        AttributeModifier(Blocks.GOLD_BLOCK, "(blocks)^0.95 * 0.5", Operation.ADDITION),
        AttributeModifier(Blocks.AMETHYST_BLOCK, "min(blocks, 8) * 8", Operation.ADDITION),
        AttributeModifier(Blocks.EMERALD_BLOCK, "(blocks)^0.95", Operation.ADDITION),
        AttributeModifier(Blocks.DIAMOND_BLOCK, "(blocks)^0.75 * 5", Operation.ADDITION),
        AttributeModifier(Blocks.NETHERITE_BLOCK, "1 + (blocks * 0.05)", Operation.MULTIPLICATION)
    ),
    val range: String = "min(10 + pts * 2, 4096)",
    val duration: String = "10 + pts / 15",
    val primaryAmplifier: String = "if(pts > 256, if(pts > 512, 3, 2), 1) + isPotent",
    val secondaryAmplifier: String = "1",
    val maxBeaconLayers: Int = 6,
    val levelOneStatusEffects: List<StatusEffect> = listOf(
        StatusEffects.FIRE_RESISTANCE,
        StatusEffects.SLOW_FALLING,
        StatusEffects.NIGHT_VISION,
    ),
    val beaconBaseBlocks: List<Block> = listOf(
        Blocks.COPPER_BLOCK,
        Blocks.IRON_BLOCK,
        Blocks.GOLD_BLOCK,
        Blocks.AMETHYST_BLOCK,
        Blocks.EMERALD_BLOCK,
        Blocks.DIAMOND_BLOCK,
        Blocks.NETHERITE_BLOCK,
    ),
) {
    /*
    Range = base + pts * 2
    Duration (ticks) = base + pts
    Duration (seconds) = base / 20 + pts / 20

    Copper: x^0.45 * 2
    Iron: x^0.6 * 2 * 2
    Gold: x^0.95 * 0.5
    Emerald: x^0.5 * 7.5
    Amethyst: x^0.95
    Diamond: x^0.75 * 5
     */
    @Serializable
    data class AttributeModifier(
        val block: Block,
        val expression: String,
        val operation: Operation,
    ) {
        enum class Operation {
            ADDITION,
            MULTIPLICATION;
        }
    }
}

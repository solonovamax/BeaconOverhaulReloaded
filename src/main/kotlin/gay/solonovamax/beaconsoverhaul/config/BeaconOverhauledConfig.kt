package gay.solonovamax.beaconsoverhaul.config

import gay.solonovamax.beaconsoverhaul.config.BeaconOverhauledConfig.AttributeModifier.Operation
import gay.solonovamax.beaconsoverhaul.config.serializer.BlockSerializer
import kotlinx.serialization.Serializable
import net.minecraft.block.Block
import net.minecraft.block.Blocks

@Serializable
data class BeaconOverhauledConfig(
    val range: List<AttributeModifier>,
    val baseRange: Double,
    val duration: List<AttributeModifier>,
    val baseDuration: Double,
) {
    @Serializable
    data class AttributeModifier(
        @Serializable(BlockSerializer::class)
        val block: Block,
        val value: Double,
        val operation: Operation,
    ) {
        enum class Operation {
            ADDITION,
            MULTIPLICATION;
        }
    }

    companion object {
        val DEFAULT = BeaconOverhauledConfig(
            baseRange = 10.0,
            baseDuration = 200.0,
            range = listOf(
                AttributeModifier(Blocks.COPPER_BLOCK, 0.5, Operation.ADDITION),
                AttributeModifier(Blocks.IRON_BLOCK, 1.0, Operation.ADDITION),
                AttributeModifier(Blocks.GOLD_BLOCK, 2.0, Operation.ADDITION),
                AttributeModifier(Blocks.AMETHYST_BLOCK, 3.0, Operation.ADDITION),
                AttributeModifier(Blocks.EMERALD_BLOCK, 4.0, Operation.ADDITION),
                AttributeModifier(Blocks.DIAMOND_BLOCK, 8.0, Operation.ADDITION),
                AttributeModifier(Blocks.NETHERITE_BLOCK, 1.05, Operation.MULTIPLICATION)
            ),
            duration = listOf(
                AttributeModifier(Blocks.COPPER_BLOCK, 1.0, Operation.ADDITION),
                AttributeModifier(Blocks.IRON_BLOCK, 2.0, Operation.ADDITION),
                AttributeModifier(Blocks.GOLD_BLOCK, 4.0, Operation.ADDITION),
                AttributeModifier(Blocks.AMETHYST_BLOCK, 6.0, Operation.ADDITION),
                AttributeModifier(Blocks.EMERALD_BLOCK, 8.0, Operation.ADDITION),
                AttributeModifier(Blocks.DIAMOND_BLOCK, 16.0, Operation.ADDITION),
                AttributeModifier(Blocks.NETHERITE_BLOCK, 1.05, Operation.MULTIPLICATION)
            )
        )
    }
}

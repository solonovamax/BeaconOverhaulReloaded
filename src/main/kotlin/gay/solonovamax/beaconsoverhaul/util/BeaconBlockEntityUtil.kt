package gay.solonovamax.beaconsoverhaul.util

import ca.solostudios.guava.kotlin.collect.Multiset
import ca.solostudios.guava.kotlin.collect.mutableMultisetOf
import gay.solonovamax.beaconsoverhaul.BeaconOverhaulReloaded
import gay.solonovamax.beaconsoverhaul.OverhauledBeacon
import gay.solonovamax.beaconsoverhaul.config.BeaconOverhauledConfig
import gay.solonovamax.beaconsoverhaul.config.BeaconOverhauledConfig.AttributeModifier.Operation
import net.minecraft.block.Block
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.registry.tag.BlockTags
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.info
import kotlin.math.floor

object BeaconBlockEntityUtil {
    private val logger by getLogger()

    @JvmStatic
    fun BeaconBlockEntity.updateTier(world: World, pos: BlockPos, x: Int, y: Int, z: Int) {
        var level = 0
        var layerOffset = 1

        this as OverhauledBeacon

        baseBlocks.clear()

        while (layerOffset <= 4) {
            val yOffset = y - layerOffset
            if (yOffset < world.bottomY)
                break

            var isLayerFull = true
            val layerContents = mutableMultisetOf<Block>()
            var xOffset = x - layerOffset
            // for (int xOffset = x - layerOffset; xOffset <= x + layerOffset && isLayerFull; ++xOffset) {
            while (xOffset <= x + layerOffset && isLayerFull) {
                for (zOffset in z - layerOffset..z + layerOffset) {
                    val state = world.getBlockState(BlockPos(xOffset, yOffset, zOffset))

                    if (state in BlockTags.BEACON_BASE_BLOCKS) {
                        layerContents.add(state.block)
                    } else {
                        // note: setting `isLayerFull` to `false` will stop the outer for loop from continuing.
                        isLayerFull = false
                        break
                    }
                }
                ++xOffset
            }

            if (!isLayerFull)
                break

            baseBlocks += layerContents
            level = layerOffset++
        }

        this.vanillaLevel = level
    }

    @JvmStatic
    fun computeRange(beacon: OverhauledBeacon): Int {
        val range = beacon.baseBlocks.calculateModifier(
            BeaconOverhaulReloaded.config.baseRange,
            BeaconOverhaulReloaded.config.range
        )

        logger.info { "The range is $range" }
        return floor(range).toInt()
    }

    private fun Multiset<Block>.calculateModifier(
        base: Double,
        modifiers: List<BeaconOverhauledConfig.AttributeModifier>,
    ): Double {
        val additionModifiers = modifiers.filter { it.operation == Operation.ADDITION }
        val multiplicationModifiers = modifiers.filter { it.operation == Operation.MULTIPLICATION }

        var result = base

        for ((block, value) in additionModifiers) {
            if (block in this)
                result += value * this[block]

            logger.info { "Block $block has count ${this[block]}, adding ${value * this[block]}" }
        }
        for ((block, value) in multiplicationModifiers) {
            if (block in this)
                result *= value * this[block]

            logger.info { "Block $block has count ${this[block]}, multiplying by ${value * this[block]}" }
        }

        return result
    }
}

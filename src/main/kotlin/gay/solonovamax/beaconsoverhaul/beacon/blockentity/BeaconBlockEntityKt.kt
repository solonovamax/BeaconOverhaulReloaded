package gay.solonovamax.beaconsoverhaul.beacon.blockentity

import ca.solostudios.guava.kotlin.collect.mutableMultisetOf
import gay.solonovamax.beaconsoverhaul.BeaconOverhaulReloaded
import gay.solonovamax.beaconsoverhaul.beacon.OverhauledBeacon
import gay.solonovamax.beaconsoverhaul.beacon.serializable.OverhauledBeaconData
import gay.solonovamax.beaconsoverhaul.util.contains
import gay.solonovamax.beaconsoverhaul.util.get
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import net.minecraft.block.Block
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.slf4j.kotlin.getLogger

object BeaconBlockEntityKt {
    private val logger by getLogger()

    @JvmStatic
    fun BeaconBlockEntity.updateTier(world: World, pos: BlockPos) {
        val x = pos.x
        val y = pos.y
        val z = pos.z
        var level = 0
        var layerOffset = 1

        this as OverhauledBeacon

        baseBlocks.clear()

        while (layerOffset <= BeaconOverhaulReloaded.config.maxBeaconLayers) {
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

        this.level = level
        this.beaconPoints = computePoints(this)
    }

    @JvmStatic
    fun computePoints(beacon: OverhauledBeacon): Double {
        var result = 0.0
        // addition modifiers (ie. most blocks)
        for ((block, expression) in BeaconOverhaulReloaded.config.additionModifiers) {
            if (block in beacon.baseBlocks) {
                val expressionResult = expression.evaluate(beacon.baseBlocks[block].toDouble())
                result += expressionResult
            }
        }
        // multiplication modifiers (ie. netherite)
        for ((block, expression) in BeaconOverhaulReloaded.config.multiplicationModifiers) {
            if (block in beacon.baseBlocks) {
                val expressionResult = expression.evaluate(beacon.baseBlocks[block].toDouble())
                result *= expressionResult
            }
        }

        return result
    }

    @JvmStatic
    fun OverhauledBeacon.writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        val data = OverhauledBeaconData.from(this)
        val bytes = Cbor.encodeToByteArray(data)
        buf.writeByteArray(bytes)
    }
}

package gay.solonovamax.beaconsoverhaul.beacon.blockentity

import ca.solostudios.guava.kotlin.collect.Multiset
import ca.solostudios.guava.kotlin.collect.MutableMultiset
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
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.slf4j.kotlin.getLogger

object BeaconBlockEntityKt {
    private val logger by getLogger()

    @JvmStatic
    fun <T> T.updateTier(world: World, pos: BlockPos) where T : OverhauledBeacon {
        if (!shouldUpdateBeacon(world, pos))
            return

        val (level, baseBlocks) = buildBlockMultiset(pos.y, world, pos.x, pos.z)
        this.baseBlocks = baseBlocks
        this.level = level
        this.beaconPoints = computePoints(baseBlocks)
    }

    private fun buildBlockMultiset(
        y: Int,
        world: World,
        x: Int,
        z: Int,
    ): Pair<Int, MutableMultiset<Block>> {
        var level = 0

        val baseBlocks = mutableMultisetOf<Block>()

        for (layerOffset in 1..BeaconOverhaulReloaded.config.maxBeaconLayers) {
            val yOffset = y - layerOffset

            if (yOffset < world.bottomY)
                break

            val layerContents = mutableMultisetOf<Block>()
            for (xOffset in x - layerOffset..x + layerOffset) {
                for (zOffset in z - layerOffset..z + layerOffset) {
                    val state = world.getBlockState(BlockPos(xOffset, yOffset, zOffset))

                    if (state !in BlockTags.BEACON_BASE_BLOCKS)
                        return level to baseBlocks

                    layerContents.add(state.block)
                }
            }

            baseBlocks.addAll(layerContents)
            level = layerOffset
        }

        return level to baseBlocks
    }

    private fun OverhauledBeacon.shouldUpdateBeacon(world: World, pos: BlockPos): Boolean {
        val time = world.time
        val updateDelay = BeaconOverhaulReloaded.config.beaconUpdateDelayTicks
        val quickCheckDelay = BeaconOverhaulReloaded.config.beaconQuickCheckDelayTicks

        when {
            // always update every `updateDelay` ticks
            time % updateDelay == 0L -> return true

            // if not first condition && level greater than 0 do not update
            level > 0 -> return false

            // if not first condition && not second condition && not on a tick mod quickCheckDelay, do not update
            time % quickCheckDelay != 0L -> return false

            // Test if we should update
            else -> {
                for (xOffset in -1..1)
                    for (zOffset in -1..1)
                        if (world.getBlockState(pos.add(xOffset, -1, zOffset)) !in BlockTags.BEACON_BASE_BLOCKS)
                            return false

                return true
            }
        }
    }

    @JvmStatic
    private fun computePoints(baseBlocks: Multiset<Block>): Double {
        var result = 0.0
        // addition modifiers (ie. most blocks)
        for ((block, expression) in BeaconOverhaulReloaded.config.additionModifiers) {
            if (block in baseBlocks) {
                val expressionResult = expression.evaluate(baseBlocks[block].toDouble())
                result += expressionResult
            }
        }
        // multiplication modifiers (ie. netherite)
        for ((block, expression) in BeaconOverhaulReloaded.config.multiplicationModifiers) {
            if (block in baseBlocks) {
                val expressionResult = expression.evaluate(baseBlocks[block].toDouble())
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

    @JvmStatic
    fun OverhauledBeacon.canApplyEffect(effect: StatusEffect): Boolean {
        if (level == 0)
            return false

        if (level <= 1)
            if (effect in BeaconBlockEntity.EFFECTS_BY_LEVEL[1])
                return false

        if (level <= 2)
            if (effect in BeaconBlockEntity.EFFECTS_BY_LEVEL[2])
                return false

        if (level <= 3)
            if (effect in BeaconBlockEntity.EFFECTS_BY_LEVEL[3])
                return false

        return true
    }
}

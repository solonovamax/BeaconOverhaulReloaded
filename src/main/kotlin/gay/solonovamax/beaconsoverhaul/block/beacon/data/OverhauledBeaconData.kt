@file:UseSerializers(BlockSerializer::class)

package gay.solonovamax.beaconsoverhaul.block.beacon.data

import ca.solostudios.guava.kotlin.collect.asMap
import gay.solonovamax.beaconsoverhaul.block.beacon.OverhauledBeacon
import gay.solonovamax.beaconsoverhaul.serialization.BlockSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.minecraft.block.Block
import kotlin.time.Duration.Companion.seconds

@Serializable
data class OverhauledBeaconData(
    val baseBlocks: Map<Block, Int>,
    val level: Int,
    val beaconPoints: Double,
    val range: Int,
    val duration: Int,
    val primaryAmplifier: Int,
    val primaryAmplifierPotent: Int,
    val secondaryAmplifier: Int,
) {
    val pointsText by lazy {
        "%.1f".format(beaconPoints)
    }
    val rangeText by lazy {
        range.toString()
    }
    val durationText by lazy {
        (duration.seconds / 20).toString()
    }

    val blocksInBase by lazy {
        baseBlocks.asSequence().filter { (_, count) ->
            count > 0
        }.sortedByDescending {
            it.value
        }.take(5).map { (block, count) ->
            block to "×$count"
        }
    }

    companion object {
        fun from(beacon: OverhauledBeacon): OverhauledBeaconData {
            return OverhauledBeaconData(
                beacon.baseBlocks.asMap(),
                beacon.level,
                beacon.beaconPoints,
                beacon.range,
                beacon.duration,
                beacon.primaryAmplifier,
                beacon.primaryAmplifierPotent,
                beacon.secondaryAmplifier,
            )
        }

        val UNIT = OverhauledBeaconData(emptyMap(), 0, 0.0, 0, 0, 0, 0, 0)
    }
}

@file:UseSerializers(BlockSerializer::class)

package gay.solonovamax.beaconsoverhaul.beacon.serializable

import gay.solonovamax.beaconsoverhaul.OverhauledBeacon
import gay.solonovamax.beaconsoverhaul.config.serializer.BlockSerializer
import gay.solonovamax.beaconsoverhaul.util.toMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.minecraft.block.Block

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
    companion object {
        fun from(beacon: OverhauledBeacon): OverhauledBeaconData {
            return OverhauledBeaconData(
                beacon.baseBlocks.toMap(),
                beacon.level,
                beacon.beaconPoints,
                beacon.range,
                beacon.duration,
                beacon.primaryAmplifier,
                beacon.primaryAmplifierPotent,
                beacon.secondaryAmplifier,
            )
        }

        val EMPTY = OverhauledBeaconData(emptyMap(), 0, 0.0, 0, 0, 0, 0, 0)
    }
}

package gay.solonovamax.beaconsoverhaul

import ca.solostudios.guava.kotlin.collect.MutableMultiset
import net.minecraft.block.Block
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface OverhauledBeacon {
    val baseBlocks: MutableMultiset<Block>
    var level: Int

    var beaconPoints: Double

    val range: Int
    val duration: Int

    var primaryEffect: StatusEffect?
    var secondaryEffect: StatusEffect?
    val world: World?
    val pos: BlockPos
    val beamSegments: List<BeaconBlockEntity.BeamSegment>

    val primaryAmplifier: Int
    val primaryAmplifierPotent: Int
    val secondaryAmplifier: Int
}

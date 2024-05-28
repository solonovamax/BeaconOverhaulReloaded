package gay.solonovamax.beaconsoverhaul.block.beacon

import ca.solostudios.guava.kotlin.collect.MutableMultiset
import kotlinx.datetime.Instant
import net.minecraft.block.Block
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface OverhauledBeacon {
    var lastUpdate: Instant

    var baseBlocks: MutableMultiset<Block>
    var level: Int

    var beaconPoints: Double

    val propertyDelegate: OverhauledBeaconPropertyDelegate

    val range: Int
    val duration: Int

    var primaryEffect: StatusEffect?
    var secondaryEffect: StatusEffect?
    val world: World?
    val pos: BlockPos
    val beamSegments: List<BeaconBlockEntity.BeamSegment>
    var beamSegmentsToCheck: List<BeaconBlockEntity.BeamSegment>

    var minY: Int

    var didRedirection: Boolean

    val primaryAmplifier: Int
    val primaryAmplifierPotent: Int
    val secondaryAmplifier: Int

    val listeningPlayers: List<ServerPlayerEntity>
    fun addUpdateListener(player: ServerPlayerEntity)
    fun removeUpdateListener(player: PlayerEntity)

    fun canApplyEffect(effect: StatusEffect): Boolean
}

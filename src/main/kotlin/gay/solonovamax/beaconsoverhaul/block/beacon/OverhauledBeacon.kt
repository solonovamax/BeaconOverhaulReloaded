package gay.solonovamax.beaconsoverhaul.block.beacon

import ca.solostudios.guava.kotlin.collect.MutableMultiset
import gay.solonovamax.beaconsoverhaul.block.beacon.blockentity.BeaconBeamSegment
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlinx.datetime.Instant

interface OverhauledBeacon {
    var lastUpdate: Instant

    var baseBlocks: MutableMultiset<Block>
    var level: Int

    var beaconPoints: Double

    val propertyDelegate: OverhauledBeaconPropertyDelegate

    val range: Int
    val duration: Int

    var primaryEffect: RegistryEntry<StatusEffect>?
    var secondaryEffect: RegistryEntry<StatusEffect>?
    val world: World
    val pos: BlockPos
    val beamSegments: List<BeaconBeamSegment>
    var beamSegmentsToCheck: List<BeaconBeamSegment>

    var brokenBeam: Boolean

    var minY: Int

    var didRedirection: Boolean

    val primaryAmplifier: Int
    val primaryAmplifierPotent: Int
    val secondaryAmplifier: Int

    val listeningPlayers: List<ServerPlayerEntity>
    fun addUpdateListener(player: ServerPlayerEntity)
    fun removeUpdateListener(player: PlayerEntity)

    fun canApplyEffect(effect: RegistryEntry<StatusEffect>): Boolean

    fun canPlaceNextMatching(state: BlockState): Boolean
    fun tryPlaceNextMatching(state: BlockState): Boolean
}

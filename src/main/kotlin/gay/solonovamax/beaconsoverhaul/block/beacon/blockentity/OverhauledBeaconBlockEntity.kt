package gay.solonovamax.beaconsoverhaul.block.beacon.blockentity

import ca.solostudios.guava.kotlin.collect.Multiset
import ca.solostudios.guava.kotlin.collect.MutableMultiset
import ca.solostudios.guava.kotlin.collect.mutableMultisetOf
import gay.solonovamax.beaconsoverhaul.BeaconOverhaulReloaded
import gay.solonovamax.beaconsoverhaul.advancement.RedirectBeaconCriterion
import gay.solonovamax.beaconsoverhaul.block.beacon.OverhauledBeacon
import gay.solonovamax.beaconsoverhaul.block.beacon.data.OverhauledBeaconData
import gay.solonovamax.beaconsoverhaul.config.BeaconOverhaulConfigManager
import gay.solonovamax.beaconsoverhaul.registry.TagRegistry
import gay.solonovamax.beaconsoverhaul.screen.OverhauledBeaconScreenHandler
import gay.solonovamax.beaconsoverhaul.util.contains
import gay.solonovamax.beaconsoverhaul.util.getNonSpectatingEntities
import kotlinx.datetime.Clock
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.Stainable
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.tag.BlockTags
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3i
import net.minecraft.world.Heightmap
import net.minecraft.world.World

fun OverhauledBeacon.createMenu(
    syncId: Int,
    player: PlayerEntity,
): OverhauledBeaconScreenHandler {
    val context = ScreenHandlerContext.create(this.world, this.pos)

    if (player is ServerPlayerEntity)
        addUpdateListener(player)

    val beaconScreenHandler = OverhauledBeaconScreenHandler(
        syncId,
        player,
        this.propertyDelegate,
        context,
    ) { player: PlayerEntity ->
        this.removeUpdateListener(player)
    }

    return beaconScreenHandler
}

fun OverhauledBeacon.updateTier(world: World, pos: BlockPos) {
    if (!shouldUpdateBeacon(world, pos))
        return

    val oldBaseBlocks = this.baseBlocks
    val oldBeaconPoints = this.beaconPoints

    val (level, baseBlocks) = buildBlockMultiset(pos.y, world, pos.x, pos.z)

    if (level > this.level) {
        // if (/*!broke &&*/beamSegmentsToCheck.isNotEmpty()) {
        for (player in world.getNonSpectatingEntities<ServerPlayerEntity>(Box(pos, pos).expand(10.0)))
            Criteria.CONSTRUCT_BEACON.trigger(player, level)
        // }
    }

    this.baseBlocks = baseBlocks
    this.level = level
    this.beaconPoints = computePoints(baseBlocks)

    if (this.beaconPoints != oldBeaconPoints || this.baseBlocks != oldBaseBlocks) {
        for (player in this.listeningPlayers)
            BeaconOverhaulReloaded.updateBeaconPacket.send(OverhauledBeaconData.from(this), player)
    }
}

private fun buildBlockMultiset(
    y: Int,
    world: World,
    x: Int,
    z: Int,
): Pair<Int, MutableMultiset<Block>> {
    var level = 0

    val baseBlocks = mutableMultisetOf<Block>()

    for (layerOffset in 1..BeaconOverhaulConfigManager.config.maxBeaconLayers) {
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
    val now = Clock.System.now()

    val updateDelay = BeaconOverhaulConfigManager.config.beaconUpdateDelay
    val initialUpdateDelay = BeaconOverhaulConfigManager.config.initialBeaconUpdateDelay

    when {
        now - this.lastUpdate > updateDelay -> {
            this.lastUpdate = Clock.System.now()
            return true
        }

        level > 0 -> {
            return false
        }

        now - this.lastUpdate <= initialUpdateDelay -> {
            return false
        }

        else -> {
            this.lastUpdate = Clock.System.now()

            for (xOffset in -1..1)
                for (zOffset in -1..1)
                    if (world.getBlockState(pos.add(xOffset, -1, zOffset)) !in BlockTags.BEACON_BASE_BLOCKS)
                        return false

            return true
        }
    }
}


private fun computePoints(baseBlocks: Multiset<Block>): Double {
    var result = 0.0
    // addition modifiers (ie. most blocks)
    for ((block, expression) in BeaconOverhaulConfigManager.config.additionModifierBlocks) {
        if (block in baseBlocks) {
            val expressionResult = expression.evaluate(baseBlocks[block].toDouble())
            result += expressionResult
        }
    }
    // multiplication modifiers (ie. netherite)
    for ((block, expression) in BeaconOverhaulConfigManager.config.multiplicationModifierBlocks) {
        if (block in baseBlocks) {
            val expressionResult = expression.evaluate(baseBlocks[block].toDouble())
            result *= expressionResult
        }
    }

    return result
}

fun OverhauledBeacon.writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
    val data = OverhauledBeaconData.from(this)
    val bytes = Cbor.encodeToByteArray(data)
    buf.writeByteArray(bytes)
}

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

fun OverhauledBeacon.constructBeamSegments() {
    val world = world!!

    if (world.time % BeaconOverhaulConfigManager.config.beamUpdateFrequency != 0L)
        return

    if (level == 0)
        return

    var currPos = pos

    var horizontalMoves = BeaconOverhaulConfigManager.config.redirectionHorizontalMoveLimit
    val targetHeight = world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.x, pos.z)

    var broke = false
    var didRedirection = false

    val beamSegmentsToCheck = mutableListOf<ExtendedBeamSegment>()

    var currColor = floatArrayOf(1.0f, 1.0f, 1.0f)
    var alpha = 1.0f

    var lastDirection: Direction? = null
    var currSegment = ExtendedBeamSegment(Direction.UP, Vec3i.ZERO, currColor, alpha, currColor, alpha)

    val seenPositions = HashSet<BlockPos>()
    var check = true
    var hardColorSet = false


    while (world.isInBuildLimit(currPos) && horizontalMoves > 0) {
        if (currSegment.direction === Direction.UP && currSegment.direction !== lastDirection) {
            val heightmapVal = world.getTopY(Heightmap.Type.WORLD_SURFACE, currPos.x, currPos.z)
            if (heightmapVal == (currPos.y + 1)) {
                currSegment.setHeight(heightmapVal + 1000)
                break
            }

            lastDirection = currSegment.direction
        }

        currPos = currPos.offset(currSegment.direction)
        if (currSegment.direction.axis.isHorizontal)
            horizontalMoves--
        else
            horizontalMoves = BeaconOverhaulConfigManager.config.redirectionHorizontalMoveLimit

        val state = world.getBlockState(currPos)
        val block = state.block

        var targetColor = block.beaconColorMultiplier
        var targetAlpha = -1.0f

        if (BeaconOverhaulConfigManager.config.allowTintedGlassTransparency) {
            if (block === Blocks.TINTED_GLASS) {
                targetAlpha = if (alpha < 0.3f) 0f else (alpha * 0.75f)
            }
        }

        if (isRedirectingBlock(block)) {
            val dir = state[Properties.FACING]
            if (dir == currSegment.direction) {
                currSegment.increaseHeight()
            } else {
                check = true
                beamSegmentsToCheck.add(currSegment)

                targetColor = floatArrayOf(1.0f, 1.0f, 1.0f)
                if (targetColor[0] == 1.0f && targetColor[1] == 1.0f && targetColor[2] == 1.0f)
                    targetColor = currColor

                val mixedColor = floatArrayOf(
                    (currColor[0] + targetColor[0] * 3) / 4.0f,
                    (currColor[1] + targetColor[1] * 3) / 4.0f,
                    (currColor[2] + targetColor[2] * 3) / 4.0f
                )
                currColor = mixedColor

                didRedirection = true
                lastDirection = currSegment.direction
                currSegment.isTurn = true
                currSegment = ExtendedBeamSegment(
                    dir,
                    currPos.subtract(pos),
                    currColor,
                    alpha,
                    currColor,
                    alpha,
                    previousSegmentIsTurn = true,
                )
            }
        } else if (targetColor != null || targetAlpha != -1.0f) {
            if (targetColor.contentEquals(currColor) && targetAlpha == alpha) {
                currSegment.increaseHeight()
            } else {
                check = true
                beamSegmentsToCheck.add(currSegment)

                val previousColor = currColor
                val previousAlpha = alpha

                var mixedColor = currColor
                if (targetColor != null) {
                    mixedColor = floatArrayOf(
                        (currColor[0] + targetColor[0]) / 2.0f,
                        (currColor[1] + targetColor[1]) / 2.0f,
                        (currColor[2] + targetColor[2]) / 2.0f
                    )

                    if (!hardColorSet) {
                        mixedColor = targetColor
                        hardColorSet = true
                    }

                    currColor = mixedColor
                }

                if (targetAlpha != -1.0f)
                    alpha = targetAlpha

                lastDirection = currSegment.direction
                currSegment = ExtendedBeamSegment(
                    currSegment.direction,
                    currPos.subtract(pos),
                    mixedColor,
                    alpha,
                    previousColor,
                    previousAlpha
                )
            }
        } else {
            // skip transparent blocks & blocks in the beacon transparent tag (bedrock)
            if (state !in TagRegistry.BEACON_TRANSPARENT && state.getOpacity(world, currPos) >= 15) {
                if (currSegment.direction == Direction.UP)
                    broke = true
                break
            }

            currSegment.increaseHeight()

            if (state in TagRegistry.BEACON_TRANSPARENT)
                continue
        }

        if (check) {
            val added = seenPositions.add(currPos)
            if (!added) {
                broke = true
                break
            }
        }
    }

    if (horizontalMoves == 0 || currPos.y <= world.bottomY)
        broke = true

    if (!broke) {
        beamSegmentsToCheck.add(currSegment)
        minY = targetHeight + 1
    } else {
        // Always show broken beams
        // TODO: Make broken beams blink red
        beamSegmentsToCheck.add(currSegment)
        // beamSegmentsToCheck.clear()

        minY = targetHeight

        if (horizontalMoves == 0)
            currSegment.setHeight(1000)
    }

    if (!this.didRedirection && didRedirection) {
        if (/*!broke &&*/beamSegmentsToCheck.isNotEmpty()) {
            for (player in world.getNonSpectatingEntities<ServerPlayerEntity>(Box(pos, pos).expand(10.0)))
                RedirectBeaconCriterion.trigger(player)
        }
    }

    this.didRedirection = didRedirection
    this.beamSegmentsToCheck = beamSegmentsToCheck
}

private fun isRedirectingBlock(block: Block): Boolean {
    return block === Blocks.AMETHYST_CLUSTER
}

val Block.beaconColorMultiplier: FloatArray?
    get() {
        if (this is Stainable)
            return color.colorComponents

        return null
    }

package gay.solonovamax.beaconsoverhaul.block.beacon.blockentity

// @Suppress("DuplicatedCode")
// abstract class AbstractBeaconBlockEntity(
//     type: BlockEntityType<*>,
//     pos: BlockPos,
//     state: BlockState,
// ) : BlockEntity(type, pos, state), NamedScreenHandlerFactory, Nameable, ExtendedScreenHandlerFactory {
//     var beamSegments: List<BeaconBlockEntity.BeamSegment> = listOf()
//     var beamSegmentsToCheck: List<BeaconBlockEntity.BeamSegment> = listOf()
//     var level: Int = 0
//     var minY: Int = 0
//     private var customName: Text? = null
//     private var lock: ContainerLock = ContainerLock.EMPTY
//
//     private val propertyDelegate = OverhauledBeaconPropertyDelegate(this as OverhauledBeacon)
//
//     abstract val range: Int
//
//     abstract val duration: Int
//
//     var primaryEffect: StatusEffect? = null
//     var secondaryEffect: StatusEffect? = null
//
//     private val listeningPlayers: MutableList<ServerPlayerEntity> = mutableListOf()
//
//     private var lastUpdate: Instant = Instant.DISTANT_PAST
//
//     var baseBlocks = mutableMultisetOf<Block>()
//
//     var beaconPoints = 0.0
//
//     private var didRedirection = false
//
//     abstract val primaryAmplifier: Int
//
//     abstract val primaryAmplifierPotent: Int
//
//     abstract val secondaryAmplifier: Int
//
//     abstract val defaultColor: FloatArray
//
//     fun updateTier(world: World, pos: BlockPos) {
//         if (!shouldUpdateBeacon(world, pos))
//             return
//
//         val oldBaseBlocks = baseBlocks
//         val oldBeaconPoints = beaconPoints
//
//         val oldLevel = level
//         val (newLevel, newBaseBlocks) = buildBlockMultiset(pos.y, world, pos.x, pos.z)
//
//         if (oldLevel != newLevel) {
//             // if (/*!broke &&*/beamSegmentsToCheck.isNotEmpty()) {
//             for (player in world.nonSpectatingEntities<ServerPlayerEntity>(Box(pos, pos).expand(10.0)))
//                 Criteria.CONSTRUCT_BEACON.trigger(player, newLevel)
//             // }
//         }
//
//         baseBlocks = newBaseBlocks
//         level = newLevel
//         beaconPoints = computePoints(newBaseBlocks)
//
//         if (beaconPoints != oldBeaconPoints || baseBlocks != oldBaseBlocks) {
//             for (player in this.listeningPlayers) {
//                 BeaconOverhaulReloaded.updateBeaconPacket.send(OverhauledBeaconData.from(this), player)
//             }
//         }
//     }
//
//     private fun shouldUpdateBeacon(world: World, pos: BlockPos): Boolean {
//         val now = Clock.System.now()
//
//         val updateDelay = BeaconOverhaulConfigManager.config.beaconUpdateDelay
//         val initialUpdateDelay = BeaconOverhaulConfigManager.config.initialBeaconUpdateDelay
//
//         return when {
//             now - lastUpdate > updateDelay -> {
//                 lastUpdate = Clock.System.now()
//                 true
//             }
//
//             level > 0 -> {
//                 false
//             }
//
//             now - lastUpdate <= initialUpdateDelay -> {
//                 false
//             }
//
//             else -> {
//                 lastUpdate = Clock.System.now()
//
//                 // very quick check (only check base)
//                 for (xOffset in -1..1)
//                     for (zOffset in -1..1)
//                         if (world.getBlockState(pos.add(xOffset, -1, zOffset)) !in BlockTags.BEACON_BASE_BLOCKS)
//                             return false
//
//                 true
//             }
//         }
//     }
//
//     private fun applyPlayerEffects(world: World, pos: BlockPos) {
//         val primaryEffect = this.primaryEffect
//         val secondaryEffect = this.secondaryEffect
//         if (world.isClient || primaryEffect == null)
//             return
//
//         val entities = world.nonSpectatingEntities<ServerPlayerEntity>(Box(pos).expand(range).stretch(0, world.height, 0))
//         val potent = level >= 4 && primaryEffect === secondaryEffect
//         applyPrimaryStatusEffect(potent, entities, primaryEffect)
//
//         if (level >= 4 && !potent && secondaryEffect != null) {
//             applySecondaryStatusEffect(entities, secondaryEffect)
//         }
//     }
//
//     abstract fun applyPrimaryStatusEffect(potent: Boolean, entities: List<LivingEntity>, primaryEffect: StatusEffect)
//
//     abstract fun applySecondaryStatusEffect(entities: List<LivingEntity>, secondaryEffect: StatusEffect)
//
//     inline fun StatusEffect.computeAmplifier(amplifierFunction: () -> Int): Int {
//         return if (BeaconOverhaulConfigManager.config.levelOneStatusEffects.contains(this))
//             0
//         else
//             amplifierFunction() - 1
//     }
//
//     fun constructBeamSegments() {
//         val world = world
//
//         if (world == null || world.time % BeaconOverhaulConfigManager.config.beamUpdateFrequency != 0L || level == 0)
//             return
//
//         var currentPosition = pos
//
//         var horizontalMoves = BeaconOverhaulConfigManager.config.redirectionHorizontalMoveLimit
//         val targetHeight = world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.x, pos.z)
//
//         var broke = false
//         var didRedirection = false
//
//         val beamSegmentsToCheck = mutableListOf<ExtendedBeamSegment>()
//
//         var color = defaultColor
//         var alpha = 1.0f
//
//         var lastDirection = Direction.UP
//         var segment = ExtendedBeamSegment(Direction.UP, Vec3i.ZERO, color, alpha, color, alpha)
//
//         val seenPositions = hashSetOf<Pair<BlockPos, Direction>>()
//         var check = true
//         var hardColorSet = false
//
//
//         while (world.isInBuildLimit(currentPosition) && horizontalMoves > 0) {
//             if (segment.direction === Direction.UP && segment.direction !== lastDirection) {
//                 val heightmapVal = world.getTopY(Heightmap.Type.WORLD_SURFACE, currentPosition.x, currentPosition.z)
//                 if (heightmapVal == (currentPosition.y + 1)) {
//                     segment.setHeight(heightmapVal + 1000)
//                     break
//                 }
//
//                 lastDirection = segment.direction
//             }
//
//             currentPosition = currentPosition.offset(segment.direction)
//             if (segment.direction.axis.isHorizontal)
//                 horizontalMoves--
//             else
//                 horizontalMoves = BeaconOverhaulConfigManager.config.redirectionHorizontalMoveLimit
//
//             val state = world.getBlockState(currentPosition)
//             val block = state.block
//
//             var targetColor = if (block is Stainable)
//                 block.color.colorComponents
//             else
//                 null
//             var targetAlpha = -1.0f
//
//             if (BeaconOverhaulConfigManager.config.allowTintedGlassTransparency) {
//                 if (block === Blocks.TINTED_GLASS) {
//                     targetAlpha = if (alpha < 0.3f) 0f else (alpha * 0.75f)
//                 }
//             }
//
//             when {
//                 isRedirectingBlock(block) -> {
//                     val dir = state[Properties.FACING]
//                     if (dir == segment.direction) {
//                         segment.increaseHeight()
//                     } else {
//                         check = true
//                         beamSegmentsToCheck.add(segment)
//
//                         targetColor = floatArrayOf(1.0f, 1.0f, 1.0f)
//                         if (targetColor[0] == 1.0f && targetColor[1] == 1.0f && targetColor[2] == 1.0f)
//                             targetColor = color
//
//                         val mixedColor = floatArrayOf(
//                             (color[0] + targetColor[0] * 3) / 4.0f,
//                             (color[1] + targetColor[1] * 3) / 4.0f,
//                             (color[2] + targetColor[2] * 3) / 4.0f
//                         )
//                         color = mixedColor
//
//                         didRedirection = true
//                         lastDirection = segment.direction
//                         segment.isTurn = true
//                         segment = ExtendedBeamSegment(
//                             dir,
//                             currentPosition.subtract(pos),
//                             color,
//                             alpha,
//                             color,
//                             alpha,
//                             previousSegmentIsTurn = true,
//                         )
//                     }
//                 }
//
//                 targetColor != null || targetAlpha != -1.0f -> {
//                     if (targetColor.contentEquals(color) && targetAlpha == alpha) {
//                         segment.increaseHeight()
//                     } else {
//                         check = true
//                         beamSegmentsToCheck.add(segment)
//
//                         val previousColor = color
//                         val previousAlpha = alpha
//
//                         var computedColor = color
//
//                         if (targetColor != null) {
//                             computedColor = floatArrayOf(
//                                 (color[0] + targetColor[0]) / 2.0f,
//                                 (color[1] + targetColor[1]) / 2.0f,
//                                 (color[2] + targetColor[2]) / 2.0f
//                             )
//
//                             if (!hardColorSet) {
//                                 computedColor = targetColor
//                                 hardColorSet = true
//                             }
//
//                             color = computedColor
//                         }
//
//                         if (targetAlpha != -1.0f)
//                             alpha = targetAlpha
//
//                         lastDirection = segment.direction
//                         segment = ExtendedBeamSegment(
//                             segment.direction,
//                             currentPosition.subtract(pos),
//                             computedColor,
//                             alpha,
//                             previousColor,
//                             previousAlpha
//                         )
//                     }
//                 }
//
//                 else -> {
//                     // skip transparent blocks & blocks in the beacon transparent tag (bedrock)
//                     if (state !in TagRegistry.BEACON_TRANSPARENT && state.getOpacity(world, currentPosition) >= 15) {
//                         if (segment.direction == Direction.UP)
//                             broke = true
//                         break
//                     }
//
//                     segment.increaseHeight()
//
//                     if (state in TagRegistry.BEACON_TRANSPARENT)
//                         continue
//                 }
//             }
//
//             if (check) {
//                 val added = seenPositions.add(currentPosition to lastDirection)
//                 if (!added) {
//                     broke = true
//                     break
//                 }
//             }
//         }
//
//         if (horizontalMoves == 0 || currentPosition.y <= world.bottomY)
//             broke = true
//
//         if (!broke) {
//             beamSegmentsToCheck.add(segment)
//             minY = targetHeight + 1
//         } else {
//             // Always show broken beams
//             // TODO: Make broken beams blink red
//             beamSegmentsToCheck.add(segment)
//             // beamSegmentsToCheck.clear()
//
//             minY = targetHeight
//
//             if (horizontalMoves == 0)
//                 segment.setHeight(1000)
//         }
//
//         if (!this.didRedirection && didRedirection) {
//             if (/*!broke &&*/beamSegmentsToCheck.isNotEmpty()) {
//                 for (player in world.nonSpectatingEntities<ServerPlayerEntity>(Box(pos, pos).expand(10.0)))
//                     RedirectBeaconCriterion.trigger(player)
//             }
//         }
//
//         this.didRedirection = didRedirection
//         this.beamSegmentsToCheck = beamSegmentsToCheck
//     }
//
//     override fun markRemoved() {
//         world?.playSound(pos, SoundEvents.BLOCK_BEACON_DEACTIVATE)
//
//         super.markRemoved()
//     }
//
//     override fun toUpdatePacket(): BlockEntityUpdateS2CPacket {
//         return BlockEntityUpdateS2CPacket.create(this)
//     }
//
//     override fun toInitialChunkDataNbt(): NbtCompound {
//         return createNbt()
//     }
//
//     override fun readNbt(nbt: NbtCompound) {
//         super.readNbt(nbt)
//         if (nbt.contains("CustomName", 8))
//             this.customName = Text.Serializer.fromJson(nbt.getString("CustomName"))
//
//         this.lock = ContainerLock.fromNbt(nbt)
//         this.level = nbt.getInt("Levels")
//         this.beaconPoints = nbt.getDouble("BeaconPoints")
//         this.didRedirection = nbt.getBoolean("DidRedirection")
//
//         this.primaryEffect = nbt.getString("Primary").takeIf { it.isNotBlank() }?.let { Registries.STATUS_EFFECT.get(Identifier(it)) }
//         this.secondaryEffect = nbt.getString("Secondary").takeIf { it.isNotBlank() }?.let { Registries.STATUS_EFFECT.get(Identifier(it)) }
//     }
//
//     public override fun writeNbt(nbt: NbtCompound) {
//         super.writeNbt(nbt)
//
//         nbt.putInt("Levels", this.level)
//         if (this.customName != null)
//             nbt.putString("CustomName", Text.Serializer.toJson(this.customName))
//
//         this.lock.writeNbt(nbt)
//         nbt.putDouble("BeaconPoints", this.beaconPoints)
//         nbt.putBoolean("DidRedirection", this.didRedirection)
//
//         val primaryId = Registries.STATUS_EFFECT.getId(this.primaryEffect)
//         if (this.primaryEffect != null && primaryId != null)
//             nbt.putString("Primary", primaryId.toString())
//
//         val secondaryId = Registries.STATUS_EFFECT.getId(this.secondaryEffect)
//         if (this.secondaryEffect != null && secondaryId != null)
//             nbt.putString("Secondary", secondaryId.toString())
//     }
//
//     fun setCustomName(customName: Text) {
//         this.customName = customName
//     }
//
//     override fun getCustomName(): Text? = customName
//
//     override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity): ScreenHandler? {
//         return if (LockableContainerBlockEntity.checkUnlocked(player, this.lock, this.displayName)) {
//             val context = ScreenHandlerContext.create(this.world, this.pos)
//
//             if (player is ServerPlayerEntity) {
//                 listeningPlayers.add(player)
//             }
//
//             return OverhauledBeaconScreenHandler(syncId, player, propertyDelegate, context) { player ->
//                 listeningPlayers.remove(player)
//             }
//         } else {
//             null
//         }
//     }
//
//     override fun getDisplayName(): Text = this.name
//
//     override fun getName(): Text = this.customName ?: CONTAINER_NAME_TEXT
//
//     override fun setWorld(world: World) {
//         super.setWorld(world)
//         this.minY = world.bottomY - 1
//     }
//
//     override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
//         val data = OverhauledBeaconData.from(this)
//         val bytes = Cbor.encodeToByteArray(data)
//         buf.writeByteArray(bytes)
//     }
//
//     fun canApplyEffect(effect: StatusEffect): Boolean {
//         return when {
//             level == 0 -> false
//             effect in BeaconOverhaulConfigManager.config.beaconEffectsByTier.tierOne -> level >= 1
//             effect in BeaconOverhaulConfigManager.config.beaconEffectsByTier.tierTwo -> level >= 2
//             effect in BeaconOverhaulConfigManager.config.beaconEffectsByTier.tierThree -> level >= 3
//             effect !in BeaconOverhaulConfigManager.config.beaconEffectsByTier.secondaryEffects -> level >= 4
//             else -> false
//         }
//     }
//
//     companion object {
//         private val CONTAINER_NAME_TEXT = Text.translatable("container.beacon")
//
//         fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: AbstractBeaconBlockEntity) {
//             if (blockEntity.minY < pos.y) {
//                 blockEntity.beamSegmentsToCheck = listOf()
//                 blockEntity.minY = pos.y - 1
//             }
//
//             blockEntity.constructBeamSegments()
//
//             val previousLevel = blockEntity.level
//             blockEntity.updateTier(world, pos)
//
//             if (world.time % 80L == 0L) {
//                 if (blockEntity.level > 0 && blockEntity.beamSegments.isNotEmpty()) {
//                     blockEntity.applyPlayerEffects(world, pos)
//                     world.playSound(pos, SoundEvents.BLOCK_BEACON_AMBIENT)
//                 }
//             }
//
//             if (blockEntity.minY >= world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.x, pos.z)) {
//                 blockEntity.minY = world.bottomY - 1
//                 val wasAboveLevelZero = previousLevel > 0
//                 blockEntity.beamSegments = blockEntity.beamSegmentsToCheck
//                 if (!world.isClient) {
//                     val aboveLevelZero = blockEntity.level > 0
//                     if (!wasAboveLevelZero && aboveLevelZero) {
//                         world.playSound(pos, SoundEvents.BLOCK_BEACON_ACTIVATE)
//                     } else if (wasAboveLevelZero && !aboveLevelZero) {
//                         world.playSound(pos, SoundEvents.BLOCK_BEACON_DEACTIVATE)
//                     }
//                 }
//             }
//         }
//
//         private fun buildBlockMultiset(
//             y: Int,
//             world: World,
//             x: Int,
//             z: Int,
//         ): Pair<Int, MutableMultiset<Block>> {
//             var level = 0
//
//             val baseBlocks = mutableMultisetOf<Block>()
//
//             for (layerOffset in 1..BeaconOverhaulConfigManager.config.maxBeaconLayers) {
//                 val yOffset = y - layerOffset
//
//                 if (yOffset < world.bottomY)
//                     break
//
//                 val layerContents = mutableMultisetOf<Block>()
//                 for (xOffset in x - layerOffset..x + layerOffset) {
//                     for (zOffset in z - layerOffset..z + layerOffset) {
//                         val state = world.getBlockState(BlockPos(xOffset, yOffset, zOffset))
//
//                         if (state !in BlockTags.BEACON_BASE_BLOCKS)
//                             return level to baseBlocks
//
//                         layerContents.add(state.block)
//                     }
//                 }
//
//                 baseBlocks.addAll(layerContents)
//                 level = layerOffset
//             }
//
//             return level to baseBlocks
//         }
//
//         private fun computePoints(baseBlocks: Multiset<Block>): Double {
//             var result = 0.0
//             // addition modifiers (ie. most blocks)
//             for ((block, expression) in BeaconOverhaulConfigManager.config.additionModifierBlocks) {
//                 if (block in baseBlocks) {
//                     val expressionResult = expression.evaluate(baseBlocks[block].toDouble())
//                     result += expressionResult
//                 }
//             }
//
//             // multiplication modifiers (ie. netherite)
//             for ((block, expression) in BeaconOverhaulConfigManager.config.multiplicationModifierBlocks) {
//                 if (block in baseBlocks) {
//                     val expressionResult = expression.evaluate(baseBlocks[block].toDouble())
//                     result *= expressionResult
//                 }
//             }
//
//             return result
//         }
//
//         private fun World.playSound(pos: BlockPos, sound: SoundEvent) {
//             playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0f, 1.0f)
//         }
//
//         private fun isRedirectingBlock(block: Block): Boolean {
//             return block === Blocks.AMETHYST_CLUSTER
//         }
//     }
// }

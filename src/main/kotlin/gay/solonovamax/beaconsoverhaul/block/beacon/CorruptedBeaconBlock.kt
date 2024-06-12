package gay.solonovamax.beaconsoverhaul.block.beacon

import net.minecraft.block.BeaconBlock
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.DyeColor
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CorruptedBeaconBlock(settings: Settings) : BeaconBlock(settings) {
    override fun getColor(): DyeColor {
        return DyeColor.WHITE
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return TODO()
        // return CorruptedBeaconBlockEntity(pos, state)
    }

    override fun <T : BlockEntity?> getTicker(world: World, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
        return null
        // return checkType(type, BlockEntityTypeRegistry.CORRUPTED_BEACON) { world, pos, state, blockEntity ->
        //     AbstractBeaconBlockEntity.tick(world, pos, state, blockEntity)
        // }
    }

    @Deprecated("Deprecated in Java")
    override fun onUse(
        state: BlockState?,
        world: World,
        pos: BlockPos?,
        player: PlayerEntity,
        hand: Hand?,
        hit: BlockHitResult?,
    ): ActionResult {
        if (world.isClient) {
            return ActionResult.SUCCESS
        } else {
            val blockEntity = world.getBlockEntity(pos)
            // if (blockEntity is CorruptedBeaconBlockEntity) {
            //     player.openHandledScreen(blockEntity)
            //     player.incrementStat(Stats.INTERACT_WITH_BEACON)
            // }

            return ActionResult.CONSUME
        }
    }

    @Deprecated("Deprecated in Java")
    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        if (itemStack.hasCustomName()) {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity is BeaconBlockEntity) {
                blockEntity.customName = itemStack.name
            }
        }
    }
}

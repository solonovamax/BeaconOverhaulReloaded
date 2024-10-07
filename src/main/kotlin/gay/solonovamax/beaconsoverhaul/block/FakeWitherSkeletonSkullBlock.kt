package gay.solonovamax.beaconsoverhaul.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

@Suppress("OVERRIDE_DEPRECATION")
class FakeWitherSkeletonSkullBlock(settings: Settings) : Block(settings) {
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape = SHAPE

    override fun getCullingShape(state: BlockState, world: BlockView, pos: BlockPos): VoxelShape = VoxelShapes.empty()

    override fun canPathfindThrough(state: BlockState?, type: NavigationType?): Boolean = false

    companion object {
        val SHAPE: VoxelShape = createCuboidShape(4.0, 0.0, 4.0, 12.0, 8.0, 12.0)
    }
}

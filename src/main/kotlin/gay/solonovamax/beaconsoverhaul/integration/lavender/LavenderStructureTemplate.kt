package gay.solonovamax.beaconsoverhaul.integration.lavender

import io.wispforest.lavender.structure.StructureTemplate
import net.minecraft.util.BlockRotation
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i

class LavenderStructureTemplate(
    id: Identifier,
    val predicates: Array<out Array<Array<BlockStatePredicate>>>,
    xSize: Int,
    ySize: Int,
    zSize: Int,
    anchor: Vec3i?,
) : StructureTemplate(id, predicates, xSize, ySize, zSize, anchor), Iterable<Pair<BlockStatePredicate, BlockPos>> {
    override fun iterator() = LavenderStructureTemplateIterator(this, BlockRotation.NONE)

    fun iterator(rotation: BlockRotation = BlockRotation.NONE) = LavenderStructureTemplateIterator(this, rotation)

    class LavenderStructureTemplateIterator(
        private val structure: LavenderStructureTemplate,
        private val rotation: BlockRotation,
    ) : Iterator<Pair<BlockStatePredicate, BlockPos>> {
        private val current = BlockPos.Mutable()
        private var x = 0
        private var y = 0
        private var z = 0

        override fun hasNext() = !(x >= structure.xSize - 1 && y >= structure.ySize - 1 && z >= structure.zSize - 1)

        override fun next(): Pair<BlockStatePredicate, BlockPos> {
            when (rotation) {
                BlockRotation.CLOCKWISE_90 -> current.set(structure.zSize - z - 1, y, x)
                BlockRotation.COUNTERCLOCKWISE_90 -> current.set(z, y, structure.xSize - x - 1)
                BlockRotation.CLOCKWISE_180 -> current.set(structure.xSize - x - 1, y, structure.zSize - z - 1)
                else -> current.set(x, y, z)
            }
            val predicate = structure.predicates[x][y][z]

            if (z + 1 < structure.zSize) {
                z++
            } else if (y + 1 < structure.ySize) {
                z = 0
                y++
            } else {
                z = 0
                y = 0
                x++
            }

            return predicate to current
        }
    }
}

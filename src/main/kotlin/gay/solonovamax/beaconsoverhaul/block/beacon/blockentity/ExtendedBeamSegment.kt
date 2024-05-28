package gay.solonovamax.beaconsoverhaul.block.beacon.blockentity

import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3i


class ExtendedBeamSegment(
    val direction: Direction,
    val offset: Vec3i,
    color: FloatArray,
    val alpha: Float,
    val previousColor: FloatArray,
    val previousAlpha: Float,
    var isTurn: Boolean = false,
    var previousSegmentIsTurn: Boolean = false,
) : BeaconBlockEntity.BeamSegment(color) {
    public override fun increaseHeight() { // increase visibility
        super.increaseHeight()
    }

    override fun getHeight(): Int {
        return super.height
    }

    fun setHeight(target: Int) {
        height = target
    }
}

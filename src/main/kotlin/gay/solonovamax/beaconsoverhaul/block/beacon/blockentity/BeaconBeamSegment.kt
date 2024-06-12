package gay.solonovamax.beaconsoverhaul.block.beacon.blockentity

import com.github.ajalt.colormath.Color
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3i


data class BeaconBeamSegment(
    val direction: Direction,
    val offset: Vec3i,
    var color: Color,
    val previousColor: Color,
    var isTurn: Boolean = false,
    var previousSegmentIsTurn: Boolean = false,
    var height: Int = 1,
) {
    fun increaseHeight() {
        ++this.height
    }
}

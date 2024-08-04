package gay.solonovamax.beaconsoverhaul.util

import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import org.joml.Vector3d

operator fun Direction.not(): Direction = opposite

fun Box.expand(value: Int): Box {
    return expand(value.toDouble())
}

fun Box.stretch(x: Int, y: Int, z: Int): Box {
    return stretch(x.toDouble(), y.toDouble(), z.toDouble())
}

operator fun Vector3d.not(): Vector3d = times(-1f)

operator fun Vector3d.plus(n: Number): Vector3d = Vector3d(this).apply { n.toDouble().let { add(it, it, it) } }

operator fun Vector3d.minus(n: Number): Vector3d = Vector3d(this).apply { n.toDouble().let { sub(it, it, it) } }

operator fun Vector3d.times(n: Number): Vector3d = Vector3d(this).mul(n.toDouble())

operator fun Vector3d.div(n: Number): Vector3d = Vector3d(this).div(n.toDouble())

operator fun Vector3d.compareTo(n: Number) = this.length().compareTo(n.toDouble())

operator fun Vector3d.plus(vec: Vector3d): Vector3d = Vector3d(this).apply { add(vec) }

operator fun Vector3d.minus(vec: Vector3d): Vector3d = Vector3d(this).apply { sub(vec) }

operator fun Vector3d.times(vec: Vector3d): Vector3d = Vector3d(this).mul(vec)

operator fun Vector3d.compareTo(vec: Vector3d) = Vector3d(this).lengthSquared().compareTo(Vector3d(vec).lengthSquared())

operator fun Vector3d.component1() = x()
operator fun Vector3d.component2() = y()
operator fun Vector3d.component3() = z()

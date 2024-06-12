package gay.solonovamax.beaconsoverhaul.util

import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

operator fun Direction.not(): Direction = opposite

fun Box.expand(value: Int): Box {
    return expand(value.toDouble())
}

fun Box.stretch(x: Int, y: Int, z: Int): Box {
    return stretch(x.toDouble(), y.toDouble(), z.toDouble())
}

package gay.solonovamax.beaconsoverhaul.integration.azurelib

import com.eliotlash.mclib.math.IValue
import com.eliotlash.mclib.math.functions.Function
import net.minecraft.client.MinecraftClient

class RotationToCameraFunction(values: Array<out IValue>, name: String) : Function(values, name) {
    override fun get(): Double {
        return when (getArg(0)) {
            0.0 -> {
                -MinecraftClient.getInstance().gameRenderer.camera.pitch.toDouble()
            }

            1.0 -> {
                MinecraftClient.getInstance().gameRenderer.camera.yaw.toDouble()
            }

            else -> {
                error("Must be 0 or 1")
            }
        }
    }

    override fun getRequiredArguments() = 1
}

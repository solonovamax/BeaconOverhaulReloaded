package gay.solonovamax.beaconsoverhaul.integration.azurelib

import net.minecraft.client.MinecraftClient
import software.bernie.geckolib.loading.math.MathValue
import software.bernie.geckolib.loading.math.function.MathFunction

class RotationToCameraFunction(vararg values: MathValue) : MathFunction() {
    private val pitchOrYaw = values.single()

    override fun getName(): String = "query.rotation_to_camera"

    override fun compute(): Double {
        return when (pitchOrYaw.get()) {
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

    override fun getMinArgs() = 1

    override fun getArgs(): Array<MathValue> = arrayOf(pitchOrYaw)
}

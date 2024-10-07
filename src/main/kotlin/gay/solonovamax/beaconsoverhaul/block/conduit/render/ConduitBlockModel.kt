package gay.solonovamax.beaconsoverhaul.block.conduit.render

import gay.solonovamax.beaconsoverhaul.block.conduit.OverhauledConduitBlockEntity
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.screen.PlayerScreenHandler
import org.joml.Math
import software.bernie.geckolib.animation.AnimationState
import software.bernie.geckolib.loading.math.MathParser
import software.bernie.geckolib.loading.math.value.Variable
import software.bernie.geckolib.model.GeoModel
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

class ConduitBlockModel : GeoModel<OverhauledConduitBlockEntity>() {
    override fun getModelResource(conduit: OverhauledConduitBlockEntity) = CONDUIT_MODEL
    override fun getTextureResource(conduit: OverhauledConduitBlockEntity) = CONDUIT_TEXTURE
    override fun getAnimationResource(conduit: OverhauledConduitBlockEntity) = CONDUIT_ANIMATION
    fun getEyeSprite(conduit: OverhauledConduitBlockEntity): SpriteIdentifier {
        return if (conduit.isEyeOpen) CONDUIT_OPEN_EYE_SPRITE else CONDUIT_CLOSED_EYE_SPRITE
    }

    fun getEyeEmissiveSprite(conduit: OverhauledConduitBlockEntity): SpriteIdentifier {
        return if (conduit.isEyeOpen) CONDUIT_OPEN_EYE_SPRITE_EMISSIVE else CONDUIT_CLOSED_EYE_SPRITE_EMISSIVE
    }

    fun getWindTexture(conduit: OverhauledConduitBlockEntity): SpriteIdentifier {
        return when ((conduit.ticksActive / 66) % 3) {
            1 -> CONDUIT_WIND_VERTICAL_SPRITE
            else -> CONDUIT_WIND_SPRITE
        }
    }

    override fun applyMolangQueries(conduit: AnimationState<OverhauledConduitBlockEntity>, animTime: Double) {
        super.applyMolangQueries(conduit, animTime)

        val step = (animTime / 20) * 6
        val sineStep = sin(Math.toRadians(step))
        val largeMotion = sin(Math.toRadians(1 - (sineStep).pow(32)))
        val smallMotion = Math.toDegrees(largeMotion) * cos(Math.toRadians(step * 28)) / 4
        val shellCornerDistance = max(largeMotion * 96 - 0.01, 0.0) + smallMotion
        MathParser.setVariable("c.sinstep") { sineStep }
        MathParser.setVariable("c.step") { step }
        MathParser.setVariable("c.large_motion") { step }
        MathParser.setVariable("c.shell_corner_distance") { shellCornerDistance }
    }

    companion object {
        private val CONDUIT_MODEL = identifierOf("geo/block/conduit.geo.json")
        private val CONDUIT_TEXTURE = identifierOf("textures/entity/conduit/conduit.png")
        private val CONDUIT_ANIMATION = identifierOf("animations/block/conduit.animation.json")
        private val CONDUIT_EYE_OPEN_TEXTURE = identifierOf("entity/conduit/open_eye")
        private val CONDUIT_EYE_CLOSED_TEXTURE = identifierOf("entity/conduit/closed_eye")
        private val CONDUIT_OPEN_EYE_SPRITE = SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, CONDUIT_EYE_OPEN_TEXTURE)
        private val CONDUIT_CLOSED_EYE_SPRITE = SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, CONDUIT_EYE_CLOSED_TEXTURE)
        private val CONDUIT_OPEN_EYE_SPRITE_EMISSIVE =
            SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, CONDUIT_EYE_OPEN_TEXTURE.withPath { "${it}_glowmask" })
        private val CONDUIT_CLOSED_EYE_SPRITE_EMISSIVE =
            SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, CONDUIT_EYE_CLOSED_TEXTURE.withPath { "${it}_glowmask" })
        private val CONDUIT_WIND_TEXTURE = identifierOf("minecraft", "entity/conduit/wind")
        private val CONDUIT_WIND_SPRITE = SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, CONDUIT_WIND_TEXTURE)
        private val CONDUIT_WIND_VERTICAL_TEXTURE = identifierOf("minecraft", "entity/conduit/wind_vertical")
        private val CONDUIT_WIND_VERTICAL_SPRITE = SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, CONDUIT_WIND_VERTICAL_TEXTURE)

        private val SINESTEP_VARIABLE = Variable("c.sinstep") {
            1.0
        }
    }
}

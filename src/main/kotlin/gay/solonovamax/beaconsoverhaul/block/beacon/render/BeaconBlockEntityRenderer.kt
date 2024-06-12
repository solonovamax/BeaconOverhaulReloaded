package gay.solonovamax.beaconsoverhaul.block.beacon.render

import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.model.SRGB
import gay.solonovamax.beaconsoverhaul.block.beacon.OverhauledBeacon
import gay.solonovamax.beaconsoverhaul.block.beacon.blockentity.BeaconBeamSegment
import gay.solonovamax.beaconsoverhaul.config.BeaconOverhaulConfigManager
import gay.solonovamax.beaconsoverhaul.util.not
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import org.joml.Matrix3f
import org.joml.Matrix4f

private val RED = SRGB(1.0f, 0.0f, 0.0f, 0.5f)

fun render(
    beacon: OverhauledBeacon,
    partialTicks: Float,
    matrixStackIn: MatrixStack,
    bufferIn: VertexConsumerProvider,
    combinedLightIn: Int,
    combinedOverlayIn: Int,
) {
    for (segment in beacon.beamSegments) {
        renderBeamSegment(
            matrixStackIn,
            bufferIn,
            segment,
            partialTicks,
            beacon.world.time,
            BeaconOverhaulConfigManager.beaconConfig.beamRadius.toFloat(),
            BeaconOverhaulConfigManager.beaconConfig.beamGlowRadius.toFloat(),
            BeaconOverhaulConfigManager.beaconConfig.beamGlowOpacity.toFloat(),
            BeaconOverhaulConfigManager.beaconConfig.beamBlendPadding.toFloat(),
            beacon.brokenBeam,
        )
    }
}

fun renderBeamSegment(
    matrixStack: MatrixStack,
    bufferIn: VertexConsumerProvider,
    segment: BeaconBeamSegment,
    partialTicks: Float,
    totalWorldTime: Long,
    beamRadius: Float,
    glowRadius: Float,
    glowOpacity: Float,
    blendPadding: Float,
    broken: Boolean,
) {
    val blendLength = 1.0f - blendPadding * 2

    val showBrokenColor = broken && (totalWorldTime / 10) % 2 == 0L
    val color = if (showBrokenColor) RED else segment.color.toSRGB()
    val previousColor = if (showBrokenColor) RED else segment.previousColor.toSRGB()
    val same = color == previousColor && color.alpha == previousColor.alpha && !segment.isTurn && !segment.previousSegmentIsTurn
    val padding = if (same) 0.0f else blendPadding
    val height = segment.height.toFloat() + if (segment.isTurn) 0.5f else padding
    val offset = Vec3d.of(segment.offset).offset(!segment.direction, 0.5)

    if (color.alpha <= 0.05f && previousColor.alpha <= 0.05f)
        return // don't render shit (alpha is zero)

    matrixStack.push()
    matrixStack.translate(0.5, 0.5, 0.5)
    matrixStack.translate(
        offset.x,
        offset.y,
        offset.z
    ) // offset by the correct distance
    matrixStack.multiply(segment.direction.rotationQuaternion)

    val angle = Math.floorMod(totalWorldTime, 40L) + partialTicks

    matrixStack.push()
    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle * 2.25f - 45.0f))

    val renderTime = -(totalWorldTime + partialTicks)
    val partAngle = MathHelper.fractionalPart(renderTime * 0.2f - MathHelper.floor(angle * 0.1f).toFloat())
    val v2 = -1.0f + partAngle

    renderInnerBeam(
        segment,
        height,
        v2,
        matrixStack,
        bufferIn,
        color,
        previousColor,
        same,
        blendLength,
        blendPadding,
        beamRadius,
    )

    renderOuterGlow(
        segment,
        height,
        v2,
        matrixStack,
        bufferIn,
        color.copy(alpha = color.alpha * glowOpacity),
        previousColor.copy(alpha = previousColor.alpha * glowOpacity),
        same,
        blendLength,
        blendPadding,
        glowRadius,
    )
}

private fun renderInnerBeam(
    segment: BeaconBeamSegment,
    height: Float,
    v2: Float,
    matrixStack: MatrixStack,
    bufferIn: VertexConsumerProvider,
    color: RGB,
    oldColor: RGB,
    same: Boolean,
    blendLength: Float,
    blendPadding: Float,
    beamRadius: Float,
) {
    when {
        segment.previousSegmentIsTurn -> {
            renderPart(
                matrixStackIn = matrixStack,
                bufferIn = bufferIn.getBuffer(RenderLayer.getBeaconBeam(BeaconBlockEntityRenderer.BEAM_TEXTURE, color.alpha < 1f)),
                color = color,
                oldColor = color,
                height = height - blendPadding,
                offset = 0.7f,
                x1 = 0.0f,
                z1 = beamRadius,
                x2 = beamRadius,
                z2 = 0.0f,
                x3 = -beamRadius,
                z3 = 0.0f,
                x4 = 0.0f,
                z4 = -beamRadius,
                u1 = 0.0f,
                u2 = 1.0f,
                v1 = (height - 0.5f) * (0.5f / beamRadius) + v2,
                v2 = v2
            )
            renderPart(
                matrixStackIn = matrixStack,
                bufferIn = bufferIn.getBuffer(RenderLayer.getBeaconBeam(BeaconBlockEntityRenderer.BEAM_TEXTURE, true)),
                color = color,
                oldColor = color.copy(alpha = 0.0f),
                height = 0.7f,
                offset = 0.2f,
                x1 = 0.0f,
                z1 = beamRadius,
                x2 = beamRadius,
                z2 = 0.0f,
                x3 = -beamRadius,
                z3 = 0.0f,
                x4 = 0.0f,
                z4 = -beamRadius,
                u1 = 0.0f,
                u2 = 1.0f,
                v1 = 0.5f * (0.5f / beamRadius) + v2,
                v2 = v2
            )
        }

        same -> {
            renderPart(
                matrixStackIn = matrixStack,
                bufferIn = bufferIn.getBuffer(RenderLayer.getBeaconBeam(BeaconBlockEntityRenderer.BEAM_TEXTURE, color.alpha < 1f)),
                color = color,
                oldColor = color,
                height = height,
                offset = 0.0f,
                x1 = 0.0f,
                z1 = beamRadius,
                x2 = beamRadius,
                z2 = 0.0f,
                x3 = -beamRadius,
                z3 = 0.0f,
                x4 = 0.0f,
                z4 = -beamRadius,
                u1 = 0.0f,
                u2 = 1.0f,
                v1 = height * (0.5f / beamRadius) + v2,
                v2 = v2
            )

        }

        else -> {
            renderPart(
                matrixStackIn = matrixStack,
                bufferIn = bufferIn.getBuffer(RenderLayer.getBeaconBeam(BeaconBlockEntityRenderer.BEAM_TEXTURE, color.alpha < 1f)),
                color = color,
                oldColor = color,
                height = height - blendPadding,
                offset = blendLength,
                x1 = 0.0f,
                z1 = beamRadius,
                x2 = beamRadius,
                z2 = 0.0f,
                x3 = -beamRadius,
                z3 = 0.0f,
                x4 = 0.0f,
                z4 = -beamRadius,
                u1 = 0.0f,
                u2 = 1.0f,
                v1 = (height - blendLength) * (0.5f / beamRadius) + v2,
                v2 = v2
            )
            renderPart(
                matrixStackIn = matrixStack,
                bufferIn = bufferIn.getBuffer(
                    RenderLayer.getBeaconBeam(
                        BeaconBlockEntityRenderer.BEAM_TEXTURE,
                        color.alpha < 1f || oldColor.alpha < 1f
                    )
                ),
                color = color,
                oldColor = oldColor,
                height = blendLength,
                offset = 0f,
                x1 = 0.0f,
                z1 = beamRadius,
                x2 = beamRadius,
                z2 = 0.0f,
                x3 = -beamRadius,
                z3 = 0.0f,
                x4 = 0.0f,
                z4 = -beamRadius,
                u1 = 0.0f,
                u2 = 1.0f,
                v1 = blendLength * (0.5f / beamRadius) + v2,
                v2 = v2
            )
        }
    }

    matrixStack.pop()
}

private fun renderOuterGlow(
    segment: BeaconBeamSegment,
    height: Float,
    v2: Float,
    matrixStack: MatrixStack,
    bufferIn: VertexConsumerProvider,
    color: RGB,
    oldColor: RGB,
    same: Boolean,
    blendLength: Float,
    blendPadding: Float,
    glowRadius: Float,
) {
    when {
        segment.previousSegmentIsTurn -> {
            renderPart(
                matrixStackIn = matrixStack,
                bufferIn = bufferIn.getBuffer(RenderLayer.getBeaconBeam(BeaconBlockEntityRenderer.BEAM_TEXTURE, true)),
                color = color,
                oldColor = oldColor,
                height = height - blendPadding,
                offset = 0.7f,
                x1 = -glowRadius,
                z1 = -glowRadius,
                x2 = glowRadius,
                z2 = -glowRadius,
                x3 = -glowRadius,
                z3 = glowRadius,
                x4 = glowRadius,
                z4 = glowRadius,
                u1 = 0.0f,
                u2 = 1.0f,
                v1 = (height - 0.5f) + v2,
                v2 = v2
            )
            renderPart(
                matrixStackIn = matrixStack,
                bufferIn = bufferIn.getBuffer(RenderLayer.getBeaconBeam(BeaconBlockEntityRenderer.BEAM_TEXTURE, true)),
                color = color,
                oldColor = oldColor.copy(alpha = 0.0f),
                height = 0.7f,
                offset = 0.2f,
                x1 = -glowRadius,
                z1 = -glowRadius,
                x2 = glowRadius,
                z2 = -glowRadius,
                x3 = -glowRadius,
                z3 = glowRadius,
                x4 = glowRadius,
                z4 = glowRadius,
                u1 = 0.0f,
                u2 = 1.0f,
                v1 = 0.5f + v2,
                v2 = v2
            )
        }

        same -> {
            renderPart(
                matrixStackIn = matrixStack,
                bufferIn = bufferIn.getBuffer(RenderLayer.getBeaconBeam(BeaconBlockEntityRenderer.BEAM_TEXTURE, true)),
                color = color,
                oldColor = color,
                height = height,
                offset = 0.0f,
                x1 = -glowRadius,
                z1 = -glowRadius,
                x2 = glowRadius,
                z2 = -glowRadius,
                x3 = -glowRadius,
                z3 = glowRadius,
                x4 = glowRadius,
                z4 = glowRadius,
                u1 = 0.0f,
                u2 = 1.0f,
                v1 = height + v2,
                v2 = v2
            )
        }

        else -> {
            renderPart(
                matrixStackIn = matrixStack,
                bufferIn = bufferIn.getBuffer(RenderLayer.getBeaconBeam(BeaconBlockEntityRenderer.BEAM_TEXTURE, true)),
                color = color,
                oldColor = color,
                height = height - blendPadding,
                offset = blendLength,
                x1 = -glowRadius,
                z1 = -glowRadius,
                x2 = glowRadius,
                z2 = -glowRadius,
                x3 = -glowRadius,
                z3 = glowRadius,
                x4 = glowRadius,
                z4 = glowRadius,
                u1 = 0.0f,
                u2 = 1.0f,
                v1 = (height - blendLength) + v2,
                v2 = v2
            )
            renderPart(
                matrixStackIn = matrixStack,
                bufferIn = bufferIn.getBuffer(RenderLayer.getBeaconBeam(BeaconBlockEntityRenderer.BEAM_TEXTURE, true)),
                color = color,
                oldColor = oldColor,
                height = blendLength,
                offset = 0.0f,
                x1 = -glowRadius,
                z1 = -glowRadius,
                x2 = glowRadius,
                z2 = -glowRadius,
                x3 = -glowRadius,
                z3 = glowRadius,
                x4 = glowRadius,
                z4 = glowRadius,
                u1 = 0.0f,
                u2 = 1.0f,
                v1 = blendLength + v2,
                v2 = v2
            )
        }
    }

    matrixStack.pop()
}

private fun renderPart(
    matrixStackIn: MatrixStack,
    bufferIn: VertexConsumer,
    color: RGB,
    oldColor: RGB,
    height: Float,
    offset: Float,
    x1: Float,
    z1: Float,
    x2: Float,
    z2: Float,
    x3: Float,
    z3: Float,
    x4: Float,
    z4: Float,
    u1: Float,
    u2: Float,
    v1: Float,
    v2: Float,
) {
    val pose = matrixStackIn.peek()
    val matrix4f: Matrix4f = pose.positionMatrix
    val matrix3f: Matrix3f = pose.normalMatrix
    addQuad(matrix4f, matrix3f, bufferIn, color, oldColor, offset, height, x1, z1, x2, z2, u1, u2, v1, v2)
    addQuad(matrix4f, matrix3f, bufferIn, color, oldColor, offset, height, x4, z4, x3, z3, u1, u2, v1, v2)
    addQuad(matrix4f, matrix3f, bufferIn, color, oldColor, offset, height, x2, z2, x4, z4, u1, u2, v1, v2)
    addQuad(matrix4f, matrix3f, bufferIn, color, oldColor, offset, height, x3, z3, x1, z1, u1, u2, v1, v2)
}

private fun addQuad(
    matrixPos: Matrix4f,
    matrixNormal: Matrix3f,
    bufferIn: VertexConsumer,
    color: RGB,
    oldColor: RGB,
    yMin: Float,
    yMax: Float,
    x1: Float,
    z1: Float,
    x2: Float,
    z2: Float,
    u1: Float,
    u2: Float,
    v1: Float,
    v2: Float,
) {
    addVertex(matrixPos, matrixNormal, bufferIn, color, yMax, x1, z1, u2, v1)
    addVertex(matrixPos, matrixNormal, bufferIn, oldColor, yMin, x1, z1, u2, v2)
    addVertex(matrixPos, matrixNormal, bufferIn, oldColor, yMin, x2, z2, u1, v2)
    addVertex(matrixPos, matrixNormal, bufferIn, color, yMax, x2, z2, u1, v1)
}

private fun addVertex(
    matrixPos: Matrix4f,
    matrixNormal: Matrix3f,
    bufferIn: VertexConsumer,
    color: RGB,
    y: Float,
    x: Float,
    z: Float,
    texU: Float,
    texV: Float,
) {
    bufferIn.vertex(matrixPos, x, y, z)
        .color(color.r, color.g, color.b, color.alpha)
        .texture(texU, texV)
        .overlay(OverlayTexture.DEFAULT_UV)
        .light(15728880)
        .normal(matrixNormal, 0.0f, 1.0f, 0.0f)
        .next()
}


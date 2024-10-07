package gay.solonovamax.beaconsoverhaul.block.beacon.render

import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.model.SRGB
import gay.solonovamax.beaconsoverhaul.block.beacon.OverhauledBeacon
import gay.solonovamax.beaconsoverhaul.block.beacon.blockentity.BeaconBeamSegment
import gay.solonovamax.beaconsoverhaul.config.ConfigManager
import gay.solonovamax.beaconsoverhaul.util.color
import gay.solonovamax.beaconsoverhaul.util.not
import gay.solonovamax.beaconsoverhaul.util.scoped
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d

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
            ConfigManager.beaconConfig.beamRadius.toFloat(),
            ConfigManager.beaconConfig.beamGlowRadius.toFloat(),
            ConfigManager.beaconConfig.beamGlowOpacity.toFloat(),
            ConfigManager.beaconConfig.beamBlendPadding.toFloat(),
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

    val angle = Math.floorMod(totalWorldTime, 40L) + partialTicks
    val renderTime = -(totalWorldTime + partialTicks)
    val partAngle = MathHelper.fractionalPart(renderTime * 0.2f - MathHelper.floor(angle * 0.1f).toFloat())
    val v2 = -1.0f + partAngle

    matrixStack.scoped {
        matrixStack.translate(0.5, 0.5, 0.5)
        matrixStack.translate(offset.x, offset.y, offset.z) // offset by the correct distance
        matrixStack.multiply(segment.direction.rotationQuaternion)

        matrixStack.scoped {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle * 2.25f - 45.0f))

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
        }

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
                consumer = bufferIn.getBuffer(RenderLayer.getBeaconBeam(BeaconBlockEntityRenderer.BEAM_TEXTURE, color.alpha < 1f)),
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
                consumer = bufferIn.getBuffer(RenderLayer.getBeaconBeam(BeaconBlockEntityRenderer.BEAM_TEXTURE, true)),
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
                consumer = bufferIn.getBuffer(RenderLayer.getBeaconBeam(BeaconBlockEntityRenderer.BEAM_TEXTURE, color.alpha < 1f)),
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
                consumer = bufferIn.getBuffer(RenderLayer.getBeaconBeam(BeaconBlockEntityRenderer.BEAM_TEXTURE, color.alpha < 1f)),
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
                consumer = bufferIn.getBuffer(
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
}

private fun renderOuterGlow(
    segment: BeaconBeamSegment,
    height: Float,
    v2: Float,
    matrixStack: MatrixStack,
    consumerProvider: VertexConsumerProvider,
    color: RGB,
    oldColor: RGB,
    same: Boolean,
    blendLength: Float,
    blendPadding: Float,
    glowRadius: Float,
) {
    val consumer = consumerProvider.getBuffer(RenderLayer.getBeaconBeam(BeaconBlockEntityRenderer.BEAM_TEXTURE, true))

    when {
        segment.previousSegmentIsTurn -> {
            renderPart(
                matrixStackIn = matrixStack,
                consumer = consumer,
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
                consumer = consumer,
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
            renderStraightLowerBeaconBeam(matrixStack, consumer, color, color, height, glowRadius, v2)
        }

        else -> {
            renderPart(
                matrixStackIn = matrixStack,
                consumer = consumer,
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
            renderStraightLowerBeaconBeam(matrixStack, consumer, color, oldColor, blendLength, glowRadius, v2)
        }
    }
}

private fun renderStraightLowerBeaconBeam(
    matrixStack: MatrixStack,
    consumer: VertexConsumer,
    color: RGB,
    oldColor: RGB,
    height: Float,
    glowRadius: Float,
    v2: Float,
) {
    renderPart(
        matrixStackIn = matrixStack,
        consumer = consumer,
        color = color,
        oldColor = oldColor,
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

private fun renderPart(
    matrixStackIn: MatrixStack,
    consumer: VertexConsumer,
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
    addQuad(pose, consumer, color, oldColor, offset, height, x1, z1, x2, z2, u1, u2, v1, v2)
    addQuad(pose, consumer, color, oldColor, offset, height, x4, z4, x3, z3, u1, u2, v1, v2)
    addQuad(pose, consumer, color, oldColor, offset, height, x2, z2, x4, z4, u1, u2, v1, v2)
    addQuad(pose, consumer, color, oldColor, offset, height, x3, z3, x1, z1, u1, u2, v1, v2)
}

private fun addQuad(
    entry: MatrixStack.Entry,
    consumer: VertexConsumer,
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
    addVertex(entry, consumer, color, yMax, x1, z1, u2, v1)
    addVertex(entry, consumer, oldColor, yMin, x1, z1, u2, v2)
    addVertex(entry, consumer, oldColor, yMin, x2, z2, u1, v2)
    addVertex(entry, consumer, color, yMax, x2, z2, u1, v1)
}

private fun addVertex(
    entry: MatrixStack.Entry,
    consumer: VertexConsumer,
    color: RGB,
    y: Float,
    x: Float,
    z: Float,
    texU: Float,
    texV: Float,
) {
    consumer.vertex(entry, x, y, z)
        .color(color)
        .texture(texU, texV)
        .overlay(OverlayTexture.DEFAULT_UV)
        .light(15728880)
        .normal(entry, 0.0f, 1.0f, 0.0f)
}


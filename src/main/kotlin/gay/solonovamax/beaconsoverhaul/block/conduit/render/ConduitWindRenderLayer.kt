package gay.solonovamax.beaconsoverhaul.block.conduit.render

import gay.solonovamax.beaconsoverhaul.block.conduit.OverhauledConduitBlockEntity
import gay.solonovamax.beaconsoverhaul.util.pushPop
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.util.math.MatrixStack
import org.joml.Quaternionf
import software.bernie.geckolib.cache.`object`.GeoBone
import software.bernie.geckolib.renderer.layer.GeoRenderLayer

class ConduitWindRenderLayer(
    renderer: OverhauledConduitBlockEntityRenderer,
    ctx: BlockEntityRendererFactory.Context,
) : GeoRenderLayer<OverhauledConduitBlockEntity>(renderer) {
    private val conduitWindLayer = ctx.getLayerModelPart(EntityModelLayers.CONDUIT_WIND)
    private val model: ConduitBlockModel
        get() = renderer.geoModel as ConduitBlockModel

    override fun renderForBone(
        matrixStack: MatrixStack,
        conduit: OverhauledConduitBlockEntity,
        bone: GeoBone,
        renderLayer: RenderLayer,
        vertexConsumerSource: VertexConsumerProvider,
        vertexConsumer: VertexConsumer,
        partialTick: Float,
        light: Int,
        overlay: Int,
    ) {
        if (!conduit.isActive || !conduit.isWindActive || bone.name != "corners")
            return

        matrixStack.pushPop {
            matrixStack.translate(0.0, 0.5, 0.0) // Offset shit by 0.5 vertically bc god dammit

            val cycle = (conduit.ticksActive / 66) % 3
            val windSprite = model.getWindTexture(conduit)
            val consumer = windSprite.getVertexConsumer(vertexConsumerSource, RenderLayer::getEntityTranslucentEmissive)

            matrixStack.pushPop {
                when (cycle) {
                    1 -> matrixStack.multiply(Quaternionf().rotationX((Math.PI / 2).toFloat()))
                    2 -> matrixStack.multiply(Quaternionf().rotationZ((Math.PI / 2).toFloat()))
                }
                conduitWindLayer.render(matrixStack, consumer, light, overlay)
            }

            matrixStack.pushPop {
                matrixStack.scale(0.875f, 0.875f, 0.875f)
                matrixStack.multiply(Quaternionf().rotationXYZ(Math.PI.toFloat(), 0.0f, Math.PI.toFloat()))
                conduitWindLayer.render(matrixStack, consumer, light, overlay)
            }
        }

        // Reset the current render layer
        // yippee! I love side effects!
        vertexConsumerSource.getBuffer(renderLayer)
    }
}

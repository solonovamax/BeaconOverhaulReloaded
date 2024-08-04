package gay.solonovamax.beaconsoverhaul.block.conduit.render

import gay.solonovamax.beaconsoverhaul.block.conduit.OverhauledConduitBlockEntity
import gay.solonovamax.beaconsoverhaul.util.pushPop
import net.minecraft.client.render.Camera
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.util.math.MatrixStack
import org.joml.Quaternionf
import software.bernie.geckolib.cache.`object`.BakedGeoModel
import software.bernie.geckolib.cache.`object`.GeoBone
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer

class ConduitEyeRenderLayer(
    renderer: OverhauledConduitBlockEntityRenderer,
    ctx: BlockEntityRendererFactory.Context,
) : AutoGlowingGeoLayer<OverhauledConduitBlockEntity>(renderer) {
    private val conduitEyeLayer = ctx.getLayerModelPart(EntityModelLayers.CONDUIT_EYE)
    private val dispatcher = ctx.renderDispatcher
    private val camera: Camera
        get() = this.dispatcher.camera
    private val model: ConduitBlockModel
        get() = renderer.geoModel as ConduitBlockModel

    override fun render(
        poseStack: MatrixStack,
        conduit: OverhauledConduitBlockEntity,
        bakedModel: BakedGeoModel,
        renderType: RenderLayer,
        vertexSource: VertexConsumerProvider,
        vertexConsumer: VertexConsumer,
        partialTick: Float,
        light: Int,
        overlay: Int,
    ) {
    }

    override fun renderForBone(
        matrixStack: MatrixStack,
        conduit: OverhauledConduitBlockEntity,
        bone: GeoBone,
        renderLayer: RenderLayer,
        vertexSource: VertexConsumerProvider,
        vertexConsumer: VertexConsumer,
        partialTick: Float,
        light: Int,
        overlay: Int,
    ) {
        if (bone.name != "eye" || !conduit.isActive)
            return

        matrixStack.pushPop {
            matrixStack.translate(0.0, 0.5, 0.0) // Offset shit by 0.5 bc god dammit
            matrixStack.scale(0.5f, 0.5f, 0.5f)
            matrixStack.multiply(
                Quaternionf().rotationYXZ(
                    -camera.yaw * (Math.PI / 180.0).toFloat(),
                    camera.pitch * (Math.PI / 180.0).toFloat(),
                    Math.PI.toFloat()
                )
            )
            matrixStack.scale(4.0f / 3.0f, 4.0f / 3.0f, 4.0f / 3.0f)
            val eyeSprite = model.getEyeSprite(conduit)
            val consumer = eyeSprite.getVertexConsumer(vertexSource, RenderLayer::getEntityCutoutNoCull)

            conduitEyeLayer.render(matrixStack, consumer, light, overlay)

            if (conduit.isEyeOpen) {
                val emissiveRenderSprite = model.getEyeEmissiveSprite(conduit)
                val emissiveConsumer = emissiveRenderSprite.getVertexConsumer(vertexSource, RenderLayer::getEntityTranslucentEmissive)
                conduitEyeLayer.render(matrixStack, emissiveConsumer, 0xF00000, OverlayTexture.DEFAULT_UV)
            }
        }

        // Reset the current render layer
        // yippee! I love side effects!
        vertexSource.getBuffer(renderLayer)
    }

    override fun preRender(
        poseStack: MatrixStack?,
        animatable: OverhauledConduitBlockEntity?,
        bakedModel: BakedGeoModel?,
        renderType: RenderLayer?,
        bufferSource: VertexConsumerProvider?,
        buffer: VertexConsumer?,
        partialTick: Float,
        packedLight: Int,
        packedOverlay: Int,
    ) {
        super.preRender(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay)
    }
}

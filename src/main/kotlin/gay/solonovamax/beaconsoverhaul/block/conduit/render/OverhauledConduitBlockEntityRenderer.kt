package gay.solonovamax.beaconsoverhaul.block.conduit.render

import gay.solonovamax.beaconsoverhaul.block.conduit.OverhauledConduitBlockEntity
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import software.bernie.geckolib.renderer.GeoBlockRenderer

class OverhauledConduitBlockEntityRenderer(
    ctx: BlockEntityRendererFactory.Context,
) : GeoBlockRenderer<OverhauledConduitBlockEntity>(ConduitBlockModel()) {
    init {
        addRenderLayer(ConduitWindRenderLayer(this, ctx))
        addRenderLayer(ConduitEyeRenderLayer(this, ctx))
        // Can't get this working for whatever reason (pain)
        // addRenderLayer(AutoGlowingGeoLayer(this))
    }
}

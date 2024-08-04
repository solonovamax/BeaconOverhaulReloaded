package gay.solonovamax.beaconsoverhaul.mixin.client;

import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConduitBlockEntityRenderer.class)
public class ConduitBlockEntityRendererMixin {
    @Inject(
            method = "render(Lnet/minecraft/block/entity/ConduitBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At("HEAD")
    )
    private void cancelRender(ConduitBlockEntity conduit, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumer, int i,
                              int j, CallbackInfo ci) {
        throw new IllegalStateException(
                """
                ConduitBlockEntityRendererMixin should never be called.
                If it has been called, this indicates that there is most likely a compatibility issue with another mod.
                """
        );
    }
}

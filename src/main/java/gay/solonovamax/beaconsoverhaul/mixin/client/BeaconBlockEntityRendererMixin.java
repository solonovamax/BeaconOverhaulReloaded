package gay.solonovamax.beaconsoverhaul.mixin.client;

import gay.solonovamax.beaconsoverhaul.block.beacon.render.BeaconBlockEntityRendererKt;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconBlockEntityRenderer.class)
public class BeaconBlockEntityRendererMixin {
    @Inject(
            method = "render(Lnet/minecraft/block/entity/BeaconBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void render(final BeaconBlockEntity beaconBlockEntity, final float partialTicks, final MatrixStack matrixStack,
                        final VertexConsumerProvider bufferIn, final int combinedLightIn, final int combinedOverlayIn,
                        final CallbackInfo ci) {
        final boolean succeeded = BeaconBlockEntityRendererKt.render(beaconBlockEntity, partialTicks, matrixStack, bufferIn, combinedLightIn, combinedOverlayIn);

        if (succeeded)
            ci.cancel();
    }
}

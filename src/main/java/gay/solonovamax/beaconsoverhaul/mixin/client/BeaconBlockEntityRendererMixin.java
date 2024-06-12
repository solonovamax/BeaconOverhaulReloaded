package gay.solonovamax.beaconsoverhaul.mixin.client;

import gay.solonovamax.beaconsoverhaul.block.beacon.OverhauledBeacon;
import gay.solonovamax.beaconsoverhaul.block.beacon.render.BeaconBlockEntityRendererKt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BeaconBlockEntityRenderer.class)
public class BeaconBlockEntityRendererMixin {
    @Inject(
            method = "render(Lnet/minecraft/block/entity/BeaconBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void render(BeaconBlockEntity beacon, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider bufferIn,
                        int combinedLightIn, int combinedOverlayIn, CallbackInfo ci) {
        BeaconBlockEntityRendererKt.render((OverhauledBeacon) beacon, partialTicks, matrixStack, bufferIn, combinedLightIn, combinedOverlayIn);

        ci.cancel();
    }
}

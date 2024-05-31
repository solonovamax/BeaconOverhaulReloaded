package gay.solonovamax.beaconsoverhaul.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameOverlayRenderer.class)
class InGameOverlayRendererMixin {
    @Inject(method = "renderFireOverlay", at = @At("HEAD"), require = 1, allow = 1, cancellable = true)
    private static void omitFireOverlayIfResistant(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if ((client.player != null) && client.player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE))
            ci.cancel();
    }
}

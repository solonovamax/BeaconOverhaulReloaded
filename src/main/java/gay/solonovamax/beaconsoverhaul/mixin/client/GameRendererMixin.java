package gay.solonovamax.beaconsoverhaul.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
class GameRendererMixin {
    @Inject(
            method = "getNightVisionStrength",
            at = @At(shift = At.Shift.BY, by = -2, value = "CONSTANT", args = "intValue=200"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true,
            require = 1,
            allow = 1
    )
    private static void noNightVisionFlickerWhenAmbient(final LivingEntity entity, final float tickDelta,
                                                        final CallbackInfoReturnable<Float> cir, final StatusEffectInstance effect) {
        if (effect.isAmbient()) {
            cir.setReturnValue(1.0F);
        }
    }
}

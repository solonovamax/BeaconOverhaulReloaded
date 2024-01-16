package gay.solonovamax.beaconsoverhaul.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BackgroundRenderer.class)
@SuppressWarnings({"StaticVariableMayNotBeInitialized", "FieldNamingConvention"})
class BackgroundRendererMixin {
    @Shadow
    private static float red;

    @Shadow
    private static float green;

    @Shadow
    private static float blue;

    @Inject(
            method = "render",
            at = @At(target = "Lnet/minecraft/client/render/GameRenderer;getNightVisionStrength(Lnet/minecraft/entity/LivingEntity;F)F",
                    shift = At.Shift.BY, by = -4, value = "INVOKE", opcode = Opcodes.INVOKESTATIC),
            require = 1, allow = 1, cancellable = true)
    private static void skipNightVisionColorShift(final Camera camera, final float tickDelta, final ClientWorld world,
                                                  final int viewDistance, final float skyDarkness,
                                                  final CallbackInfo ci) {
        final Entity focusedEntity = camera.getFocusedEntity();

        if (focusedEntity instanceof final LivingEntity livingEntity) {
            final StatusEffectInstance nightVision = livingEntity.getStatusEffect(StatusEffects.NIGHT_VISION);

            if ((nightVision != null) && (nightVision.getAmplifier() > 0)) {
                // noinspection StaticVariableUsedBeforeInitialization
                RenderSystem.clearColor(BackgroundRendererMixin.red, BackgroundRendererMixin.green, BackgroundRendererMixin.blue, 0.0F);
                ci.cancel();
            }
        }
    }
}

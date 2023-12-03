package gay.solonovamax.beaconsoverhaul.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(LightmapTextureManager.class)
abstract class LightmapTextureManagerMixin {
    @Final
    @Shadow
    private MinecraftClient client;

    @ModifyVariable(
            method = "update",
            at = @At(
                    target = "Lorg/joml/Vector3f;<init>(Lorg/joml/Vector3fc;)V",
                    shift = Shift.BEFORE,
                    value = "INVOKE",
                    opcode = Opcodes.INVOKESPECIAL,
                    ordinal = 0,
                    remap = false
            ),
            index = 15,
            require = 1,
            allow = 1
    )
    private float fullBrightNightVision(final float skyLight) {
        final @Nullable ClientPlayerEntity player = this.client.player;

        if (player == null) {
            return skyLight;
        }

        final @Nullable StatusEffectInstance nightVision = player.getStatusEffect(StatusEffects.NIGHT_VISION);
        return ((nightVision != null) && (nightVision.getAmplifier() > 0)) ? 15.0F : skyLight;
    }
}

package gay.solonovamax.beaconsoverhaul.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import gay.solonovamax.beaconsoverhaul.register.StatusEffectRegistry;
import gay.solonovamax.beaconsoverhaul.render.PlacementTooltipRendererKt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
abstract class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyExpressionValue(
            method = "renderFood",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I",
                    opcode = Opcodes.INVOKEINTERFACE
            ),
            require = 1,
            allow = 1
    )
    private int noNutritionHungerShake(int original) {
        PlayerEntity player = this.getCameraPlayer();

        if ((player != null) && !player.getHungerManager().isNotFull()) {
            if (player.hasStatusEffect(StatusEffectRegistry.NUTRITION))
                return 1;
            else
                return 0;
        } else {
            return original;
        }
    }

    @Inject(
            method = "renderCrosshair",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;defaultBlendFunc()V",
                    shift = At.Shift.AFTER
            ),
            allow = 1,
            require = 1
    )
    private void renderCrosshairPlacementTooltip(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        PlacementTooltipRendererKt.renderTooltip(context, this.client);
    }

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();
}

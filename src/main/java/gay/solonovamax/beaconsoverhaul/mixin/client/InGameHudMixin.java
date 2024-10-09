package gay.solonovamax.beaconsoverhaul.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import gay.solonovamax.beaconsoverhaul.register.StatusEffectRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
abstract class InGameHudMixin {
    @ModifyExpressionValue(
            method = "renderFood",
            at = @At(
                    target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I",
                    value = "INVOKE",
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

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();
}

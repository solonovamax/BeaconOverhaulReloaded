package gay.solonovamax.beaconsoverhaul.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import gay.solonovamax.beaconsoverhaul.register.StatusEffectRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.Random;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
abstract class InGameHudMixin {
    @WrapWithCondition(
            method = "renderFood",
            at = @At(
                    target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I",
                    value = "INVOKE",
                    opcode = Opcodes.INVOKEINTERFACE
            ),
            require = 1,
            allow = 1
    )
    private boolean noNutritionHungerShake(Random instance, int i) {
        PlayerEntity player = this.getCameraPlayer();

        if ((player != null) && !player.getHungerManager().isNotFull()) {
            return player.hasStatusEffect(StatusEffectRegistry.NUTRITION);
        } else {
            return true;
        }
    }

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();
}

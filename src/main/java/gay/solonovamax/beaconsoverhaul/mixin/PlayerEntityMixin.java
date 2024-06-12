package gay.solonovamax.beaconsoverhaul.mixin;

import gay.solonovamax.beaconsoverhaul.register.StatusEffectRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntity {
    PlayerEntityMixin(EntityType<? extends LivingEntity> type, World level) {
        super(type, level);
    }

    @ModifyVariable(method = "canConsume", require = 1, allow = 1, argsOnly = true, at = @At("HEAD"))
    private boolean orHasNutritionEffect(boolean invulnerable) {
        return invulnerable || this.hasStatusEffect(StatusEffectRegistry.NUTRITION);
    }
}

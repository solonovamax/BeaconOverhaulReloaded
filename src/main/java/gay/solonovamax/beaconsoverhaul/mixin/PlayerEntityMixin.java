package gay.solonovamax.beaconsoverhaul.mixin;

import gay.solonovamax.beaconsoverhaul.registry.StatusEffectRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntity {
    PlayerEntityMixin(final EntityType<? extends LivingEntity> type, final World level) {
        super(type, level);
    }

    @ModifyVariable(method = "canConsume", require = 1, allow = 1, argsOnly = true, at = @At("HEAD"))
    private boolean orHasNutritionEffect(final boolean invulnerable) {
        return invulnerable || this.hasStatusEffect(StatusEffectRegistry.NUTRITION);
    }
}

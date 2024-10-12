package gay.solonovamax.beaconsoverhaul.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import gay.solonovamax.beaconsoverhaul.player.PlayerWithSculkActivationRange;
import gay.solonovamax.beaconsoverhaul.register.EntityAttributeRegistry;
import gay.solonovamax.beaconsoverhaul.register.StatusEffectRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntity implements PlayerWithSculkActivationRange {
    PlayerEntityMixin(EntityType<? extends LivingEntity> type, World level) {
        super(type, level);
    }

    @ModifyReturnValue(
            method = "createPlayerAttributes",
            at = @At("RETURN")
    )
    private static DefaultAttributeContainer.Builder addSculkActivationRangeAttribute(DefaultAttributeContainer.Builder original) {
        return original.add(EntityAttributeRegistry.SCULK_DETECTION_RANGE_MULTIPLIER);
    }

    @ModifyVariable(
            method = "canConsume",
            require = 1,
            allow = 1,
            argsOnly = true,
            at = @At("HEAD")
    )
    private boolean orHasNutritionEffect(boolean invulnerable) {
        return invulnerable || this.hasStatusEffect(StatusEffectRegistry.NUTRITION);
    }

    @Unique
    @Override
    public double sculkActivationRange(double defaultRange) {
        return defaultRange * this.getAttributeValue(EntityAttributeRegistry.SCULK_DETECTION_RANGE_MULTIPLIER);
    }
}

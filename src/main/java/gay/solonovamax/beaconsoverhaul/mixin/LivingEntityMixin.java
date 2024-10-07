package gay.solonovamax.beaconsoverhaul.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
    LivingEntityMixin(EntityType<?> type, World level) {
        super(type, level);
    }

    @Shadow
    public abstract AttributeContainer getAttributes();

    @Shadow
    protected abstract void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect, @Nullable Entity source);

    @ModifyReturnValue(method = "getStepHeight", at = @At("RETURN"))
    private float jumpBoostStepAssist(float original) {
        if (hasStatusEffect(StatusEffects.JUMP_BOOST))
            if (isSneaking())
                return 1.0f;
            else
                return original + 1.0f;
        else
            return original;
    }

    /**
     * Don't damage tick when the health boost effect is being upgraded/re-applied.
     */
    @Redirect(
            method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;onStatusEffectUpgraded(Lnet/minecraft/entity/effect/StatusEffectInstance;ZLnet/minecraft/entity/Entity;)V"
            )
    )
    private void fixHealthBoostDamageTick(LivingEntity instance, StatusEffectInstance effect, boolean reapplyEffect, Entity source) {
        if (!this.getWorld().isClient) {
            RegistryEntry<StatusEffect> statusEffect = effect.getEffectType();
            if (statusEffect != StatusEffects.HEALTH_BOOST)
                statusEffect.value().onRemoved(this.getAttributes());
            statusEffect.value().onApplied(this.getAttributes(), effect.getAmplifier());
        }

        this.onStatusEffectUpgraded(effect, false, source);
    }

    @ModifyExpressionValue(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/registry/entry/RegistryEntry;)Z",
                    ordinal = 0
            ),
            require = 1,
            allow = 1
    )
    private boolean dropIfCrouching(boolean original) {
        return original && this.isSneaking();
    }

    @Shadow
    public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);
}

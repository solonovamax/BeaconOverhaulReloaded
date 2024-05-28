package gay.solonovamax.beaconsoverhaul.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
    @Shadow
    private boolean effectsChanged;

    @Shadow
    @Final
    private Map<StatusEffect, StatusEffectInstance> activeStatusEffects;

    LivingEntityMixin(final EntityType<?> type, final World level) {
        super(type, level);
    }

    @Shadow
    public abstract AttributeContainer getAttributes();
    @Unique
    private float previousStepHeight = 0.0f;
    @Unique
    private boolean stepIncreased = false;

    @Shadow
    protected abstract void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect, @Nullable Entity source);

    @Inject(method = "tickStatusEffects", at = @At("HEAD"), require = 1)
    private void updateJumpBoostStepAssist(final CallbackInfo ci) {
        if (this.hasStatusEffect(StatusEffects.JUMP_BOOST) && !this.isSneaking()) {
            if (!this.stepIncreased) {
                this.previousStepHeight = this.getStepHeight();
                this.setStepHeight(1.0F);
                this.stepIncreased = true;
            }
        } else if (this.stepIncreased) {
            this.setStepHeight(this.previousStepHeight);
            this.stepIncreased = false;
        }
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
    private void statusEffectInstanceShit(final LivingEntity instance, final StatusEffectInstance effect, final boolean reapplyEffect,
                                          final Entity source) {
        if (!this.getWorld().isClient) {
            final StatusEffect statusEffect = effect.getEffectType();
            if (statusEffect != StatusEffects.HEALTH_BOOST)
                statusEffect.onRemoved((LivingEntity) (Object) this, this.getAttributes(), effect.getAmplifier());
            statusEffect.onApplied((LivingEntity) (Object) this, this.getAttributes(), effect.getAmplifier());
        }

        this.onStatusEffectUpgraded(effect, false, source);
    }

    @ModifyVariable(
            method = "travel",
            at = @At(
                    target = "Lnet/minecraft/world/entity/LivingEntity;resetFallDistance()V",
                    ordinal = 0,
                    shift = At.Shift.BY,
                    by = 2,
                    value = "CONSTANT",
                    args = "doubleValue=0.01"
            ),
            require = 1,
            allow = 1
    )
    private double dropIfCrouching(final double fallDelta) {
        return this.isSneaking() ? 0.08 : fallDelta;
    }

    @Shadow
    public abstract boolean hasStatusEffect(StatusEffect effect);
}

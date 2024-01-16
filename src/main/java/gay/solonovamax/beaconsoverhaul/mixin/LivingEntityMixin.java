package gay.solonovamax.beaconsoverhaul.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.World;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
    @Unique
    @MonotonicNonNull
    private Float baseUpStep;

    @Unique
    private boolean stepIncreased = false;

    LivingEntityMixin(EntityType<?> type, World level) {
        super(type, level);
    }

    @Inject(method = "baseTick()V", at = @At("HEAD"), require = 1)
    private void setBaseUpStep(CallbackInfo ci) {
        if (this.baseUpStep == null) {
            this.baseUpStep = this.getStepHeight();
        }
    }

    @Inject(method = "tickStatusEffects", at = @At("HEAD"), require = 1)
    private void updateJumpBoostStepAssist(CallbackInfo ci) {
        if (this.hasStatusEffect(StatusEffects.JUMP_BOOST) && !this.isSneaking()) {
            if (!this.stepIncreased) {
                this.setStepHeight(1.0F);
                this.stepIncreased = true;
            }
        } else if (this.stepIncreased) {
            this.setStepHeight(this.baseUpStep);
            this.stepIncreased = false;
        }
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
    private double dropIfCrouching(double fallDelta) {
        return this.isSneaking() ? 0.08 : fallDelta;
    }

    @Shadow
    public abstract float getStepHeight();

    @Shadow
    public abstract boolean hasStatusEffect(StatusEffect effect);
}

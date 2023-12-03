package gay.solonovamax.beaconsoverhaul.mixin.client;

import gay.solonovamax.beaconsoverhaul.client.BeaconPowerTooltips;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BeaconScreen.EffectButtonWidget.class)
abstract class EffectButtonWidgetMixin extends BeaconScreen.BaseButtonWidget {
    @Unique
    @SuppressWarnings({"ConstantConditions", "InstanceofThis"})
    private final boolean upgrade = (Object) this instanceof BeaconScreen.LevelTwoEffectButtonWidget;

    // @Final
    // @Shadow(aliases = "this$0")
    // private BeaconScreen this$0;

    @Shadow
    private StatusEffect effect;

    @Final
    @Shadow
    BeaconScreen field_2811;

    EffectButtonWidgetMixin(final int x, final int y) {
        super(x, y);
    }

    @Unique
    private void setTieredTooltip(final StatusEffect effect) {
        this.setTooltip(Tooltip.of(BeaconPowerTooltips.createTooltip(this.field_2811, effect, this.upgrade), null));
    }

    @Inject(method = "init", at = @At("RETURN"), require = 1, allow = 1)
    private void setTieredTooltip(final StatusEffect effect, final CallbackInfo ci) {
        this.setTieredTooltip(effect);
    }

    @Inject(method = "tick(I)V", at = @At("TAIL"), require = 1, allow = 1)
    private void updateTieredTooltip(final CallbackInfo ci) {
        if (!this.upgrade && this.effect != null) {
            this.setTieredTooltip(this.effect);
        }
    }
}

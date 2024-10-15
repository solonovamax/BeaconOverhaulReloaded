package gay.solonovamax.beaconsoverhaul.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import gay.solonovamax.beaconsoverhaul.player.PlayerWithSculkActivationRange;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WardenEntity.VibrationCallback.class)
public abstract class WardenEntityVibrationCallbackMixin {
    @Final
    @Shadow
    @SuppressWarnings("FieldNamingConvention")
    WardenEntity field_44600;

    @Shadow
    public abstract int getRange();

    @ModifyReturnValue(
            method = "accepts",
            at = @At("RETURN")
    )
    private boolean filterWithinRange(boolean original, ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event,
                                      GameEvent.Emitter emitter) {
        if (emitter.sourceEntity() instanceof PlayerEntity player) {
            double playerRange = ((PlayerWithSculkActivationRange) player).sculkActivationRange(getRange());
            return !(this.field_44600.getPos().squaredDistanceTo(player.getPos()) > playerRange * playerRange) && original;
        } else {

            return original;
        }
    }
}

package gay.solonovamax.beaconsoverhaul.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import gay.solonovamax.beaconsoverhaul.player.PlayerWithSculkActivationRange;
import gay.solonovamax.beaconsoverhaul.register.EntityAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.block.entity.SculkSensorBlockEntity$VibrationCallback")
public abstract class SculkBlockEntityVibrationCallbackMixin {
    @Final
    @Shadow
    protected BlockPos pos;

    @Shadow
    public abstract int getRange();

    @ModifyExpressionValue(
            method = "accept(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/SculkSensorBlockEntity$VibrationCallback;getRange()I"
            )
    )
    private int updateRangeWithAttribute(int original, ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event,
                                         @Nullable Entity sourceEntity, @Nullable Entity entity) {
        if (sourceEntity instanceof PlayerEntity player) {
            return (int) Math.floor(original * player.getAttributeValue(EntityAttributeRegistry.SCULK_DETECTION_RANGE_MULTIPLIER));
        } else {
            return original;
        }
    }

    @ModifyExpressionValue(
            method = "accepts(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/world/event/GameEvent$Emitter;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/SculkSensorBlock;isInactive(Lnet/minecraft/block/BlockState;)Z"
            )
    )
    private boolean filterWithinRange(boolean original, ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event,
                                      GameEvent.Emitter emitter) {
        if (emitter.sourceEntity() instanceof PlayerEntity player) {
            double playerRange = ((PlayerWithSculkActivationRange) player).sculkActivationRange(getRange());
            return !(this.pos.getSquaredDistance(player.getPos()) > playerRange * playerRange) && original;
        } else {

            return original;
        }
    }
}


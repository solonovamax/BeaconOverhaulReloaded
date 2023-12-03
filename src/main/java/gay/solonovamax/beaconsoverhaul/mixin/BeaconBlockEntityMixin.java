package gay.solonovamax.beaconsoverhaul.mixin;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import gay.solonovamax.beaconsoverhaul.MutableTieredBeacon;
import gay.solonovamax.beaconsoverhaul.PotencyTier;
import gay.solonovamax.beaconsoverhaul.TieredBeacon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;

@Mixin(BeaconBlockEntity.class)
abstract class BeaconBlockEntityMixin extends BlockEntity implements NamedScreenHandlerFactory, MutableTieredBeacon {
    @Unique
    private final Multiset<Block> baseBlocks = HashMultiset.create();
    @Shadow
    int level;
    @Unique
    private PotencyTier tier = PotencyTier.NONE;

    BeaconBlockEntityMixin(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
    }

    @Inject(
            method = "tick",
            at = @At(
                    target = "Lnet/minecraft/block/entity/BeaconBlockEntity;updateLevel(Lnet/minecraft/world/World;III)I",
                    shift = At.Shift.BY,
                    by = 2,
                    value = "INVOKE",
                    opcode = Opcodes.INVOKESTATIC
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            require = 1,
            allow = 1
    )
    private static void updateTier(final World world, final BlockPos pos, final BlockState state, final BeaconBlockEntity beacon,
                                   final CallbackInfo ci, final int x, final int y, final int z) {
        PotencyTier tier = PotencyTier.HIGH;
        int layerOffset = 1;

        layerCheck:
        while (layerOffset <= 4) {
            final int yOffset = y - layerOffset;

            if (yOffset < world.getBottomY()) {
                tier = PotencyTier.NONE;
                break;
            }

            for (int xOffset = x - layerOffset; xOffset <= (x + layerOffset); ++xOffset) {
                for (int zOffset = z - layerOffset; zOffset <= (z + layerOffset); ++zOffset) {
                    final BlockState stateAt = world.getBlockState(new BlockPos(xOffset, yOffset, zOffset));

                    if (!stateAt.isIn(BlockTags.BEACON_BASE_BLOCKS)) {
                        if (layerOffset == 1) {
                            tier = PotencyTier.NONE;
                        }

                        break layerCheck;
                    }

                    final PotencyTier tierAt;

                    if (stateAt.isIn(PotencyTier.HIGH_POTENCY_BLOCKS)) {
                        tierAt = PotencyTier.HIGH;
                    } else if (stateAt.isIn(PotencyTier.LOW_POTENCY_BLOCKS)) {
                        tierAt = PotencyTier.LOW;
                    } else {
                        tierAt = PotencyTier.NONE;
                    }

                    if (tierAt.ordinal() < tier.ordinal()) {
                        tier = tierAt;
                    }
                }
            }

            ++layerOffset;
        }

        ((MutableTieredBeacon) beacon).setTier(tier);
    }

    @ModifyVariable(
            method = "applyPlayerEffects",
            at = @At(value = "STORE", opcode = Opcodes.DSTORE, ordinal = 0),
            index = 5,
            require = 1,
            allow = 1
    )
    private static double modifyEffectRadius(final double radius, final World level, final BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof final TieredBeacon beacon) {
            return radius + (10.0 * beacon.getTier().ordinal());
        }

        return radius; // (levels * 10) + 10
    }

    @ModifyVariable(
            method = "applyPlayerEffects",
            at = @At(value = "STORE", opcode = Opcodes.ISTORE, ordinal = 0),
            index = 7, require = 1, allow = 1)
    private static int modifyPrimaryAmplifier(final int primaryAmplifier, final World level, final BlockPos pos, final int levels,
                                              final @Nullable StatusEffect primaryEffect) {
        if (primaryEffect != StatusEffects.NIGHT_VISION) {
            if (level.getBlockEntity(pos) instanceof final TieredBeacon beacon) {
                return beacon.getTier().ordinal();
            }
        }

        return primaryAmplifier; // 0
    }

    @ModifyVariable(
            method = "applyPlayerEffects",
            at = @At(value = "STORE", opcode = Opcodes.ISTORE, ordinal = 1),
            index = 7,
            require = 1,
            allow = 1
    )
    private static int modifyPotentPrimaryAmplifier(
            final int primaryAmplifier, final World level, final BlockPos pos, final int levels,
            final @Nullable StatusEffect primaryEffect, final @Nullable StatusEffect secondaryEffect) {
        if ((primaryEffect != StatusEffects.NIGHT_VISION)
                && (secondaryEffect != StatusEffects.SLOW_FALLING)
                && (secondaryEffect != StatusEffects.FIRE_RESISTANCE)) {
            if (level.getBlockEntity(pos) instanceof final TieredBeacon beacon) {
                return primaryAmplifier + beacon.getTier().ordinal();
            }
        }

        return primaryAmplifier; // 1
    }

    @ModifyVariable(
            method = "applyPlayerEffects",
            at = @At(value = "STORE", opcode = Opcodes.ISTORE, ordinal = 0),
            index = 8,
            require = 1,
            allow = 1
    )
    private static int modifyDuration(final int duration, final World level, final BlockPos pos, final int levels) {
        if (level.getBlockEntity(pos) instanceof final TieredBeacon beacon) {
            return ((9 * (beacon.getTier().ordinal() + 1)) + (levels * 2)) * 20;
        }

        return duration; // (9 + levels * 2) * 20
    }

    // Cannot use ModifyArg here as we need to capture the target method parameters
    @ModifyConstant(
            method = "applyPlayerEffects",
            constant = @Constant(/*intValue = 0,*/ ordinal = 1),
            require = 1,
            allow = 1
    )
    private static int modifySecondaryAmplifier(final int secondaryAmplifier, final World level, final BlockPos pos, final int levels,
                                                final @Nullable StatusEffect primaryEffect, final @Nullable StatusEffect secondaryEffect) {
        if ((secondaryEffect != StatusEffects.SLOW_FALLING)
                && (secondaryEffect != StatusEffects.FIRE_RESISTANCE)) {
            if (level.getBlockEntity(pos) instanceof final TieredBeacon beacon) {
                return beacon.getTier().ordinal();
            }
        }

        return secondaryAmplifier; // 0
    }

    @Unique
    @Override
    public final PotencyTier getTier() {
        return this.tier;
    }

    @Unique
    @Override
    public final void setTier(final PotencyTier tier) {
        this.tier = Objects.requireNonNull(tier);
    }

    @Mixin(targets = "net.minecraft.block.entity.BeaconBlockEntity$1")
    private abstract static class PropertyDelegateMixin implements PropertyDelegate {
        @Final
        @Shadow(aliases = {"this$0", "field_17379"})
        BeaconBlockEntity this$0;

        @Final
        @Shadow
        private BeaconBlockEntity field_17379;

        @Inject(method = "get", require = 1, at = @At("HEAD"), cancellable = true)
        private void tryGetTier(final int index, final CallbackInfoReturnable<Integer> cir) {
            if (index == 3) {
                cir.setReturnValue(((TieredBeacon) this.field_17379).getTier().ordinal());
            }
        }

        @Inject(method = "set", require = 1, at = @At("HEAD"), cancellable = true)
        private void trySetTier(final int index, final int value, final CallbackInfo ci) {
            if (index == 3) {
                ((MutableTieredBeacon) this.field_17379).setTier(PotencyTier.values()[value]);
                ci.cancel();
            }
        }

        @ModifyConstant(method = "size", constant = @Constant(intValue = 3), require = 1, allow = 1)
        private int expandDataCount(final int count) {
            System.out.println("In expand data count!!");
            return 3 + 1;
        }
    }
}

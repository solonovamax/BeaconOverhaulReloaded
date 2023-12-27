package gay.solonovamax.beaconsoverhaul.mixin;

import ca.solostudios.guava.kotlin.collect.MultisetsKt;
import ca.solostudios.guava.kotlin.collect.MutableMultiset;
import com.google.common.collect.HashMultiset;
import gay.solonovamax.beaconsoverhaul.MutableTieredBeacon;
import gay.solonovamax.beaconsoverhaul.OverhauledBeacon;
import gay.solonovamax.beaconsoverhaul.PotencyTier;
import gay.solonovamax.beaconsoverhaul.TieredBeacon;
import gay.solonovamax.beaconsoverhaul.util.BeaconBlockEntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;

@Mixin(BeaconBlockEntity.class)
abstract class BeaconBlockEntityMixin extends BlockEntity implements NamedScreenHandlerFactory, MutableTieredBeacon, OverhauledBeacon {
    @Unique
    private final MutableMultiset<Block> baseBlocks = MultisetsKt.toKotlin(HashMultiset.create());
    @Shadow
    private int level;
    @Unique
    private PotencyTier tier = PotencyTier.NONE;

    BeaconBlockEntityMixin(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
    }

    @Redirect(
            method = "tick",
            at = @At(target = "Lnet/minecraft/block/entity/BeaconBlockEntity;updateLevel(Lnet/minecraft/world/World;III)I", value = "INVOKE")
    )
    private static int updateLevel(final World world, final int x, final int y, final int z) {
        // no-op (we have our own implementation)
        return 0;
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
    private static void updateTier(final World world, final BlockPos pos, final BlockState beaconState, final BeaconBlockEntity beacon,
                                   final CallbackInfo ci, final int x, final int y, final int z) {
        BeaconBlockEntityUtil.updateTier(beacon, world, pos, x, y, z);
    }

    @ModifyVariable(
            method = "applyPlayerEffects",
            at = @At(value = "STORE", opcode = Opcodes.DSTORE, ordinal = 0),
            index = 5,
            require = 1,
            allow = 1
    )
    private static double modifyRange(final double radius, final World world, final BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof OverhauledBeacon beacon) {
            System.out.println("Is OverhauledBeacon");
            return BeaconBlockEntityUtil.computeRange(beacon);
        }
        System.out.println("Is not OverhauledBeacon");

        return radius; // this is the default radius computation
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

    @NotNull
    @Override
    public MutableMultiset<Block> getBaseBlocks() {
        return this.baseBlocks;
    }

    @Override
    public int getVanillaLevel() {
        return this.level;
    }

    @Override
    public void setVanillaLevel(final int level) {
        this.level = level;
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

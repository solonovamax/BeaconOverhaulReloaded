package gay.solonovamax.beaconsoverhaul.mixin;

import ca.solostudios.guava.kotlin.collect.MultisetsKt;
import ca.solostudios.guava.kotlin.collect.MutableMultiset;
import com.google.common.collect.HashMultiset;
import gay.solonovamax.beaconsoverhaul.BeaconOverhaulReloaded;
import gay.solonovamax.beaconsoverhaul.OverhauledBeacon;
import gay.solonovamax.beaconsoverhaul.beacon.OverhauledBeaconPropertyDelegate;
import gay.solonovamax.beaconsoverhaul.beacon.blockentity.BeaconBlockEntityKt;
import gay.solonovamax.beaconsoverhaul.beacon.screen.OverhauledBeaconScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
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

import java.util.List;

@SuppressWarnings("PackageVisibleField")
@Mixin(BeaconBlockEntity.class)
abstract class BeaconBlockEntityMixin extends BlockEntity implements ExtendedScreenHandlerFactory, OverhauledBeacon {
    @Unique
    private final MutableMultiset<Block> baseBlocks = MultisetsKt.toKotlin(HashMultiset.create());

    @Shadow
    int level;

    @Shadow
    @Nullable
    StatusEffect primary;

    @Shadow
    @Nullable
    StatusEffect secondary;

    @Shadow
    List<BeaconBlockEntity.BeamSegment> beamSegments;

    @Final
    @Shadow
    @Mutable
    private PropertyDelegate propertyDelegate;

    @Unique
    private double beaconPoints = 0.0;

    BeaconBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Redirect(method = "tick", at = @At(target = "Lnet/minecraft/block/entity/BeaconBlockEntity;updateLevel(Lnet/minecraft/world/World;III)I", value = "INVOKE"))
    private static int updateLevel(World world, int x, int y, int z) {
        // no-op (we have our own implementation)
        return 0;
    }

    @Inject(method = "tick", at = @At(target = "Lnet/minecraft/block/entity/BeaconBlockEntity;updateLevel(Lnet/minecraft/world/World;III)I", shift = At.Shift.BY, by = 2, value = "INVOKE", opcode = Opcodes.INVOKESTATIC), require = 1, allow = 1)
    private static void calculatePoints(World world, BlockPos pos, BlockState beaconState, BeaconBlockEntity beacon, CallbackInfo ci) {
        BeaconBlockEntityKt.updateTier(beacon, world, pos);
    }

    @ModifyVariable(method = "applyPlayerEffects", at = @At(value = "STORE", opcode = Opcodes.DSTORE, ordinal = 0), index = 5, require = 1, allow = 1)
    private static double modifyRange(double radius, World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof OverhauledBeacon beacon)
            return beacon.getRange();

        // Default case. Should never happen.
        // Add an exception here?
        return radius; // this is the default radius computation
    }

    @ModifyVariable(method = "applyPlayerEffects", at = @At(value = "STORE", opcode = Opcodes.ISTORE, ordinal = 0), index = 7, require = 1, allow = 1)
    private static int modifyPrimaryAmplifier(int primaryAmplifier, World level, BlockPos pos, int levels,
                                              @Nullable StatusEffect primaryEffect) {
        if (level.getBlockEntity(pos) instanceof OverhauledBeacon beacon) {
            if (!BeaconOverhaulReloaded.getConfig().getLevelOneStatusEffects().contains(primaryEffect))
                return beacon.getPrimaryAmplifier() - 1;
            else
                return 0; // 0 = level 1
        }

        // Default case. Should never happen.
        // Add an exception here?
        return primaryAmplifier; // 0
    }

    @ModifyVariable(method = "applyPlayerEffects", at = @At(value = "STORE", opcode = Opcodes.ISTORE, ordinal = 1), index = 7, require = 1, allow = 1)
    private static int modifyPotentPrimaryAmplifier(int primaryAmplifier, World level, BlockPos pos, int levels,
                                                    @Nullable StatusEffect primaryEffect, @Nullable StatusEffect secondaryEffect) {
        if (level.getBlockEntity(pos) instanceof OverhauledBeacon beacon) {
            if (!BeaconOverhaulReloaded.getConfig().getLevelOneStatusEffects().contains(primaryEffect))
                return beacon.getPrimaryAmplifierPotent() - 1;
            else
                return 0;
        }

        // Default case. Should never happen.
        // Add an exception here?
        return primaryAmplifier; // 1
    }

    // Cannot use ModifyArg here as we need to capture the target method parameters
    @ModifyConstant(method = "applyPlayerEffects", constant = @Constant(/*intValue = 0,*/ ordinal = 1), require = 1, allow = 1)
    private static int modifySecondaryAmplifier(int secondaryAmplifier, World level, BlockPos pos, int levels,
                                                @Nullable StatusEffect primaryEffect, @Nullable StatusEffect secondaryEffect) {
        if (level.getBlockEntity(pos) instanceof OverhauledBeacon beacon) {
            if (!BeaconOverhaulReloaded.getConfig().getLevelOneStatusEffects().contains(secondaryEffect))
                return beacon.getSecondaryAmplifier() - 1;
            else
                return 0;
        }

        // Default case. Should never happen.
        // Add an exception here?
        return secondaryAmplifier; // 0
    }

    @ModifyVariable(method = "applyPlayerEffects", at = @At(value = "STORE", opcode = Opcodes.ISTORE, ordinal = 0), index = 8, require = 1, allow = 1)
    private static int modifyDuration(int duration, World level, BlockPos pos, int levels) {
        if (level.getBlockEntity(pos) instanceof OverhauledBeacon beacon)
            return beacon.getDuration();

        // Default case. Should never happen.
        // Add an exception here?
        return duration; // (9 + levels * 2) * 20
    }

    @Inject(method = "<init>(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        this.propertyDelegate = new OverhauledBeaconPropertyDelegate(this);
    }

    @Inject(method = "createMenu", at = @At(value = "NEW", target = "(ILnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;Lnet/minecraft/screen/ScreenHandlerContext;)Lnet/minecraft/screen/BeaconScreenHandler;"), cancellable = true)
    private void createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity, CallbackInfoReturnable<ScreenHandler> cir) {
        ScreenHandlerContext context = ScreenHandlerContext.create(this.world, this.pos);
        cir.setReturnValue(new OverhauledBeaconScreenHandler(i, playerInventory, this.propertyDelegate, context));
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        BeaconBlockEntityKt.writeScreenOpeningData(this, player, buf);
    }

    @Unique
    @NotNull
    @Override
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public MutableMultiset<Block> getBaseBlocks() {
        return this.baseBlocks;
    }

    @Unique
    @Override
    public int getLevel() {
        return this.level;
    }

    @Unique
    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Unique
    @Override
    public double getBeaconPoints() {
        return this.beaconPoints;
    }

    @Unique
    @Override
    public void setBeaconPoints(double beaconPoints) {
        this.beaconPoints = beaconPoints;
    }

    @Unique
    @Override
    public int getRange() {
        return BeaconOverhaulReloaded.getConfig().calculateRange(this.beaconPoints);
    }

    @Unique
    @Override
    public int getDuration() {
        return BeaconOverhaulReloaded.getConfig().calculateDuration(this.beaconPoints);
    }

    @Unique
    @Nullable
    @Override
    @SuppressWarnings("SuspiciousGetterSetter")
    public StatusEffect getPrimaryEffect() {
        return this.primary;
    }

    @Unique
    @Override
    @SuppressWarnings("SuspiciousGetterSetter")
    public void setPrimaryEffect(@Nullable StatusEffect statusEffect) {
        this.primary = statusEffect;
    }

    @Unique
    @Nullable
    @Override
    @SuppressWarnings("SuspiciousGetterSetter")
    public StatusEffect getSecondaryEffect() {
        return this.secondary;
    }

    @Unique
    @Override
    @SuppressWarnings("SuspiciousGetterSetter")
    public void setSecondaryEffect(@Nullable StatusEffect statusEffect) {
        this.secondary = statusEffect;
    }

    @Unique
    @Nullable
    @Override
    public World getWorld() {
        return this.world;
    }

    @Unique
    @Override
    @NotNull
    public BlockPos getPos() {
        return this.pos;
    }

    @NotNull
    @Override
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public List<BeaconBlockEntity.BeamSegment> getBeamSegments() {
        return this.beamSegments;
    }

    @Override
    public int getPrimaryAmplifier() {
        return BeaconOverhaulReloaded.getConfig().calculatePrimaryAmplifier(this.beaconPoints, false);
    }

    @Override
    public int getPrimaryAmplifierPotent() {
        return BeaconOverhaulReloaded.getConfig().calculatePrimaryAmplifier(this.beaconPoints, true);
    }

    @Override
    public int getSecondaryAmplifier() {
        return BeaconOverhaulReloaded.getConfig().calculateSecondaryAmplifier(this.beaconPoints);
    }
}

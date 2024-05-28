package gay.solonovamax.beaconsoverhaul.mixin;

import ca.solostudios.guava.kotlin.collect.MultisetsKt;
import ca.solostudios.guava.kotlin.collect.MutableMultiset;
import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import gay.solonovamax.beaconsoverhaul.block.beacon.OverhauledBeacon;
import gay.solonovamax.beaconsoverhaul.block.beacon.OverhauledBeaconPropertyDelegate;
import gay.solonovamax.beaconsoverhaul.block.beacon.blockentity.OverhauledBeaconBlockEntityKt;
import gay.solonovamax.beaconsoverhaul.config.BeaconOverhaulConfigManager;
import kotlinx.datetime.Instant;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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

import java.util.Collections;
import java.util.List;

@SuppressWarnings({"PackageVisibleField", "CastToIncompatibleInterface"})
@Mixin(BeaconBlockEntity.class)
abstract class BeaconBlockEntityMixin extends BlockEntity implements ExtendedScreenHandlerFactory, OverhauledBeacon, InventoryProvider {
    @Unique
    @NotNull
    private final List<ServerPlayerEntity> listeningPlayers = Lists.newArrayList();

    @Shadow
    int level;

    @Shadow
    @Nullable
    StatusEffect primary;

    @Shadow
    @Nullable
    StatusEffect secondary;

    @Shadow
    @NotNull
    List<BeaconBlockEntity.BeamSegment> beamSegments;

    @Unique
    @NotNull
    private Instant lastUpdate = Instant.Companion.getDISTANT_PAST();

    @Unique
    @NotNull
    private MutableMultiset<Block> baseBlocks = MultisetsKt.mutableMultisetOf();

    @Final
    @Shadow
    @Mutable
    @NotNull
    private PropertyDelegate propertyDelegate;

    @Shadow
    private int minY;

    @Shadow
    private List<BeaconBlockEntity.BeamSegment> beamSegmentsToCheck;

    @Unique
    private double beaconPoints = 0.0;

    @Unique
    private boolean didRedirection = false;

    BeaconBlockEntityMixin(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
    }

    @Redirect(
            method = "tick",
            at = @At(
                    target = "Lnet/minecraft/block/entity/BeaconBlockEntity;updateLevel(Lnet/minecraft/world/World;III)I",
                    value = "INVOKE"
            )
    )
    private static int updateLevel(final World world, final int x, final int y, final int z) {
        final OverhauledBeacon beacon = (OverhauledBeacon) world.getBlockEntity(new BlockPos(x, y, z));
        assert beacon != null;

        return beacon.getLevel();
    }

    // This captures the for loop inside tick that computes the beacon segments
    @ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0))
    private static int constructBeamSegments(final int original, final World level, final BlockPos pos, final BlockState state,
                                             final BeaconBlockEntity beacon) {
        OverhauledBeaconBlockEntityKt.constructBeamSegments((OverhauledBeacon) beacon);

        return Integer.MAX_VALUE;
    }

    @Inject(
            method = "tick",
            at = @At(
                    target = "Lnet/minecraft/block/entity/BeaconBlockEntity;level:I",
                    shift = At.Shift.BY,
                    by = 2,
                    value = "FIELD",
                    opcode = Opcodes.INVOKESTATIC,
                    ordinal = 0
            )
    )
    private static void updateTier(final World world, final BlockPos pos, final BlockState beaconState, final BeaconBlockEntity beacon,
                                   final CallbackInfo ci) {
        OverhauledBeaconBlockEntityKt.updateTier((OverhauledBeacon) beacon, world, pos);
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getNonSpectatingEntities(Ljava/lang/Class;Lnet/minecraft/util/math/Box;)Ljava/util/List;"
            )
    )
    private static <T> List<T> disableAdvancementTrigger(final World instance, final Class<T> aClass, final Box box) {
        return List.of();
    }

    @ModifyVariable(
            method = "applyPlayerEffects",
            at = @At(value = "STORE", opcode = Opcodes.DSTORE, ordinal = 0),
            index = 5,
            require = 1,
            allow = 1
    )
    private static double modifyRange(final double radius, final World world, final BlockPos pos) {
        final OverhauledBeacon beacon = (OverhauledBeacon) world.getBlockEntity(pos);
        assert beacon != null;

        return beacon.getRange();
    }

    @ModifyVariable(
            method = "applyPlayerEffects",
            at = @At(value = "STORE", opcode = Opcodes.ISTORE, ordinal = 0),
            index = 7,
            require = 1,
            allow = 1
    )
    private static int modifyPrimaryAmplifier(final int primaryAmplifier, final World world, final BlockPos pos, final int levels,
                                              @Nullable final StatusEffect primaryEffect) {
        final OverhauledBeacon beacon = (OverhauledBeacon) world.getBlockEntity(pos);
        assert beacon != null;

        if (!BeaconOverhaulConfigManager.getConfig().getLevelOneStatusEffects().contains(primaryEffect))
            return beacon.getPrimaryAmplifier() - 1;
        else
            return 0; // 0 = level 1
    }

    @ModifyVariable(
            method = "applyPlayerEffects",
            at = @At(value = "STORE", opcode = Opcodes.ISTORE, ordinal = 1),
            index = 7,
            require = 1,
            allow = 1
    )
    private static int modifyPotentPrimaryAmplifier(final int primaryAmplifier, final World world, final BlockPos pos, final int levels,
                                                    @Nullable final StatusEffect primaryEffect,
                                                    @Nullable final StatusEffect secondaryEffect) {
        final OverhauledBeacon beacon = (OverhauledBeacon) world.getBlockEntity(pos);

        assert beacon != null;
        if (!BeaconOverhaulConfigManager.getConfig().getLevelOneStatusEffects().contains(primaryEffect))
            return beacon.getPrimaryAmplifierPotent() - 1;
        else
            return 0;

    }

    // Cannot use ModifyArg here as we need to capture the target method parameters
    @ModifyConstant(method = "applyPlayerEffects", constant = @Constant(/*intValue = 0,*/ ordinal = 1), require = 1, allow = 1)
    private static int modifySecondaryAmplifier(final int secondaryAmplifier, final World world, final BlockPos pos, final int levels,
                                                @Nullable final StatusEffect primaryEffect, @Nullable final StatusEffect secondaryEffect) {
        final OverhauledBeacon beacon = (OverhauledBeacon) world.getBlockEntity(pos);
        assert beacon != null;

        if (!BeaconOverhaulConfigManager.getConfig().getLevelOneStatusEffects().contains(secondaryEffect))
            return beacon.getSecondaryAmplifier() - 1;
        else
            return 0;
    }

    @ModifyVariable(
            method = "applyPlayerEffects",
            at = @At(value = "STORE", opcode = Opcodes.ISTORE, ordinal = 0),
            index = 8,
            require = 1,
            allow = 1
    )
    private static int modifyDuration(final int duration, final World world, final BlockPos pos, final int levels) {
        final OverhauledBeacon beacon = (OverhauledBeacon) world.getBlockEntity(pos);
        assert beacon != null;

        return beacon.getDuration();
    }

    @Redirect(
            method = "applyPlayerEffects",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/entity/effect/StatusEffect;IIZZ)Lnet/minecraft/entity/effect/StatusEffectInstance;"
            ),
            expect = 2
    )
    private static StatusEffectInstance disableEffectParticles(final StatusEffect type, final int duration, final int amplifier,
                                                               final boolean ambient,
                                                               final boolean visible) {
        return new StatusEffectInstance(type, duration, amplifier, ambient, BeaconOverhaulConfigManager.getConfig().getEffectParticles());
    }

    @Inject(method = "<init>(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at = @At("TAIL"))
    private void init(final CallbackInfo info) {
        this.propertyDelegate = new OverhauledBeaconPropertyDelegate(this);
    }

    @Inject(
            method = "createMenu",
            at = @At(
                    value = "NEW",
                    target = "(ILnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;Lnet/minecraft/screen/ScreenHandlerContext;)Lnet/minecraft/screen/BeaconScreenHandler;"
            ),
            cancellable = true
    )
    private void createMenu(final int syncId, final PlayerInventory playerInventory, final PlayerEntity playerEntity,
                            final CallbackInfoReturnable<ScreenHandler> cir) {
        cir.setReturnValue(OverhauledBeaconBlockEntityKt.createMenu(this, syncId, playerEntity));
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void readOverhauledNbt(final NbtCompound nbt, final CallbackInfo ci) {
        this.level = nbt.getInt("Levels");
        this.beaconPoints = nbt.getDouble("BeaconPoints");
        this.didRedirection = nbt.getBoolean("DidRedirection");

        final String primaryIdentifier = nbt.getString("Primary");
        if (!primaryIdentifier.isBlank())
            this.primary = Registries.STATUS_EFFECT.get(new Identifier(primaryIdentifier));

        final String secondaryIdentifier = nbt.getString("Secondary");
        if (!secondaryIdentifier.isBlank())
            this.secondary = Registries.STATUS_EFFECT.get(new Identifier(secondaryIdentifier));
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void writeOverhauledNbt(final NbtCompound nbt, final CallbackInfo ci) {
        nbt.putDouble("BeaconPoints", this.beaconPoints);
        nbt.putBoolean("DidRedirection", this.didRedirection);

        final Identifier primaryId = Registries.STATUS_EFFECT.getId(this.primary);
        if (this.primary != null && primaryId != null)
            nbt.putString("Primary", primaryId.toString());

        final Identifier secondaryId = Registries.STATUS_EFFECT.getId(this.secondary);
        if (this.secondary != null && secondaryId != null)
            nbt.putString("Secondary", secondaryId.toString());
    }

    @Unique
    @Override
    public void writeScreenOpeningData(final ServerPlayerEntity player, final PacketByteBuf buf) {
        OverhauledBeaconBlockEntityKt.writeScreenOpeningData(this, player, buf);
    }

    @Override
    public int getMinY() {
        return this.minY;
    }

    @Override
    public void setMinY(final int minY) {
        this.minY = minY;
    }

    @Unique
    @NotNull
    @Override
    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    @Unique
    @Override
    public void setLastUpdate(@NotNull final Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Unique
    @NotNull
    @Override
    public MutableMultiset<Block> getBaseBlocks() {
        return this.baseBlocks;
    }

    @Unique
    @Override
    public void setBaseBlocks(@NotNull final MutableMultiset<Block> baseBlocks) {
        this.baseBlocks = baseBlocks;
    }

    @Unique
    @Override
    public int getLevel() {
        return this.level;
    }

    @Unique
    @Override
    public void setLevel(final int level) {
        this.level = level;
    }

    @Unique
    @Override
    public double getBeaconPoints() {
        return this.beaconPoints;
    }

    @Unique
    @Override
    public void setBeaconPoints(final double beaconPoints) {
        this.beaconPoints = beaconPoints;
    }

    @Unique
    @NotNull
    @Override
    public OverhauledBeaconPropertyDelegate getPropertyDelegate() {
        return (OverhauledBeaconPropertyDelegate) this.propertyDelegate;
    }

    @Unique
    @Override
    public int getRange() {
        return BeaconOverhaulConfigManager.getConfig().calculateRange(this.beaconPoints);
    }

    @Unique
    @Override
    public int getDuration() {
        return BeaconOverhaulConfigManager.getConfig().calculateDuration(this.beaconPoints);
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
    public void setPrimaryEffect(@Nullable final StatusEffect primaryEffect) {
        this.primary = primaryEffect;
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
    public void setSecondaryEffect(@Nullable final StatusEffect secondaryEffect) {
        this.secondary = secondaryEffect;
    }

    @Unique
    @Nullable
    @Override
    public World getWorld() {
        return this.world;
    }

    @Unique
    @NotNull
    @Override
    public BlockPos getPos() {
        return this.pos;
    }

    @Unique
    @NotNull
    @Override
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public List<BeaconBlockEntity.BeamSegment> getBeamSegments() {
        return this.beamSegments;
    }

    @Unique
    @NotNull
    @Override
    public List<BeaconBlockEntity.BeamSegment> getBeamSegmentsToCheck() {
        return this.beamSegmentsToCheck;
    }

    @Unique
    @Override
    @SuppressWarnings("unchecked")
    public void setBeamSegmentsToCheck(@NotNull final List<? extends BeaconBlockEntity.BeamSegment> beamSegmentsToCheck) {
        this.beamSegmentsToCheck = (List<BeaconBlockEntity.BeamSegment>) beamSegmentsToCheck;
    }

    @Override
    public boolean getDidRedirection() {
        return this.didRedirection;
    }

    @Override
    public void setDidRedirection(final boolean didRedirection) {
        this.didRedirection = didRedirection;
    }

    @Unique
    @Override
    public int getPrimaryAmplifier() {
        return BeaconOverhaulConfigManager.getConfig().calculatePrimaryAmplifier(this.beaconPoints, false);
    }

    @Unique
    @Override
    public int getPrimaryAmplifierPotent() {
        return BeaconOverhaulConfigManager.getConfig().calculatePrimaryAmplifier(this.beaconPoints, true);
    }

    @Unique
    @Override
    public int getSecondaryAmplifier() {
        return BeaconOverhaulConfigManager.getConfig().calculateSecondaryAmplifier(this.beaconPoints);
    }

    @Unique
    @NotNull
    @Override
    public List<ServerPlayerEntity> getListeningPlayers() {
        return Collections.unmodifiableList(this.listeningPlayers);
    }

    @Unique
    @Override
    public void addUpdateListener(@NotNull final ServerPlayerEntity player) {
        this.listeningPlayers.add(player);
    }

    @Unique
    @Override
    public void removeUpdateListener(@NotNull final PlayerEntity player) {
        this.listeningPlayers.remove(player);
    }

    @Unique
    @Override
    public boolean canApplyEffect(@NotNull final StatusEffect effect) {
        return OverhauledBeaconBlockEntityKt.canApplyEffect(this, effect);
    }
}

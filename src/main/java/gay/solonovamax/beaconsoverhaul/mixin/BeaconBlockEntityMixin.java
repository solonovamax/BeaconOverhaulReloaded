package gay.solonovamax.beaconsoverhaul.mixin;

import ca.solostudios.guava.kotlin.collect.MultisetsKt;
import ca.solostudios.guava.kotlin.collect.MutableMultiset;
import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import gay.solonovamax.beaconsoverhaul.block.beacon.OverhauledBeacon;
import gay.solonovamax.beaconsoverhaul.block.beacon.OverhauledBeaconPropertyDelegate;
import gay.solonovamax.beaconsoverhaul.block.beacon.blockentity.BeaconBeamSegment;
import gay.solonovamax.beaconsoverhaul.block.beacon.blockentity.OverhauledBeaconBlockEntityKt;
import gay.solonovamax.beaconsoverhaul.config.ConfigManager;
import gay.solonovamax.beaconsoverhaul.util.JvmUtils;
import kotlinx.datetime.Instant;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
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
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Mixin(BeaconBlockEntity.class)
@Debug(export = true)
@SuppressWarnings("CastToIncompatibleInterface")
abstract class BeaconBlockEntityMixin extends BlockEntity implements ExtendedScreenHandlerFactory<byte[]>, OverhauledBeacon {
    @Unique
    @NotNull
    private final List<ServerPlayerEntity> listeningPlayers = Lists.newArrayList();

    @Shadow
    public int level;

    @Shadow
    @Nullable
    public RegistryEntry<StatusEffect> primary;

    @Shadow
    @Nullable
    public RegistryEntry<StatusEffect> secondary;

    @Shadow
    @NotNull
    public List<BeaconBlockEntity.BeamSegment> beamSegments;

    @Shadow
    public int minY;

    @Shadow
    @SuppressWarnings("FieldNamingConvention")
    private List<BeaconBlockEntity.BeamSegment> field_19178;

    @Unique
    private boolean brokenBeam = false;

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

    @Unique
    private double beaconPoints = 0.0;

    @Unique
    private boolean didRedirection = false;

    BeaconBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Redirect(
            method = "tick",
            at = @At(
                    target = "Lnet/minecraft/block/entity/BeaconBlockEntity;updateLevel(Lnet/minecraft/world/World;III)I",
                    value = "INVOKE"
            )
    )
    private static int updateLevel(World world, int x, int y, int z) {
        OverhauledBeacon beacon = (OverhauledBeacon) world.getBlockEntity(new BlockPos(x, y, z));
        assert beacon != null;

        return beacon.getLevel();
    }

    @Inject(
            method = "tick",
            at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0),
            require = 1,
            allow = 1
    )
    private static void constructBeamSegments(World world, BlockPos pos, BlockState state, BeaconBlockEntity beacon, CallbackInfo ci) {
        OverhauledBeaconBlockEntityKt.constructBeamSegments((OverhauledBeacon) beacon);
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0),
            require = 1,
            allow = 1
    )
    private static int disableDefaultBeamSegmentsMethod(int original, World world, BlockPos pos, BlockState state,
                                                        BeaconBlockEntity beacon) {
        return Integer.MAX_VALUE;
    }

    @Inject(
            method = "tick",
            at = @At(
                    target = "Lnet/minecraft/block/entity/BeaconBlockEntity;level:I",
                    value = "FIELD",
                    opcode = Opcodes.INVOKESTATIC
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;up()Lnet/minecraft/util/math/BlockPos;"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getTime()J")
            ),
            require = 1,
            allow = 1
    )
    private static void updateTier(World world, BlockPos pos, BlockState beaconState, BeaconBlockEntity beacon, CallbackInfo ci) {
        OverhauledBeaconBlockEntityKt.updateTier((OverhauledBeacon) beacon, world, pos);
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getNonSpectatingEntities(Ljava/lang/Class;Lnet/minecraft/util/math/Box;)Ljava/util/List;"
            ),
            require = 1,
            allow = 1
    )
    private static <T> List<T> disableAdvancementTrigger(World instance, Class<T> aClass, Box box) {
        return List.of();
    }

    @ModifyVariable(
            method = "applyPlayerEffects",
            at = @At(value = "STORE", ordinal = 0),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z"),
                    to = @At(value = "INVOKE", target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z")
            ),
            require = 1,
            allow = 1
    )
    private static double modifyRange(double radius, World world, BlockPos pos) {
        OverhauledBeacon beacon = (OverhauledBeacon) world.getBlockEntity(pos);
        assert beacon != null;

        return beacon.getRange();
    }

    @ModifyExpressionValue(
            method = "applyPlayerEffects",
            at = @At(value = "CONSTANT", args = "intValue=0"),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z"),
                    to = @At(value = "INVOKE", target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z")
            ),
            require = 1,
            allow = 1
    )
    private static int modifyPrimaryAmplifier(int originalAmplifier, World world, BlockPos pos, int levels,
                                              @Nullable RegistryEntry<StatusEffect> primaryEffect) {
        OverhauledBeacon beacon = (OverhauledBeacon) world.getBlockEntity(pos);
        assert beacon != null;

        if (!ConfigManager.getBeaconConfig().getLevelOneStatusEffects().contains(primaryEffect))
            return beacon.getPrimaryAmplifier() - 1;
        else
            return StatusEffectInstance.MIN_AMPLIFIER;
    }

    @ModifyExpressionValue(
            method = "applyPlayerEffects",
            at = @At(value = "CONSTANT", args = "intValue=1", ordinal = 0),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"),
                    to = @At(value = "CONSTANT", args = "intValue=20")
            ),
            require = 1,
            allow = 1
    )
    private static int modifyPotentPrimaryAmplifier(int originalAmplifier, World world, BlockPos pos, int levels,
                                                    @Nullable RegistryEntry<StatusEffect> primaryEffect,
                                                    @Nullable RegistryEntry<StatusEffect> secondaryEffect) {
        OverhauledBeacon beacon = (OverhauledBeacon) world.getBlockEntity(pos);

        assert beacon != null;
        if (!ConfigManager.getBeaconConfig().getLevelOneStatusEffects().contains(primaryEffect))
            return beacon.getPrimaryAmplifierPotent() - 1;
        else
            return StatusEffectInstance.MIN_AMPLIFIER;

    }

    @ModifyConstant(
            method = "applyPlayerEffects",
            constant = @Constant(intValue = 0),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/entity/player/PlayerEntity;addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)Z"
                    ),
                    to = @At("TAIL")
            ),
            require = 1,
            allow = 1
    )
    private static int modifySecondaryAmplifier(int originalAmplifier, World world, BlockPos pos, int levels,
                                                @Nullable RegistryEntry<StatusEffect> primaryEffect,
                                                @Nullable RegistryEntry<StatusEffect> secondaryEffect) {
        OverhauledBeacon beacon = (OverhauledBeacon) world.getBlockEntity(pos);
        assert beacon != null;

        if (!ConfigManager.getBeaconConfig().getLevelOneStatusEffects().contains(secondaryEffect))
            return beacon.getSecondaryAmplifier() - 1;
        else
            return StatusEffectInstance.MIN_AMPLIFIER;
    }

    @ModifyVariable(
            method = "applyPlayerEffects",
            at = @At(value = "STORE", opcode = Opcodes.ISTORE, ordinal = 0),
            index = 8,
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"),
                    to = @At(value = "NEW", target = "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/Box;")
            ),
            require = 1,
            allow = 1
    )
    private static int modifyDuration(int originalDuration, World world, BlockPos pos, int levels) {
        OverhauledBeacon beacon = (OverhauledBeacon) world.getBlockEntity(pos);
        assert beacon != null;

        return beacon.getDuration();
    }

    @Redirect(
            method = "applyPlayerEffects",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/registry/entry/RegistryEntry;IIZZ)Lnet/minecraft/entity/effect/StatusEffectInstance;"
            ),
            expect = 2,
            allow = 2
    )
    private static StatusEffectInstance disableEffectParticles(RegistryEntry<StatusEffect> effect, int duration, int amplifier,
                                                               boolean ambient, boolean visible) {
        return new StatusEffectInstance(effect, duration, amplifier, ambient, ConfigManager.getBeaconConfig().getEffectParticles());
    }

    @Inject(
            method = "<init>(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
            at = @At("TAIL")
    )
    private void init(CallbackInfo info) {
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
    private void createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity,
                            CallbackInfoReturnable<ScreenHandler> cir) {
        cir.setReturnValue(OverhauledBeaconBlockEntityKt.createMenu(this, syncId, playerEntity));
    }

    @Inject(
            method = "readNbt",
            at = @At("TAIL")
    )
    private void readOverhauledNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        this.level = nbt.getInt("levels");
        this.beaconPoints = nbt.getDouble("beacon_points");
        this.didRedirection = nbt.getBoolean("did_redirection");
    }

    @Inject(
            method = "writeNbt",
            at = @At("TAIL")
    )
    private void writeOverhauledNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        nbt.putDouble("beacon_points", this.beaconPoints);
        nbt.putBoolean("did_redirection", this.didRedirection);
    }

    @Unique
    @Override
    public byte[] getScreenOpeningData(ServerPlayerEntity player) {
        return OverhauledBeaconBlockEntityKt.screenOpeningData(this, player);
    }

    @Unique
    @NotNull
    @Override
    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    @Unique
    @Override
    public void setLastUpdate(@NotNull Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
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
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public void setBaseBlocks(@NotNull MutableMultiset<Block> baseBlocks) {
        this.baseBlocks = baseBlocks;
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
    @NotNull
    @Override
    public OverhauledBeaconPropertyDelegate getPropertyDelegate() {
        return (OverhauledBeaconPropertyDelegate) this.propertyDelegate;
    }

    @Unique
    @Override
    public int getRange() {
        return ConfigManager.getBeaconConfig().calculateRange(this.beaconPoints);
    }

    @Unique
    @Override
    public int getDuration() {
        return ConfigManager.getBeaconConfig().calculateDuration(this.beaconPoints);
    }

    @Unique
    @Nullable
    @Override
    public RegistryEntry<StatusEffect> getPrimaryEffect() {
        return this.primary;
    }

    @Unique
    @Override
    public void setPrimaryEffect(@Nullable RegistryEntry<StatusEffect> primaryEffect) {
        this.primary = primaryEffect;
    }

    @Unique
    @Nullable
    @Override
    public RegistryEntry<StatusEffect> getSecondaryEffect() {
        return this.secondary;
    }

    @Unique
    @Override
    public void setSecondaryEffect(@Nullable RegistryEntry<StatusEffect> secondaryEffect) {
        this.secondary = secondaryEffect;
    }

    @Unique
    @NotNull
    @Override
    public List<BeaconBeamSegment> getBeamSegments() {
        return JvmUtils.castUnchecked(this.beamSegments);
    }

    @Unique
    @NotNull
    @Override
    public List<BeaconBeamSegment> getBeamSegmentsToCheck() {
        return JvmUtils.castUnchecked(this.field_19178);
    }

    @Unique
    @Override
    public void setBeamSegmentsToCheck(@NotNull List<BeaconBeamSegment> beamSegmentsToCheck) {
        this.field_19178 = JvmUtils.castUnchecked(beamSegmentsToCheck);
    }

    @Override
    public boolean getBrokenBeam() {
        return this.brokenBeam;
    }

    @Override
    public void setBrokenBeam(boolean brokenBeam) {
        this.brokenBeam = brokenBeam;
    }

    @Override
    public int getMinY() {
        return this.minY;
    }

    @Override
    public void setMinY(int minY) {
        this.minY = minY;
    }

    @Override
    public boolean getDidRedirection() {
        return this.didRedirection;
    }

    @Override
    public void setDidRedirection(boolean didRedirection) {
        this.didRedirection = didRedirection;
    }

    @Unique
    @Override
    public int getPrimaryAmplifier() {
        return ConfigManager.getBeaconConfig().calculatePrimaryAmplifier(this.beaconPoints, false);
    }

    @Unique
    @Override
    public int getPrimaryAmplifierPotent() {
        return ConfigManager.getBeaconConfig().calculatePrimaryAmplifier(this.beaconPoints, true);
    }

    @Unique
    @Override
    public int getSecondaryAmplifier() {
        return ConfigManager.getBeaconConfig().calculateSecondaryAmplifier(this.beaconPoints);
    }

    @Unique
    @NotNull
    @Override
    public List<ServerPlayerEntity> getListeningPlayers() {
        return Collections.unmodifiableList(this.listeningPlayers);
    }

    @Unique
    @Override
    public void addUpdateListener(@NotNull ServerPlayerEntity player) {
        this.listeningPlayers.add(player);
    }

    @Unique
    @Override
    public void removeUpdateListener(@NotNull PlayerEntity player) {
        if (player instanceof ServerPlayerEntity)
            this.listeningPlayers.remove(player);
    }

    @Unique
    @Override
    public boolean canApplyEffect(@NotNull RegistryEntry<StatusEffect> effect) {
        return OverhauledBeaconBlockEntityKt.testCanApplyEffect(this, effect);
    }

    @Unique
    @NotNull
    @Override
    public World getWorld() {
        return Objects.requireNonNull(this.world);
    }

    @Unique
    @NotNull
    @Override
    public BlockPos getPos() {
        return this.pos;
    }
}

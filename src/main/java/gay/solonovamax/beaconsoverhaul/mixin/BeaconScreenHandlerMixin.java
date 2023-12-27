package gay.solonovamax.beaconsoverhaul.mixin;

import gay.solonovamax.beaconsoverhaul.PotencyTier;
import gay.solonovamax.beaconsoverhaul.TieredBeacon;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BeaconScreenHandler.class)
abstract class BeaconScreenHandlerMixin extends ScreenHandler implements TieredBeacon {
    @Final
    @Shadow
    private PropertyDelegate propertyDelegate;

    BeaconScreenHandlerMixin(@Nullable final ScreenHandlerType<?> type, final int id) {
        super(type, id);
    }

    @ModifyConstant(
            method = "<init>(ILnet/minecraft/inventory/Inventory;)V",
            constant = @Constant(intValue = 3),
            require = 1,
            allow = 1
    )
    private static int getNewDataCount(final int dataCount) {
        return 3 + 1;
    }

    @ModifyConstant(
            method = "<init>(ILnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;Lnet/minecraft/screen/ScreenHandlerContext;)V",
            constant = @Constant(intValue = 3, ordinal = 0),
            require = 1,
            allow = 1
    )
    private int getDataPreconditionCount(final int dataCount) {
        return 3 + 1;
    }

    @Override
    @Unique
    public final PotencyTier getTier() {
        // System.out.println("Here is the shit:", );
        return PotencyTier.values()[this.propertyDelegate.get(3)];
    }
}

package gay.solonovamax.beaconsoverhaul.mixin;

import gay.solonovamax.beaconsoverhaul.block.beacon.OverhauledBeacon;
import gay.solonovamax.beaconsoverhaul.block.conduit.OverhauledConduitBlockEntity;
import gay.solonovamax.beaconsoverhaul.integration.lavender.LavenderStructureTemplate;
import gay.solonovamax.beaconsoverhaul.integration.lavender.LavenderStructuresKt;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Shadow
    public abstract Block getBlock();

    @Inject(
            method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void placeNextMatchingStructureBlock(ItemPlacementContext placementContext, BlockState state,
                                                 CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = placementContext.getPlayer();
        if (player == null || !player.isSneaking())
            return;

        ItemUsageContextAccessor usageContextAccessor = (ItemUsageContextAccessor) placementContext;
        World world = placementContext.getWorld();
        BlockPos pos = usageContextAccessor.getHit().getBlockPos();
        BlockState targetBlockState = world.getBlockState(pos);
        Block targetBlock = targetBlockState.getBlock();

        if (targetBlock == Blocks.CONDUIT) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            BlockState placementState = getBlock().getPlacementState(placementContext);

            if (placementState == null)
                return;

            if (!(blockEntity instanceof OverhauledConduitBlockEntity conduit))
                return;

            int nextTier = conduit.getTier() + 1;
            LavenderStructureTemplate template = (LavenderStructureTemplate) OverhauledConduitBlockEntity.structureForTier(nextTier);

            if (template != null && LavenderStructuresKt.tryPlaceNextMatching(template, placementState, pos, world))
                cir.setReturnValue(true);
        } else if (targetBlock == Blocks.BEACON) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            BlockState placementState = getBlock().getPlacementState(placementContext);

            if (placementState != null && blockEntity instanceof OverhauledBeacon beacon && beacon.tryPlaceNextMatching(placementState))
                cir.setReturnValue(true);
        }
    }
}

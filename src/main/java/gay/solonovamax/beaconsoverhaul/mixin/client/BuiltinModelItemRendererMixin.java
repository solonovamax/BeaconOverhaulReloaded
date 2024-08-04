package gay.solonovamax.beaconsoverhaul.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import gay.solonovamax.beaconsoverhaul.block.conduit.OverhauledConduitBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BuiltinModelItemRenderer.class)
public class BuiltinModelItemRendererMixin {
    @Unique
    private final OverhauledConduitBlockEntity overhauledConduit = new OverhauledConduitBlockEntity(BlockPos.ORIGIN, Blocks.CONDUIT.getDefaultState());

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;renderEntity(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)Z"
            )
    )
    private BlockEntity replaceConduitRenderer(BlockEntity entity, @Local Block block, @Local LocalRef<BlockEntity> blockEntity) {
        if (entity instanceof ConduitBlockEntity)
            return this.overhauledConduit;
        else
            return entity;
    }
}

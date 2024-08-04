package gay.solonovamax.beaconsoverhaul.mixin.client;

import gay.solonovamax.beaconsoverhaul.block.conduit.OverhauledConduitBlockEntity;
import gay.solonovamax.beaconsoverhaul.block.conduit.render.OverhauledConduitBlockEntityRenderer;
import gay.solonovamax.beaconsoverhaul.util.JvmUtils;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(BlockEntityRendererFactories.class)
public abstract class BlockEntityRendererFactoriesMixin {
    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/entity/BlockEntityRendererFactories;register(Lnet/minecraft/block/entity/BlockEntityType;Lnet/minecraft/client/render/block/entity/BlockEntityRendererFactory;)V"
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/block/entity/BlockEntityType;CONDUIT:Lnet/minecraft/block/entity/BlockEntityType;"
                    ),
                    to = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/block/entity/BlockEntityType;BELL:Lnet/minecraft/block/entity/BlockEntityType;"
                    )
            ),
            require = 1,
            allow = 1
    )
    private static void replaceConduitRenderer(BlockEntityType<ConduitBlockEntity> type,
                                               BlockEntityRendererFactory<ConduitBlockEntity> factory) {
        BlockEntityRendererFactory<OverhauledConduitBlockEntity> rendererFactory = OverhauledConduitBlockEntityRenderer::new;
        BlockEntityRendererFactories.register(type, JvmUtils.castUnchecked(rendererFactory));
    }
}

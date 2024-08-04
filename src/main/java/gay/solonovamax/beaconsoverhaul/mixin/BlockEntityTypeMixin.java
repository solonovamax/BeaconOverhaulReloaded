package gay.solonovamax.beaconsoverhaul.mixin;

import gay.solonovamax.beaconsoverhaul.block.conduit.OverhauledConduitBlockEntity;
import gay.solonovamax.beaconsoverhaul.util.JvmUtils;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ConduitBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {
    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/BlockEntityType;create(Ljava/lang/String;Lnet/minecraft/block/entity/BlockEntityType$Builder;)Lnet/minecraft/block/entity/BlockEntityType;"
            ),
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=conduit"),
                    to = @At(value = "CONSTANT", args = "stringValue=barrel")
            ),
            require = 1,
            allow = 1
    )
    private static BlockEntityType<ConduitBlockEntity> overwriteConduitBlockEntityType(String id,
                                                                                       BlockEntityType.Builder<ConduitBlockEntity> builder) {
        var conduitBuilder = BlockEntityType.Builder.create(OverhauledConduitBlockEntity::new, Blocks.CONDUIT);
        return JvmUtils.castUnchecked(BlockEntityType.create("conduit", conduitBuilder));
    }
}

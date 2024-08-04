package gay.solonovamax.beaconsoverhaul.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import gay.solonovamax.beaconsoverhaul.register.BlockRegistry;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(BlockFamilies.class)
public class BlockFamiliesMixin {
    @ModifyExpressionValue(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/block/Blocks;PRISMARINE_BRICK_SLAB:Lnet/minecraft/block/Block;"
                    ),
                    to = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/data/family/BlockFamilies;PRISMARINE_BRICK:Lnet/minecraft/data/family/BlockFamily;"
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/data/family/BlockFamily$Builder;slab(Lnet/minecraft/block/Block;)Lnet/minecraft/data/family/BlockFamily$Builder;"
            ),
            require = 1,
            allow = 1
    )
    private static BlockFamily.Builder addPrismarineBrickWall(BlockFamily.Builder original) {
        return original.wall(BlockRegistry.PRISMARINE_BRICK_WALL);
    }


    @ModifyExpressionValue(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/block/Blocks;DARK_PRISMARINE_SLAB:Lnet/minecraft/block/Block;"
                    ),
                    to = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/data/family/BlockFamilies;DARK_PRISMARINE:Lnet/minecraft/data/family/BlockFamily;"
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/data/family/BlockFamily$Builder;slab(Lnet/minecraft/block/Block;)Lnet/minecraft/data/family/BlockFamily$Builder;"
            ),
            require = 1,
            allow = 1
    )
    private static BlockFamily.Builder addDarkPrismarineWall(BlockFamily.Builder original) {
        return original.wall(BlockRegistry.DARK_PRISMARINE_WALL);
    }
}

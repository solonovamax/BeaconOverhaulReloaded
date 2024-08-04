package gay.solonovamax.beaconsoverhaul.mixin;

import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemUsageContext.class)
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface ItemUsageContextAccessor {
    @Accessor
    BlockHitResult getHit();
}

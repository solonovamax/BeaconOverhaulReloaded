package gay.solonovamax.beaconsoverhaul.mixin;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityAccessor {
    @Mutable
    @Accessor("EFFECTS")
    static void setEffects(final Set<StatusEffect> value) {
        throw new UnsupportedOperationException();
    }
}

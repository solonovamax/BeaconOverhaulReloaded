package gay.solonovamax.beaconsoverhaul.mixin;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityAccessor {

    @Mutable
    @Accessor("EFFECTS_BY_LEVEL")
    static void setEffectsByLevel(final StatusEffect[][] effectsByLevel) {
        throw new UnsupportedOperationException();
    }

    @Mutable
    @Accessor("EFFECTS")
    static void setEffects(final Set<StatusEffect> value) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Invoker("getPotionEffectById")
    static StatusEffect getPotionEffectById(int id) {
        throw new UnsupportedOperationException();
    }
}

package gay.solonovamax.beaconsoverhaul.mixin;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.class)
public interface GameRulesAccessor {
    @Invoker("register")
    static <T extends GameRules.Rule<T>> GameRules.Key<T> register(final String name, final GameRules.Category category,
                                                                   final GameRules.Type<T> type) {
        throw new AssertionError();
    }
}

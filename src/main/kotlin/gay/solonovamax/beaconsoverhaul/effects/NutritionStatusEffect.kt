package gay.solonovamax.beaconsoverhaul.effects

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.player.PlayerEntity

class NutritionStatusEffect : StatusEffect(StatusEffectCategory.BENEFICIAL, 0xC75F79) {
    override fun applyUpdateEffect(entity: LivingEntity, amplifier: Int) {
        if (entity !is PlayerEntity)
            return

        entity.hungerManager.add(1, 0.0f)
    }

    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean {
        // what the hell is this ??
        return 100 shr amplifier <= 0 || duration % (100 shr amplifier) == 0
    }
}

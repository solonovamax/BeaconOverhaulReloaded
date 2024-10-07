package gay.solonovamax.beaconsoverhaul.effects

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.player.PlayerEntity

class NutritionStatusEffect : StatusEffect(StatusEffectCategory.BENEFICIAL, 0xC75F79) {
    override fun applyUpdateEffect(entity: LivingEntity, amplifier: Int): Boolean {
        if (entity !is PlayerEntity)
            return false

        entity.hungerManager.add(1, 0.0f)
        if (entity.hungerManager.saturationLevel <= amplifier)
            entity.hungerManager.saturationLevel += 1.0f

        return true
    }

    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean {
        // magic shit
        val i = 50 shr amplifier
        return if (i > 0)
            duration % i == 0
        else
            true
    }
}

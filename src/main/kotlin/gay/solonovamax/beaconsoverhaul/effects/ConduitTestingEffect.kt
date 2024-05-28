package gay.solonovamax.beaconsoverhaul.effects

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory

class ConduitTestingEffect : StatusEffect(StatusEffectCategory.BENEFICIAL, 0x00D4FF) {
    override fun applyUpdateEffect(entity: LivingEntity, amplifier: Int) {
    }

    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean {
        return false
        // magic shit
        val i = 50 shr amplifier
        return if (i > 0)
            duration % i == 0
        else
            true
    }
}

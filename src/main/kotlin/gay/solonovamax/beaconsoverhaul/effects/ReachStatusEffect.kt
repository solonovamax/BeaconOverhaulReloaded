package gay.solonovamax.beaconsoverhaul.effects

import gay.solonovamax.beaconsoverhaul.BeaconOverhaulReloaded
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.AttributeContainer
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import kotlin.math.max

class ReachStatusEffect : StatusEffect(StatusEffectCategory.BENEFICIAL, 0xDEF58F) {
    private fun getLongReachAmount(entity: LivingEntity, mul: Int): Double {
        return (max(
            0,
            entity.world.gameRules.getInt(BeaconOverhaulReloaded.LONG_REACH_INCREMENT)
        ) * (mul + 1)).toDouble()
    }

    override fun onApplied(entity: LivingEntity, attributes: AttributeContainer, amplifier: Int) {
        for ((key, modifier) in attributeModifiers) {
            val instance = attributes.getCustomInstance(key)
            if (instance != null) {
                instance.removeModifier(modifier)
                instance.addPersistentModifier(
                    EntityAttributeModifier(
                        modifier.id,
                        "$translationKey $amplifier",
                        getLongReachAmount(entity, amplifier),
                        modifier.operation
                    )
                )
            }
        }
    }
}

package gay.solonovamax.beaconsoverhaul.register

import gay.solonovamax.beaconsoverhaul.effects.NutritionStatusEffect
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import gay.solonovamax.beaconsoverhaul.util.registerReference
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.registry.Registries

object StatusEffectRegistry : CommonRegistration {
    val LONG_REACH = StatusEffect(StatusEffectCategory.BENEFICIAL, 0xF0F55F)
        .addAttributeModifier(
            EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE,
            identifierOf("effect.long_reach"),
            1.0,
            Operation.ADD_VALUE
        )
        .addAttributeModifier(
            EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE,
            identifierOf("effect.long_reach"),
            1.0,
            Operation.ADD_VALUE
        )
        .let { Registries.STATUS_EFFECT.registerReference(identifierOf("long_reach"), it) }

    val STEALTHY = StatusEffect(StatusEffectCategory.BENEFICIAL, 0x29DFEB)
        .addAttributeModifier(
            EntityAttributeRegistry.SCULK_DETECTION_RANGE_MULTIPLIER,
            identifierOf("effect.stealthy"),
            -0.125,
            Operation.ADD_MULTIPLIED_BASE
        )
        .let { Registries.STATUS_EFFECT.registerReference(identifierOf("stealthy"), it) }

    @JvmField
    val NUTRITION = NutritionStatusEffect().let { Registries.STATUS_EFFECT.registerReference(identifierOf("nutrition"), it) }

    override fun register() {}
}

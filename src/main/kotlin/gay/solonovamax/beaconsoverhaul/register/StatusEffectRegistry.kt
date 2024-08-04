package gay.solonovamax.beaconsoverhaul.register

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes
import gay.solonovamax.beaconsoverhaul.effects.NutritionStatusEffect
import gay.solonovamax.beaconsoverhaul.effects.ReachStatusEffect
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import gay.solonovamax.beaconsoverhaul.util.register
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.registry.Registries

object StatusEffectRegistry : CommonRegistration {
    @JvmField
    val LONG_REACH = ReachStatusEffect().also { reachEffect ->
        reachEffect.addAttributeModifier(
            ReachEntityAttributes.ATTACK_RANGE,
            "C764C44F-FC32-498B-98EB-B3262BA58B3B", Double.NaN, EntityAttributeModifier.Operation.ADDITION
        )
        reachEffect.addAttributeModifier(
            ReachEntityAttributes.REACH,
            "C20A0A8F-83DF-4C37-BC34-3678C24C3F01", Double.NaN, EntityAttributeModifier.Operation.ADDITION
        )
    }

    @JvmField
    val NUTRITION = NutritionStatusEffect()

    override fun register() {
        Registries.STATUS_EFFECT.register(identifierOf("long_reach"), LONG_REACH)
        Registries.STATUS_EFFECT.register(identifierOf("nutrition"), NUTRITION)
        // Registries.STATUS_EFFECT.register(identifierOf("conduit_testing_effect"), CONDUIT_TESTING_EFFECT)
    }
}

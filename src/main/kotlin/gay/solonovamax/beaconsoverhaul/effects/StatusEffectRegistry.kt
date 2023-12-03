package gay.solonovamax.beaconsoverhaul.effects

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes
import gay.solonovamax.beaconsoverhaul.BeaconConstants
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object StatusEffectRegistry {
    @JvmField
    val LONG_REACH = ReachStatusEffect().addAttributeModifier(
        ReachEntityAttributes.ATTACK_RANGE,
        "C764C44F-FC32-498B-98EB-B3262BA58B3B", Double.NaN, EntityAttributeModifier.Operation.ADDITION
    ).addAttributeModifier(
        ReachEntityAttributes.REACH,
        "C20A0A8F-83DF-4C37-BC34-3678C24C3F01", Double.NaN, EntityAttributeModifier.Operation.ADDITION
    )

    @JvmField
    val NUTRITION = Registry.register(Registries.STATUS_EFFECT, identifierOf(path = "nutrition"), NutritionStatusEffect())
    fun register() {
        Registry.register(Registries.STATUS_EFFECT, Identifier(BeaconConstants.NAMESPACE, "long_reach"), LONG_REACH)
        Registry.register(Registries.STATUS_EFFECT, Identifier(BeaconConstants.NAMESPACE, "nutrition"), NUTRITION)
    }
}

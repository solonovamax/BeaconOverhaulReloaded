package gay.solonovamax.beaconsoverhaul

import gay.solonovamax.beaconsoverhaul.effects.StatusEffectRegistry
import gay.solonovamax.beaconsoverhaul.effects.StatusEffectRegistry.LONG_REACH
import gay.solonovamax.beaconsoverhaul.effects.StatusEffectRegistry.NUTRITION
import gay.solonovamax.beaconsoverhaul.mixin.BeaconBlockEntityAccessor
import gay.solonovamax.beaconsoverhaul.mixin.GameRulesAccessor
import gay.solonovamax.beaconsoverhaul.mixin.IntRuleAccessor
import net.fabricmc.api.ModInitializer
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.world.GameRules

object BeaconStatusEffects : ModInitializer {
    val LONG_REACH_INCREMENT = GameRulesAccessor.register("longReachIncrement", GameRules.Category.PLAYER, IntRuleAccessor.create(2))

    private fun addStatusEffectsToBeacon() {
        val effects = BeaconBlockEntity.EFFECTS_BY_LEVEL
        effects[0] = arrayOf(*effects[0], StatusEffects.NIGHT_VISION)
        effects[1] = arrayOf(*effects[1], LONG_REACH)
        effects[2] = arrayOf(*effects[2], NUTRITION)
        effects[3] = arrayOf(*effects[3], StatusEffects.FIRE_RESISTANCE, StatusEffects.SLOW_FALLING)

        BeaconBlockEntityAccessor.setEffects(effects.flatMapTo(mutableSetOf()) { it.asIterable() })
    }

    override fun onInitialize() {
        addStatusEffectsToBeacon()

        StatusEffectRegistry.register()
    }
}

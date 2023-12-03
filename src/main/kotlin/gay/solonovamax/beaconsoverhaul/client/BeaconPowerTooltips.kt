package gay.solonovamax.beaconsoverhaul.client

import gay.solonovamax.beaconsoverhaul.TieredBeacon
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.ingame.BeaconScreen
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.text.MutableText
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
object BeaconPowerTooltips {
    private val EFFECT_SUFFIXES = arrayOf("II", "III", "IV")

    @JvmStatic
    fun createTooltip(screen: BeaconScreen, effect: StatusEffect, upgrade: Boolean): MutableText {
        val component = Text.translatable(effect.translationKey)

        if (effect != StatusEffects.SLOW_FALLING && effect != StatusEffects.FIRE_RESISTANCE) {
            var potency = if (upgrade) 1 else 0
            if (effect !== StatusEffects.NIGHT_VISION)
                potency += (screen.screenHandler as TieredBeacon).tier.ordinal

            if (potency > 0)
                return component.append(" ").append(EFFECT_SUFFIXES[potency - 1])
        }

        return component
    }
}

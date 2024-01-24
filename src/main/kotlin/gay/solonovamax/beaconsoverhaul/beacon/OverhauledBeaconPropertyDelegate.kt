package gay.solonovamax.beaconsoverhaul.beacon

import gay.solonovamax.beaconsoverhaul.mixin.BeaconBlockEntityAccessor
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.screen.PropertyDelegate
import net.minecraft.sound.SoundEvents
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.info

class OverhauledBeaconPropertyDelegate(
    private val overhauledBeacon: OverhauledBeacon,
) : PropertyDelegate {
    private val logger by getLogger()

    override fun get(index: Int): Int {
        return when (index) {
            0 -> overhauledBeacon.level
            1 -> StatusEffect.getRawIdNullable(overhauledBeacon.primaryEffect)
            2 -> StatusEffect.getRawIdNullable(overhauledBeacon.secondaryEffect)
            // 3 -> (overhauledBeacon as TieredBeacon).tier.ordinal
            else -> 0
        }
    }

    override fun set(index: Int, value: Int) {
        var updatedEffects = false
        when (index) {
            0 -> overhauledBeacon.level = value
            1 -> {
                val newEffect = BeaconBlockEntityAccessor.getPotionEffectById(value)
                when {
                    newEffect == null -> overhauledBeacon.primaryEffect = null
                    overhauledBeacon.canApplyEffect(newEffect) -> {
                        if (newEffect != overhauledBeacon.primaryEffect) {
                            updatedEffects = true
                            overhauledBeacon.primaryEffect = newEffect
                        } else {
                            logger.info { "Attempted to set inaccessible beacon effect at position ${overhauledBeacon.pos}" }
                        }
                    }
                }
            }

            2 -> {
                val newEffect = BeaconBlockEntityAccessor.getPotionEffectById(value)
                when {
                    newEffect == null -> overhauledBeacon.secondaryEffect = null
                    overhauledBeacon.canApplyEffect(newEffect) -> {
                        if (newEffect != overhauledBeacon.secondaryEffect) {
                            updatedEffects = true
                            overhauledBeacon.secondaryEffect = newEffect
                        } else {
                            logger.info { "Attempted to set inaccessible beacon effect at position ${overhauledBeacon.pos}" }
                        }
                    }
                }
            }
            // 3 -> (overhauledBeacon as MutableTieredBeacon).tier = PotencyTier.entries[value]
        }

        if (updatedEffects)
            if (!overhauledBeacon.world!!.isClient && overhauledBeacon.beamSegments.isNotEmpty())
                BeaconBlockEntity.playSound(overhauledBeacon.world, overhauledBeacon.pos, SoundEvents.BLOCK_BEACON_POWER_SELECT)
    }

    override fun size() = 3
}

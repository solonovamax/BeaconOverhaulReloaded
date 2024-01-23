package gay.solonovamax.beaconsoverhaul.beacon

import gay.solonovamax.beaconsoverhaul.mixin.BeaconBlockEntityAccessor
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.screen.PropertyDelegate
import net.minecraft.sound.SoundEvents

class OverhauledBeaconPropertyDelegate(
    private val overhauledBeacon: OverhauledBeacon,
) : PropertyDelegate {
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
                    overhauledBeacon.canApplyEffect(newEffect) && newEffect != overhauledBeacon.primaryEffect -> {
                        updatedEffects = true
                        overhauledBeacon.primaryEffect = newEffect
                    }
                }
            }

            2 -> {
                val newEffect = BeaconBlockEntityAccessor.getPotionEffectById(value)
                when {
                    newEffect == null -> overhauledBeacon.secondaryEffect = null
                    overhauledBeacon.canApplyEffect(newEffect) && newEffect != overhauledBeacon.secondaryEffect -> {
                        updatedEffects = true
                        overhauledBeacon.secondaryEffect = newEffect
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

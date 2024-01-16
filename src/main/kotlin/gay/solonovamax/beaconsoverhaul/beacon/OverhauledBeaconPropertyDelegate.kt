package gay.solonovamax.beaconsoverhaul.beacon

import gay.solonovamax.beaconsoverhaul.OverhauledBeacon
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
        when (index) {
            0 -> overhauledBeacon.level = value
            1 -> {
                if (!overhauledBeacon.world!!.isClient && overhauledBeacon.beamSegments.isNotEmpty()) {
                    BeaconBlockEntity.playSound(
                        overhauledBeacon.world,
                        overhauledBeacon.pos,
                        SoundEvents.BLOCK_BEACON_POWER_SELECT
                    )
                }

                overhauledBeacon.primaryEffect = BeaconBlockEntityAccessor.getPotionEffectById(value)
            }

            2 -> overhauledBeacon.secondaryEffect = BeaconBlockEntityAccessor.getPotionEffectById(value)
            // 3 -> (overhauledBeacon as MutableTieredBeacon).tier = PotencyTier.entries[value]
        }
    }

    override fun size(): Int {
        return 3
    }
}

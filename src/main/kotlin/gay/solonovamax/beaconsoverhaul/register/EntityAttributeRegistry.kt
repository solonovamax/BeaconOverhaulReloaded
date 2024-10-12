package gay.solonovamax.beaconsoverhaul.register

import gay.solonovamax.beaconsoverhaul.util.clampedEntityAttributeOf
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import gay.solonovamax.beaconsoverhaul.util.registerReference
import net.minecraft.registry.Registries

object EntityAttributeRegistry {
    @JvmField
    val SCULK_DETECTION_RANGE_MULTIPLIER = clampedEntityAttributeOf(
        translationKey = "attribute.name.player.sculk_detection_range_multiplier",
        fallback = 1.0,
        min = 0.0,
        max = 1.0,
        tracked = true
    ).let {
        Registries.ATTRIBUTE.registerReference(identifierOf("player.sculk_detection_range_multiplier"), it)
    }
}

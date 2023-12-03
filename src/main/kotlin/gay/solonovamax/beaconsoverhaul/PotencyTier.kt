package gay.solonovamax.beaconsoverhaul

import gay.solonovamax.beaconsoverhaul.util.identifierOf
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

enum class PotencyTier {
    NONE,
    LOW,
    HIGH;

    companion object {
        @JvmField
        val LOW_POTENCY_BLOCKS = TagKey.of(RegistryKeys.BLOCK, identifierOf(path = "low_potency"))

        @JvmField
        val HIGH_POTENCY_BLOCKS = TagKey.of(RegistryKeys.BLOCK, identifierOf(path = "high_potency"))
    }
}

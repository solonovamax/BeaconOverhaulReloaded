package gay.solonovamax.beaconsoverhaul.register

import gay.solonovamax.beaconsoverhaul.util.identifierOf
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

/**
 * Okay, technically tags aren't added to a registry, but whatever.
 */
object TagRegistry {
    val BEACON_TRANSPARENT = TagKey.of(RegistryKeys.BLOCK, identifierOf("beacon_transparent"))

    fun register() {}
}

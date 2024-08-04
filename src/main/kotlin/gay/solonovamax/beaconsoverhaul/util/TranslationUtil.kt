package gay.solonovamax.beaconsoverhaul.util

import gay.solonovamax.beaconsoverhaul.BeaconConstants.NAMESPACE

private val TIERED_STRUCTURE_REGEX = "^structure\\.$NAMESPACE\\.(.+)\\.tier_(\\d+)$".toRegex()

fun rewriteStructureTranslation(key: String, fallback: String, translations: Map<String, String>): String? {
    return if (key.matches(TIERED_STRUCTURE_REGEX)) {
        val match = TIERED_STRUCTURE_REGEX.matchEntire(key) ?: return null
        val (structureName, structureTier) = match.destructured
        val structureNameKey = "structure.$NAMESPACE.$structureName"

        return translations.getOrDefault(structureNameKey, fallback).format(structureTier)
    } else {
        null
    }
}

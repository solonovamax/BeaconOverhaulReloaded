package gay.solonovamax.beaconsoverhaul.register

import gay.solonovamax.beaconsoverhaul.integration.lavender.BeaconStructureFeature
import gay.solonovamax.beaconsoverhaul.integration.lavender.EntityModelFeature
import gay.solonovamax.beaconsoverhaul.integration.lavender.StructureFeature
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import io.wispforest.lavender.client.LavenderBookScreen

object LavenderRegistration {
    fun register() {
        LavenderBookScreen.registerFeatureFactory(identifierOf("guidebook")) { componentSource ->
            listOf(
                EntityModelFeature(componentSource),
                StructureFeature(componentSource),
                BeaconStructureFeature(componentSource),
            )
        }
    }
}

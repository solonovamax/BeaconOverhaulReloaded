package gay.solonovamax.beaconsoverhaul.registry

import gay.solonovamax.beaconsoverhaul.BeaconConstants.NAMESPACE
import gay.solonovamax.beaconsoverhaul.integration.lavender.BeaconOverhaulStructureComponent
import gay.solonovamax.beaconsoverhaul.integration.lavender.EntityModelComponent
import io.wispforest.owo.ui.parsing.UIParsing

object OwoUIRegistry {
    fun register() {
        // Game Objects
        UIParsing.registerFactory("$NAMESPACE.entity-model") { element -> EntityModelComponent.parse(element) }
        UIParsing.registerFactory("$NAMESPACE.structure") { element -> BeaconOverhaulStructureComponent.parse(element) }
    }
}

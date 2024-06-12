package gay.solonovamax.beaconsoverhaul.register

import gay.solonovamax.beaconsoverhaul.BeaconConstants.NAMESPACE
import gay.solonovamax.beaconsoverhaul.integration.lavender.BeaconStructureComponent
import gay.solonovamax.beaconsoverhaul.integration.lavender.EntityModelComponent
import gay.solonovamax.beaconsoverhaul.integration.lavender.StructureComponent
import io.wispforest.owo.ui.parsing.UIParsing

object OwoUIRegistration {
    fun register() {
        // Game Objects
        UIParsing.registerFactory("$NAMESPACE.entity-model") { element -> EntityModelComponent.parse(element) }
        UIParsing.registerFactory("$NAMESPACE.structure") { element -> StructureComponent.parse(element) }
        UIParsing.registerFactory("$NAMESPACE.beacon-structure") { element -> BeaconStructureComponent.parse(element) }
    }
}

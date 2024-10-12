package gay.solonovamax.beaconsoverhaul.register

import gay.solonovamax.beaconsoverhaul.integration.lavender.BeaconStructureComponent
import gay.solonovamax.beaconsoverhaul.integration.lavender.EntityModelComponent
import gay.solonovamax.beaconsoverhaul.integration.lavender.StructureComponent
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import io.wispforest.owo.ui.parsing.UIParsing

object OwoUIRegistration : ClientRegistration {
    override fun registerClient() {
        UIParsing.registerFactory(identifierOf("entity-model")) { element -> EntityModelComponent.parse(element) }
        UIParsing.registerFactory(identifierOf("structure")) { element -> StructureComponent.parse(element) }
        UIParsing.registerFactory(identifierOf("beacon-structure")) { element -> BeaconStructureComponent.parse(element) }
    }
}

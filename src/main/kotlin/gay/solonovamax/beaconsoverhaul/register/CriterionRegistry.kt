package gay.solonovamax.beaconsoverhaul.register

import gay.solonovamax.beaconsoverhaul.advancement.RedirectBeaconCriterion
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import gay.solonovamax.beaconsoverhaul.util.register
import net.minecraft.registry.Registries

object CriterionRegistry : CommonRegistration {
    val REDIRECT_BEACON_CRITERION = RedirectBeaconCriterion()

    override fun register() {
        Registries.CRITERION.register(identifierOf("redirect_beacon"), REDIRECT_BEACON_CRITERION)
    }
}

package gay.solonovamax.beaconsoverhaul.register

import gay.solonovamax.beaconsoverhaul.advancement.RedirectBeaconCriterion
import net.minecraft.advancement.criterion.Criteria

object CriterionRegistry {
    val REDIRECT_BEACON_CRITERION = RedirectBeaconCriterion

    fun register() {
        Criteria.register(REDIRECT_BEACON_CRITERION)
    }
}

package gay.solonovamax.beaconsoverhaul.registry

import gay.solonovamax.beaconsoverhaul.advancement.RedirectBeaconCriterion
import net.minecraft.advancement.criterion.Criteria

object CriterionRegistry {
    fun register() {
        Criteria.register(RedirectBeaconCriterion)
    }
}

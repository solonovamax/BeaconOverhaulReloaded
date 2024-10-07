package gay.solonovamax.beaconsoverhaul.advancement

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gay.solonovamax.beaconsoverhaul.register.CriterionRegistry
import gay.solonovamax.beaconsoverhaul.util.forKProperty
import net.minecraft.advancement.AdvancementCriterion
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterion.Conditions
import net.minecraft.predicate.NumberRange.IntRange
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import java.util.Optional

class RedirectBeaconCriterion : AbstractCriterion<RedirectBeaconCriterion.Conditions>() {
    fun trigger(player: ServerPlayerEntity) {
        trigger(player) { true }
    }

    override fun getConditionsCodec() = Conditions.CODEC

    data class Conditions(
        val player: Optional<LootContextPredicate>,
        val redirections: IntRange,
    ) : AbstractCriterion.Conditions {
        fun matches(redirections: Int): Boolean = this.redirections.test(redirections)

        override fun player() = player

        companion object {
            val CODEC: Codec<Conditions> = RecordCodecBuilder.create {
                it.group(
                    EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC
                        .forKProperty(Conditions::player),
                    IntRange.CODEC
                        .forKProperty(Conditions::redirections, IntRange.ANY)
                ).apply(it, ::Conditions)
            }

            fun create(redirections: IntRange = IntRange.ANY): AdvancementCriterion<Conditions> {
                return CriterionRegistry.REDIRECT_BEACON_CRITERION.create(Conditions(Optional.empty(), redirections))
            }
        }
    }
}

package gay.solonovamax.beaconsoverhaul.util

import net.minecraft.advancement.Advancement
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import java.util.function.Consumer

fun ServerPlayerEntity.grantAdvancement(id: Identifier) {
    val tracker = advancementTracker
    val advancement = server.advancementLoader[id]
    val progress = tracker.getProgress(advancement)

    progress.unobtainedCriteria.forEach { criterion ->
        tracker.grantCriterion(advancement, criterion)
    }
}

fun ServerPlayerEntity.revokeAdvancement(id: Identifier) {
    val tracker = advancementTracker
    val advancement = server.advancementLoader[id]
    val progress = tracker.getProgress(advancement)

    progress.obtainedCriteria.forEach { criterion ->
        tracker.revokeCriterion(advancement, criterion)
    }
}

fun ServerPlayerEntity.hasAdvancement(id: Identifier): Boolean {
    val tracker = advancementTracker
    val advancement = server.advancementLoader[id]
    val progress = tracker.getProgress(advancement)

    return progress.isDone
}

fun Advancement.Builder.build(exporter: Consumer<Advancement>, id: Identifier): Advancement {
    return build(id).apply { exporter.accept(this) }
}

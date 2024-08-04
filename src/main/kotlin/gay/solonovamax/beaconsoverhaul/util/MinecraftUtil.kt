@file:Suppress("FunctionName")

package gay.solonovamax.beaconsoverhaul.util

import com.mojang.datafixers.util.Either
import gay.solonovamax.beaconsoverhaul.BeaconConstants
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.entry.RegistryEntryList
import net.minecraft.registry.tag.TagKey
import net.minecraft.resource.featuretoggle.FeatureSet
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.util.TypeFilter
import net.minecraft.util.math.Box
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.World
import net.silkmc.silk.core.kotlin.asKotlinRandom
import java.util.function.Function
import kotlin.random.Random
import kotlin.streams.asSequence

fun identifierOf(identifier: String): Identifier {
    return if (identifier.contains(':'))
        identifier.split(':').let { (namespace, path) -> identifierOf(namespace, path) }
    else
        identifierOf(path = identifier)
}

fun identifierOf(namespace: String = BeaconConstants.NAMESPACE, path: String): Identifier = Identifier(namespace, path)

operator fun TagKey<Block>.contains(state: BlockState): Boolean {
    return state.isIn(this)
}

fun <V, T : V> Registry<V>.register(id: Identifier, entry: T): T = Registry.register(this, id, entry)

fun <T> Registry<in T>.register(id: String, entry: T): T = Registry.register(this, id, entry)

operator fun RegistryEntryList<Block>.contains(state: BlockState): Boolean {
    return state.isIn(this)
}

fun <T : ScreenHandler?> ScreenHandlerType(
    requiredFeatures: FeatureSet,
    factory: ScreenHandlerType.Factory<T>,
): ScreenHandlerType<T> = ScreenHandlerType(factory, requiredFeatures)

val Block.id: Identifier
    get() = Registries.BLOCK.getId(this)

fun PropertyDelegate(size: Int): ArrayPropertyDelegate = ArrayPropertyDelegate(size)

fun World.otherEntities(
    except: Entity?,
    box: Box,
    predicate: (Entity) -> Boolean = EntityPredicates.EXCEPT_SPECTATOR::test,
): List<Entity> {
    return getOtherEntities(except, box, predicate)
}

inline fun <reified T : Entity> World.entitiesByType(box: Box, noinline predicate: (T) -> Boolean): List<T> {
    return getEntitiesByType(TypeFilter.instanceOf(T::class.java), box, predicate)
}

inline fun <reified T : Entity> World.entitiesByClass(box: Box, noinline predicate: (T) -> Boolean): List<T> {
    return getEntitiesByClass(T::class.java, box, predicate)
}

inline fun <reified T : Entity> World.nonSpectatingEntities(box: Box): List<T> {
    return getNonSpectatingEntities(T::class.java, box)
}

fun World.entityCollisions(entity: Entity?, box: Box): List<VoxelShape> {
    return getEntityCollisions(entity, box)
}

val World.kotlinRandom: Random
    get() = random.asKotlinRandom()

fun <T, L : T, R : T> Either<L, R>.flatten(): T {
    return map(Function.identity(), Function.identity())
}

fun <T> Registry<T>.getEntryIdentifiersMatching(value: String): Sequence<Identifier> {
    val separator = value.indexOf(Identifier.NAMESPACE_SEPARATOR)
    val splitNamespace = separator.takeIf { it != -1 }?.let { value.substring(0, it).lowercase() }
    val path = separator.takeIf { it != -1 }?.let { value.substring(separator + 1).lowercase() } ?: value.lowercase()
    return this.streamEntries()
        .asSequence()
        .map { holder -> holder.registryKey().value }
        .filter { identifier ->
            if (splitNamespace == null) {
                path in identifier.path || path in identifier.namespace
            } else {
                identifier.namespace == splitNamespace && path in identifier.path
            }
        }
        .sortedWith { id1: Identifier, id2: Identifier ->
            val id1StartsWith = id1.path.lowercase().startsWith(path)
            val id2StartsWith = id2.path.lowercase().startsWith(path)
            return@sortedWith when {
                id1StartsWith && !id2StartsWith -> -1
                !id1StartsWith && id2StartsWith -> 1
                else -> id1.compareTo(id2)
            }
        }
}

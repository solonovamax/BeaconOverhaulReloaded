@file:Suppress("FunctionName")

package gay.solonovamax.beaconsoverhaul.util

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import gay.solonovamax.beaconsoverhaul.BeaconConstants
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.registry.Registries
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
import java.util.Optional
import java.util.function.Function
import kotlin.random.Random
import kotlin.reflect.KFunction1
import kotlin.reflect.KProperty1

fun identifierOf(identifier: String): Identifier {
    return if (identifier.contains(':'))
        identifier.split(':').let { (namespace, path) -> identifierOf(namespace, path) }
    else
        identifierOf(path = identifier)
}

fun identifierOf(namespace: String = BeaconConstants.NAMESPACE, path: String): Identifier = Identifier.of(namespace, path)

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

fun <O, A> MapCodec<A>.forKFunction(function: KFunction1<O, A>) = forGetter(function)
fun <O, A> MapCodec<A>.forKProperty(property: KProperty1<O, A>) = forGetter(property)

fun <O, A> Codec<A>.forKProperty(property: KProperty1<O, A>) = fieldOf(property.name).forGetter(property)

@JvmName("forKPropertyOptional")
fun <O, A> Codec<A>.forKProperty(property: KProperty1<O, Optional<A>>) = optionalFieldOf(property.name).forGetter(property)

@JvmName("forKPropertyDefaultValue")
fun <O, A> Codec<A>.forKProperty(property: KProperty1<O, A>, defaultValue: A) =
    optionalFieldOf(property.name, defaultValue).forGetter(property)

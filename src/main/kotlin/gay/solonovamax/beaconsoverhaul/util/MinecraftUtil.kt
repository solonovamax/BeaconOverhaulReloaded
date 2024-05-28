@file:Suppress("FunctionName")

package gay.solonovamax.beaconsoverhaul.util

import gay.solonovamax.beaconsoverhaul.BeaconConstants
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.entry.RegistryEntryList
import net.minecraft.registry.tag.TagKey
import net.minecraft.resource.featuretoggle.FeatureSet
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import net.minecraft.world.World
import vazkii.patchouli.common.util.ItemStackUtil

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

fun String.toItemStack(): ItemStack = ItemStackUtil.loadStackFromString(this)

inline fun <reified T : Entity> World.getNonSpectatingEntities(box: Box): List<T> {
    return getNonSpectatingEntities(T::class.java, box)
}

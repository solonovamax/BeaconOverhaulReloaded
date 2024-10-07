package gay.solonovamax.beaconsoverhaul.util

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.registry.Registry
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.entry.RegistryEntryList
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import kotlin.streams.asSequence

operator fun TagKey<Block>.contains(state: BlockState): Boolean {
    return state.isIn(this)
}

fun <V, T : V> Registry<V>.register(id: Identifier, entry: T): T = Registry.register(this, id, entry)
fun <T> Registry<in T>.register(id: String, entry: T): T = Registry.register(this, id, entry)

fun <T> Registry<T>.registerReference(id: Identifier, entry: T): RegistryEntry<T> = Registry.registerReference(this, id, entry)
fun <T> Registry<T>.registerReference(id: String, entry: T): RegistryEntry<T> = Registry.registerReference(this, identifierOf(id), entry)

operator fun RegistryEntryList<Block>.contains(state: BlockState): Boolean {
    return state.isIn(this)
}

fun <T> registryEntryListOf(): RegistryEntryList<T> = RegistryEntryList.empty()
fun <T> registryEntryListOf(vararg entries: RegistryEntry<T>): RegistryEntryList<T> = RegistryEntryList.of(*entries)
fun <T> List<RegistryEntry<T>>.toRegistryEntryList(): RegistryEntryList<T> = RegistryEntryList.of(this)
fun <T, E> List<E>.mapToRegistryEntryList(transform: (E) -> RegistryEntry<T>): RegistryEntryList<T> = RegistryEntryList.of(transform, this)

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

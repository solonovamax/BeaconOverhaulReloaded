package gay.solonovamax.beaconsoverhaul.util

import ca.solostudios.guava.kotlin.collect.Multiset

inline operator fun <E> Multiset<E>.get(element: E): Int = this.count(element)

fun <E> Multiset<E>.toMap(): Map<E, Int> = entrySet.fold(mutableMapOf()) { map, entry ->
    map.apply { this[entry.element] = entry.count }
}

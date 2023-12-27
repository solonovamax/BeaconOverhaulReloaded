package gay.solonovamax.beaconsoverhaul.util

import ca.solostudios.guava.kotlin.collect.Multiset

inline operator fun <E> Multiset<E>.get(element: E): Int = this.count(element)

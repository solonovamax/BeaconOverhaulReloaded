@file:JvmName("JvmUtils")

package gay.solonovamax.beaconsoverhaul.util

import org.jetbrains.annotations.Contract

@Contract("_ -> param1")
@Suppress("NOTHING_TO_INLINE")
inline fun <T> castUnchecked(any: Any?): T? {
    @Suppress("UNCHECKED_CAST")
    return any as T
}

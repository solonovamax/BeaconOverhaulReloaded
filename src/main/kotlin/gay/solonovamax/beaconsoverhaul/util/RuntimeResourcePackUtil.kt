package gay.solonovamax.beaconsoverhaul.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.devtech.arrp.api.RuntimeResourcePack
import net.minecraft.util.Identifier

@OptIn(ExperimentalSerializationApi::class)
val resourcePackJsonSerializer = Json {
    encodeDefaults = true
    prettyPrint = true
    explicitNulls = false
}

inline fun <reified T> RuntimeResourcePack.addData(identifier: Identifier, data: T): ByteArray {
    return this.addData(identifier, resourcePackJsonSerializer.encodeToString(data))
}

fun RuntimeResourcePack.addData(identifier: Identifier, data: String): ByteArray {
    return this.addData(identifier, data.toByteArray())
}

inline fun <reified T> RuntimeResourcePack.addAsset(identifier: Identifier, data: T): ByteArray {
    return this.addAsset(identifier, resourcePackJsonSerializer.encodeToString(data))
}

fun RuntimeResourcePack.addAsset(identifier: Identifier, data: String): ByteArray {
    return this.addAsset(identifier, data.toByteArray())
}

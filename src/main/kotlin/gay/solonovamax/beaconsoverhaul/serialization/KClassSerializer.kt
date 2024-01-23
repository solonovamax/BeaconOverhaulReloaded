package gay.solonovamax.beaconsoverhaul.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

class KClassSerializer : KSerializer<KClass<*>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Block", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): KClass<*> {
        return this::class.java.classLoader.loadClass(decoder.decodeString()).kotlin
    }

    override fun serialize(encoder: Encoder, value: KClass<*>) {
        encoder.encodeString(value.qualifiedName!!)
    }
}


package gay.solonovamax.beaconsoverhaul.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.registry.Registry
import net.silkmc.silk.core.serialization.serializers.ResourceLocationSerializer
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

abstract class RegistrySerializer<T : Any>(
    val registry: Registry<T>,
    klass: KClass<T>,
) : KSerializer<T> {
    private val delegateSerializer = ResourceLocationSerializer()

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(klass.simpleName ?: klass.jvmName, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): T {
        val identifier = decoder.decodeSerializableValue(delegateSerializer)
        return registry.getOrEmpty(identifier).orElseThrow()
    }

    override fun serialize(encoder: Encoder, value: T) {
        val identifier = registry.getEntry(value).key.orElseThrow().value
        encoder.encodeSerializableValue(delegateSerializer, identifier)
    }
}

package gay.solonovamax.beaconsoverhaul.config.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.registry.Registries
import net.silkmc.silk.core.serialization.serializers.ResourceLocationSerializer

class StatusEffectSerializer : KSerializer<StatusEffect> {
    private val delegateSerializer = ResourceLocationSerializer()

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StatusEffect", delegateSerializer.primitiveKind)
    override fun deserialize(decoder: Decoder): StatusEffect {
        val identifier = decoder.decodeSerializableValue(delegateSerializer)
        val statusEffect = Registries.STATUS_EFFECT.get(identifier)
        require(statusEffect != null) {
            "The status effect $statusEffect could not be found in Registries.STATUS_EFFECT."
        }
        return statusEffect
    }

    override fun serialize(encoder: Encoder, value: StatusEffect) {
        val identifier = Registries.STATUS_EFFECT.getId(value)

        if (identifier != null)
            encoder.encodeSerializableValue(delegateSerializer, identifier)
    }
}

package gay.solonovamax.beaconsoverhaul.config.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.block.Block
import net.minecraft.registry.Registries
import net.silkmc.silk.core.serialization.serializers.ResourceLocationSerializer as IdentifierSerializer


class BlockSerializer : KSerializer<Block> {
    private val delegateSerializer = IdentifierSerializer()

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Block", delegateSerializer.primitiveKind)
    override fun deserialize(decoder: Decoder): Block {
        val identifier = decoder.decodeSerializableValue(delegateSerializer)
        return Registries.BLOCK.get(identifier)
    }

    override fun serialize(encoder: Encoder, value: Block) {
        val identifier = Registries.BLOCK.getId(value)

        encoder.encodeSerializableValue(delegateSerializer, identifier)
    }
}

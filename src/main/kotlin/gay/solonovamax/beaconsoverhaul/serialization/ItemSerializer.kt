package gay.solonovamax.beaconsoverhaul.serialization

import gay.solonovamax.beaconsoverhaul.util.identifierOf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.item.Item
import net.minecraft.registry.Registries

class ItemSerializer : KSerializer<Item> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Item", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): Item {
        return Registries.ITEM[identifierOf(decoder.decodeString())]
    }

    override fun serialize(encoder: Encoder, value: Item) {
        val registryKey = Registries.ITEM.getKey(value)

        require(registryKey.isPresent) { "Could not find registered item id" }
        val identifier = registryKey.get().value

        return encoder.encodeString(identifier.toString())
    }
}


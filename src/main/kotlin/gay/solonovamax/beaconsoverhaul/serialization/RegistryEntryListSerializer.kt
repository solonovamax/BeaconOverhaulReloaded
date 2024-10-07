package gay.solonovamax.beaconsoverhaul.serialization

import gay.solonovamax.beaconsoverhaul.util.toRegistryEntryList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.registry.entry.RegistryEntryList

class RegistryEntryListSerializer<T : Any>(entrySerializer: RegistrySerializer<T>) : KSerializer<RegistryEntryList<T>> {
    private val delegate = ListSerializer(RegistryEntrySerializer(entrySerializer))

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun deserialize(decoder: Decoder): RegistryEntryList<T> {
        val entryList = delegate.deserialize(decoder)
        return entryList.toRegistryEntryList()
    }

    override fun serialize(encoder: Encoder, value: RegistryEntryList<T>) {
        delegate.serialize(encoder, value.toList())
    }
}

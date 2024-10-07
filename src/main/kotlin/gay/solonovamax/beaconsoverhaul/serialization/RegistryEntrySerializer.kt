package gay.solonovamax.beaconsoverhaul.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.registry.entry.RegistryEntry

class RegistryEntrySerializer<T : Any>(
    private val entrySerializer: RegistrySerializer<T>,
) : KSerializer<RegistryEntry<T>> {
    override val descriptor = entrySerializer.descriptor

    override fun deserialize(decoder: Decoder): RegistryEntry<T> {
        return entrySerializer.registry.getEntry(entrySerializer.deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: RegistryEntry<T>) {
        entrySerializer.serialize(encoder, value.value())
    }
}

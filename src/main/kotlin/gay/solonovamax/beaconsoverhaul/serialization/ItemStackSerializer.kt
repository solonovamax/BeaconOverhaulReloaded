package gay.solonovamax.beaconsoverhaul.serialization

import gay.solonovamax.beaconsoverhaul.util.toItemStack
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.item.ItemStack
import net.minecraft.nbt.visitor.StringNbtWriter
import net.minecraft.registry.Registries

class ItemStackSerializer : KSerializer<ItemStack> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ItemStack", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): ItemStack {
        return decoder.decodeString().toItemStack()
    }

    override fun serialize(encoder: Encoder, value: ItemStack) {
        val registryKey = Registries.ITEM.getKey(value.item)

        require(registryKey.isPresent) { "Could not find registered item id" }
        val identifier = registryKey.get().value

        return if (!value.hasNbt()) {
            encoder.encodeString(identifier.toString())
        } else {
            val nbt = value.nbt!!
            val nbtString = StringNbtWriter().apply(nbt)

            encoder.encodeString("$identifier$nbtString")
        }
    }
}


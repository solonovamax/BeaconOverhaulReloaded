package gay.solonovamax.beaconsoverhaul.serialization

import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import net.minecraft.block.Block
import net.minecraft.registry.Registries

class BlockSerializer : RegistrySerializer<Block>(Registries.BLOCK, Block::class) {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Block", PrimitiveKind.STRING)
}

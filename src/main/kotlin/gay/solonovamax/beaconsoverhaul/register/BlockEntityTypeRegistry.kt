package gay.solonovamax.beaconsoverhaul.register

import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity

object BlockEntityTypeRegistry {
    // val CORRUPTED_BEACON = createBlockEntity(BlockRegistry.CORRUPTED_BEACON) { pos, state ->
    //     CorruptedBeaconBlockEntity(pos, state)
    // }.build()
}

fun <T : BlockEntity?> createBlockEntity(
    vararg blocks: Block,
    factory: FabricBlockEntityTypeBuilder.Factory<out T>,
): FabricBlockEntityTypeBuilder<T> {
    return FabricBlockEntityTypeBuilder.create(factory, *blocks)
}

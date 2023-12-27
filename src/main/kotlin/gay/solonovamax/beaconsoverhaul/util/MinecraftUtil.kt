package gay.solonovamax.beaconsoverhaul.util

import gay.solonovamax.beaconsoverhaul.BeaconConstants
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.registry.entry.RegistryEntryList
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

fun identifierOf(namespace: String = BeaconConstants.NAMESPACE, path: String): Identifier = Identifier(namespace, path)

operator fun TagKey<Block>.contains(state: BlockState): Boolean {
    return state.isIn(this)
}

operator fun RegistryEntryList<Block>.contains(state: BlockState): Boolean {
    return state.isIn(this)
}

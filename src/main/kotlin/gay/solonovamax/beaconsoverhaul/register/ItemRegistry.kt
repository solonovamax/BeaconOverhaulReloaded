package gay.solonovamax.beaconsoverhaul.register

import gay.solonovamax.beaconsoverhaul.util.id
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import gay.solonovamax.beaconsoverhaul.util.register
import io.wispforest.lavender.book.LavenderBookItem
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries


object ItemRegistry {
    val GUIDEBOOK = LavenderBookItem.registerForBook(identifierOf("guidebook"), Item.Settings().maxCount(1))
    // val CORRUPTED_BEACON = BlockItem(BlockRegistry.CORRUPTED_BEACON, Item.Settings())

    fun register() {
        // CORRUPTED_BEACON.register()
    }

    private fun <T : BlockItem> T.register(): T {
        return Registries.ITEM.register(block.id, this)
    }
}

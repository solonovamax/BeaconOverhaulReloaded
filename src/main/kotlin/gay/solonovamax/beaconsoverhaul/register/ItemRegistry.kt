package gay.solonovamax.beaconsoverhaul.register

import gay.solonovamax.beaconsoverhaul.util.id
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import gay.solonovamax.beaconsoverhaul.util.register
import io.wispforest.lavender.book.LavenderBookItem
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries


object ItemRegistry : CommonRegistration {
    val GUIDEBOOK = LavenderBookItem.registerForBook(identifierOf("guidebook"), itemSettings {
        maxCount(1)
    })
    // val CORRUPTED_BEACON = BlockItem(BlockRegistry.CORRUPTED_BEACON, Item.Settings())

    val PRISMARINE_BRICK_WALL = BlockItem(BlockRegistry.PRISMARINE_BRICK_WALL, itemSettings())
    val DARK_PRISMARINE_WALL = BlockItem(BlockRegistry.DARK_PRISMARINE_WALL, itemSettings())

    override fun register() {
        // CORRUPTED_BEACON.register()
        PRISMARINE_BRICK_WALL.register()
        DARK_PRISMARINE_WALL.register()
    }

    private fun <T : BlockItem> T.register(): T {
        return Registries.ITEM.register(block.id, this)
    }

    private inline fun itemSettings(
        builder: Item.Settings.() -> Unit = {},
    ): Item.Settings {
        return Item.Settings().apply(builder)
    }
}

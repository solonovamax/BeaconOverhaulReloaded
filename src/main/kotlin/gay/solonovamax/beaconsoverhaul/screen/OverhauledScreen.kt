package gay.solonovamax.beaconsoverhaul.screen

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

abstract class OverhauledScreen<D, T : OverhauledScreenHandler<D>>(
    handler: T,
    inventory: PlayerInventory?,
    title: Text,
) : HandledScreen<T>(handler, inventory, title) {
    val data: D
        get() = handler.data

    final override fun drawForeground(context: DrawContext, mouseX: Int, mouseY: Int) {
        with(context) {
            with(Unit) {
                drawForeground(mouseX, mouseY)
            }
        }
    }

    context(DrawContext, Unit)
    open fun drawForeground(mouseX: Int, mouseY: Int) {
    }

    final override fun drawBackground(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        with(context) {
            with(Unit) {
                drawBackground(delta, mouseX, mouseY)
            }
        }
    }

    context(DrawContext, Unit) // nasty hack to bypass having the same jvm signature
    open fun drawBackground(delta: Float, mouseX: Int, mouseY: Int) {
    }

    final override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        super.render(context, mouseX, mouseY, delta)
        with(context) {
            drawMouseoverTooltip(mouseX, mouseY)
            with(Unit) {
                render(delta, mouseX, mouseY)
            }
        }
    }


    context(DrawContext, Unit)
    open fun render(delta: Float, mouseX: Int, mouseY: Int) {
    }

    context(DrawContext)
    @JvmName("drawSlotHighlightExt")
    fun drawSlotHighlight(x: Int, y: Int, z: Int) = drawSlotHighlight(this@DrawContext, x, y, z)

    context(DrawContext)
    @JvmName("drawMouseoverTooltipExt")
    fun drawMouseoverTooltip(x: Int, y: Int) = super.drawMouseoverTooltip(this@DrawContext, x, y)
}

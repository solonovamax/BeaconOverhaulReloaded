package gay.solonovamax.beaconsoverhaul.util

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.model.RGB
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.OrderedText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text

context(DrawContext)
fun drawHorizontalLine(x1: Int, x2: Int, y: Int, color: Color) {
    drawHorizontalLine(x1, x2, y, color.toSRGB().toRGBInt().argb.toInt())
}

context(DrawContext)
fun drawHorizontalLine(layer: RenderLayer, x1: Int, x2: Int, y: Int, color: Color) {
    drawHorizontalLine(layer, x1, x2, y, color.toSRGB().toRGBInt().argb.toInt())
}

context(DrawContext)
fun drawVerticalLine(x: Int, y1: Int, y2: Int, color: Color) {
    drawVerticalLine(x, y1, y2, color.toSRGB().toRGBInt().argb.toInt())
}

context(DrawContext)
fun drawVerticalLine(layer: RenderLayer, x: Int, y1: Int, y2: Int, color: Color) {
    drawVerticalLine(layer, x, y1, y2, color.toSRGB().toRGBInt().argb.toInt())
}

context(DrawContext)
fun setShaderColor(color: Color) {
    val rgb = color.toSRGB()
    setShaderColor(rgb.r, rgb.g, rgb.b, rgb.alpha)
}

context(DrawContext)
fun fill(x1: Int, y1: Int, x2: Int, y2: Int, color: Color) {
    fill(x1, y1, x2, y2, color.toSRGB().toRGBInt().argb.toInt())
}

context(DrawContext)
fun fill(x1: Int, y1: Int, x2: Int, y2: Int, z: Int, color: Color) {
    fill(x1, y1, x2, y2, z, color.toSRGB().toRGBInt().argb.toInt())
}

context(DrawContext)
fun fill(layer: RenderLayer, x1: Int, y1: Int, x2: Int, y2: Int, color: Color) {
    fill(layer, x1, y1, x2, y2, color.toSRGB().toRGBInt().argb.toInt())
}

context(DrawContext)
fun fill(layer: RenderLayer, x1: Int, y1: Int, x2: Int, y2: Int, z: Int, color: Color) {
    fill(layer, x1, y1, x2, y2, z, color.toSRGB().toRGBInt().argb.toInt())
}

context(DrawContext)
fun fillGradient(startX: Int, startY: Int, endX: Int, endY: Int, colorStart: Color, colorEnd: Color) {
    val colorStartInt = colorStart.toSRGB().toRGBInt().argb.toInt()
    val colorEndInt = colorEnd.toSRGB().toRGBInt().argb.toInt()
    fillGradient(startX, startY, endX, endY, colorStartInt, colorEndInt)
}

context(DrawContext)
fun fillGradient(startX: Int, startY: Int, endX: Int, endY: Int, z: Int, colorStart: Color, colorEnd: Color) {
    val colorStartInt = colorStart.toSRGB().toRGBInt().argb.toInt()
    val colorEndInt = colorEnd.toSRGB().toRGBInt().argb.toInt()
    fillGradient(startX, startY, endX, endY, z, colorStartInt, colorEndInt)
}

context(DrawContext)
fun fillGradient(
    layer: RenderLayer,
    startX: Int,
    startY: Int,
    endX: Int,
    endY: Int,
    colorStart: Color,
    colorEnd: Color,
    z: Int,
) {
    val colorStartInt = colorStart.toSRGB().toRGBInt().argb.toInt()
    val colorEndInt = colorEnd.toSRGB().toRGBInt().argb.toInt()
    fillGradient(layer, startX, startY, endX, endY, colorStartInt, colorEndInt, z)
}

context(DrawContext)
fun drawCenteredTextWithShadow(textRenderer: TextRenderer, text: String, centerX: Int, y: Int, color: Color) {
    drawCenteredTextWithShadow(textRenderer, text, centerX, y, color.toSRGB().toRGBInt().argb.toInt())
}

context(DrawContext)
fun drawCenteredTextWithShadow(textRenderer: TextRenderer, text: Text, centerX: Int, y: Int, color: Color) {
    drawCenteredTextWithShadow(textRenderer, text, centerX, y, color.toSRGB().toRGBInt().argb.toInt())
}

context(DrawContext)
fun drawCenteredTextWithShadow(textRenderer: TextRenderer, text: OrderedText, centerX: Int, y: Int, color: Color) {
    drawCenteredTextWithShadow(textRenderer, text, centerX, y, color.toSRGB().toRGBInt().argb.toInt())
}

context(DrawContext)
fun drawTextWithShadow(textRenderer: TextRenderer, text: String, x: Int, y: Int, color: Color): Int {
    return drawTextWithShadow(textRenderer, text, x, y, color.toSRGB().toRGBInt().argb.toInt())
}

context(DrawContext)
fun drawText(textRenderer: TextRenderer, text: String, x: Int, y: Int, color: Color, shadow: Boolean): Int {
    return drawText(textRenderer, text, x, y, color.toSRGB().toRGBInt().argb.toInt(), shadow)
}

context(DrawContext)
fun drawTextWithShadow(textRenderer: TextRenderer, text: OrderedText, x: Int, y: Int, color: Color): Int {
    return drawTextWithShadow(textRenderer, text, x, y, color.toSRGB().toRGBInt().argb.toInt())
}

context(DrawContext)
fun drawText(textRenderer: TextRenderer, text: OrderedText, x: Int, y: Int, color: Color, shadow: Boolean): Int {
    return drawText(textRenderer, text, x, y, color.toSRGB().toRGBInt().argb.toInt(), shadow)
}

context(DrawContext)
fun drawTextWithShadow(textRenderer: TextRenderer, text: Text, x: Int, y: Int, color: Color): Int {
    return drawTextWithShadow(textRenderer, text, x, y, color.toSRGB().toRGBInt().argb.toInt())
}

context(DrawContext)
fun drawText(textRenderer: TextRenderer, text: Text, x: Int, y: Int, color: Color, shadow: Boolean): Int {
    return drawText(textRenderer, text, x, y, color.toSRGB().toRGBInt().argb.toInt(), shadow)
}

context(DrawContext)
fun drawTextWrapped(textRenderer: TextRenderer, text: StringVisitable, x: Int, y: Int, width: Int, color: Color) {
    drawTextWrapped(textRenderer, text, x, y, width, color.toSRGB().toRGBInt().argb.toInt())
}

context(DrawContext)
fun drawBorder(x: Int, y: Int, width: Int, height: Int, color: Color) {
    drawBorder(x, y, width, height, color.toSRGB().toRGBInt().argb.toInt())
}

context(DrawContext)
fun drawItem(item: Item, x: Int, y: Int) {
    drawItem(ItemStack(item), x, y)
}

fun VertexConsumer.color(color: RGB): VertexConsumer = color.run { color(r, g, b, alpha) }
fun VertexConsumer.color(color: Color): VertexConsumer = color.toSRGB().run { color(r, g, b, alpha) }

inline fun MatrixStack.scoped(action: () -> Unit) {
    push()
    action()
    pop()
}

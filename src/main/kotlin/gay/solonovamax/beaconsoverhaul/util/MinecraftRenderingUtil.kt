package gay.solonovamax.beaconsoverhaul.util

import com.github.ajalt.colormath.Color
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.OrderedText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text

fun DrawContext.drawHorizontalLine(x1: Int, x2: Int, y: Int, color: Color) {
    drawHorizontalLine(x1, x2, y, color.toSRGB().toRGBInt().argb.toInt())
}

fun DrawContext.drawHorizontalLine(layer: RenderLayer, x1: Int, x2: Int, y: Int, color: Color) {
    drawHorizontalLine(layer, x1, x2, y, color.toSRGB().toRGBInt().argb.toInt())
}

fun DrawContext.drawVerticalLine(x: Int, y1: Int, y2: Int, color: Color) {
    drawVerticalLine(x, y1, y2, color.toSRGB().toRGBInt().argb.toInt())
}

fun DrawContext.drawVerticalLine(layer: RenderLayer, x: Int, y1: Int, y2: Int, color: Color) {
    drawVerticalLine(layer, x, y1, y2, color.toSRGB().toRGBInt().argb.toInt())
}

fun DrawContext.setShaderColor(color: Color) {
    val rgb = color.toSRGB()
    setShaderColor(rgb.r, rgb.g, rgb.b, rgb.alpha)
}

fun DrawContext.fill(x1: Int, y1: Int, x2: Int, y2: Int, color: Color) {
    fill(x1, y1, x2, y2, color.toSRGB().toRGBInt().argb.toInt())
}

fun DrawContext.fill(x1: Int, y1: Int, x2: Int, y2: Int, z: Int, color: Color) {
    fill(x1, y1, x2, y2, z, color.toSRGB().toRGBInt().argb.toInt())
}

fun DrawContext.fill(layer: RenderLayer, x1: Int, y1: Int, x2: Int, y2: Int, color: Color) {
    fill(layer, x1, y1, x2, y2, color.toSRGB().toRGBInt().argb.toInt())
}

fun DrawContext.fill(layer: RenderLayer, x1: Int, y1: Int, x2: Int, y2: Int, z: Int, color: Color) {
    fill(layer, x1, y1, x2, y2, z, color.toSRGB().toRGBInt().argb.toInt())
}

fun DrawContext.fillGradient(startX: Int, startY: Int, endX: Int, endY: Int, colorStart: Color, colorEnd: Color) {
    val colorStartInt = colorStart.toSRGB().toRGBInt().argb.toInt()
    val colorEndInt = colorEnd.toSRGB().toRGBInt().argb.toInt()
    fillGradient(startX, startY, endX, endY, colorStartInt, colorEndInt)
}

fun DrawContext.fillGradient(startX: Int, startY: Int, endX: Int, endY: Int, z: Int, colorStart: Color, colorEnd: Color) {
    val colorStartInt = colorStart.toSRGB().toRGBInt().argb.toInt()
    val colorEndInt = colorEnd.toSRGB().toRGBInt().argb.toInt()
    fillGradient(startX, startY, endX, endY, z, colorStartInt, colorEndInt)
}

fun DrawContext.fillGradient(
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

fun DrawContext.drawCenteredTextWithShadow(textRenderer: TextRenderer, text: String, centerX: Int, y: Int, color: Color) {
    drawCenteredTextWithShadow(textRenderer, text, centerX, y, color.toSRGB().toRGBInt().argb.toInt())
}

fun DrawContext.drawCenteredTextWithShadow(textRenderer: TextRenderer, text: Text, centerX: Int, y: Int, color: Color) {
    drawCenteredTextWithShadow(textRenderer, text, centerX, y, color.toSRGB().toRGBInt().argb.toInt())
}

fun DrawContext.drawCenteredTextWithShadow(textRenderer: TextRenderer, text: OrderedText, centerX: Int, y: Int, color: Color) {
    drawCenteredTextWithShadow(textRenderer, text, centerX, y, color.toSRGB().toRGBInt().argb.toInt())
}

fun DrawContext.drawTextWithShadow(textRenderer: TextRenderer, text: String, x: Int, y: Int, color: Color): Int {
    return drawTextWithShadow(textRenderer, text, x, y, color.toSRGB().toRGBInt().argb.toInt())
}

fun DrawContext.drawText(textRenderer: TextRenderer, text: String, x: Int, y: Int, color: Color, shadow: Boolean): Int {
    return drawText(textRenderer, text, x, y, color.toSRGB().toRGBInt().argb.toInt(), shadow)
}

fun DrawContext.drawTextWithShadow(textRenderer: TextRenderer, text: OrderedText, x: Int, y: Int, color: Color): Int {
    return drawTextWithShadow(textRenderer, text, x, y, color.toSRGB().toRGBInt().argb.toInt())
}

fun DrawContext.drawText(textRenderer: TextRenderer, text: OrderedText, x: Int, y: Int, color: Color, shadow: Boolean): Int {
    return drawText(textRenderer, text, x, y, color.toSRGB().toRGBInt().argb.toInt(), shadow)
}

fun DrawContext.drawTextWithShadow(textRenderer: TextRenderer, text: Text, x: Int, y: Int, color: Color): Int {
    return drawTextWithShadow(textRenderer, text, x, y, color.toSRGB().toRGBInt().argb.toInt())
}

fun DrawContext.drawText(textRenderer: TextRenderer, text: Text, x: Int, y: Int, color: Color, shadow: Boolean): Int {
    return drawText(textRenderer, text, x, y, color.toSRGB().toRGBInt().argb.toInt(), shadow)
}

fun DrawContext.drawTextWrapped(textRenderer: TextRenderer, text: StringVisitable, x: Int, y: Int, width: Int, color: Color) {
    drawTextWrapped(textRenderer, text, x, y, width, color.toSRGB().toRGBInt().argb.toInt())
}

fun DrawContext.drawBorder(x: Int, y: Int, width: Int, height: Int, color: Color) {
    drawBorder(x, y, width, height, color.toSRGB().toRGBInt().argb.toInt())
}

fun DrawContext.drawItem(item: Item, x: Int, y: Int) {
    drawItem(ItemStack(item), x, y)
}

package gay.solonovamax.beaconsoverhaul.integration.lavender

import io.wispforest.lavender.client.StructureOverlayRenderer
import io.wispforest.lavender.structure.BlockStatePredicate
import io.wispforest.lavender.structure.LavenderStructures
import io.wispforest.lavender.structure.StructureTemplate
import io.wispforest.owo.ui.base.BaseComponent
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.CursorStyle
import io.wispforest.owo.ui.core.Easing
import io.wispforest.owo.ui.core.OwoUIDrawContext
import io.wispforest.owo.ui.parsing.UIModelParsingException
import io.wispforest.owo.ui.parsing.UIParsing
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RotationAxis
import org.lwjgl.glfw.GLFW
import org.w3c.dom.Element
import kotlin.math.max
import kotlin.math.min

class StructureComponent(
    var structure: StructureTemplate,
    private val displayAngle: Int,
) : BaseComponent() {
    private var rotation = -45f
    private var lastInteractionTime = 0L

    var placeable = true
        set(value) {
            if (!value)
                tooltip(null)
            cursorStyle(if (placeable) CursorStyle.HAND else CursorStyle.POINTER)

            field = value
        }
    var visibleLayer = -1
        set(value) {
            StructureOverlayRenderer.restrictVisibleLayer(structure.id, visibleLayer)

            field = value
        }

    var showLayersBelowVisible = true

    init {
        cursorStyle(CursorStyle.HAND)
    }

    override fun update(delta: Float, mouseX: Int, mouseY: Int) {
        super.update(delta, mouseX, mouseY)

        val diff = Util.getMeasuringTimeMs() - lastInteractionTime
        if (diff < 5000L) return

        rotation += delta * Easing.SINE.apply(min(1.0f, ((diff - 5000) / 1500f)))
    }

    override fun draw(context: OwoUIDrawContext, mouseX: Int, mouseY: Int, partialTicks: Float, delta: Float) {
        val client = MinecraftClient.getInstance()
        val entityBuffers = client.bufferBuilders.entityVertexConsumers

        var scale = min(width, height).toFloat()
        scale /= max(structure.xSize, max(structure.ySize, structure.zSize)).toFloat()
        scale /= 1.625f

        val matrices = context.matrices

        matrices.push()
        matrices.translate(x + width / 2f, y + height / 2f, 100f)
        matrices.scale(scale, -scale, scale)

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(displayAngle.toFloat()))
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation))
        matrices.translate(structure.xSize / -2f, structure.ySize / -2f, structure.zSize / -2f)

        structure.forEachPredicate { blockPos: BlockPos, predicate: BlockStatePredicate ->
            if (visibleLayer != -1 && if (showLayersBelowVisible) visibleLayer < blockPos.y else visibleLayer != blockPos.y)
                return@forEachPredicate

            matrices.push()
            matrices.translate(blockPos.x.toFloat(), blockPos.y.toFloat(), blockPos.z.toFloat())

            client.blockRenderManager.renderBlockAsEntity(
                predicate.preview(), matrices, entityBuffers,
                LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE,
                OverlayTexture.DEFAULT_UV
            )
            matrices.pop()
        }

        matrices.pop()

        DiffuseLighting.disableGuiDepthLighting()
        entityBuffers.draw()
        DiffuseLighting.enableGuiDepthLighting()

        if (placeable) {
            if (StructureOverlayRenderer.isShowingOverlay(structure.id)) {
                context.drawText(
                    client.textRenderer, Text.translatable("text.lavender.structure_component.active_overlay_hint"),
                    x + width - 5 - client.textRenderer.getWidth("⚓"),
                    y + height - 9 - 5, 0, false
                )
                tooltip(Text.translatable("text.lavender.structure_component.hide_hint"))
            } else {
                tooltip(Text.translatable("text.lavender.structure_component.place_hint"))
            }
        }
    }

    override fun onMouseDown(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val result = super.onMouseDown(mouseX, mouseY, button)
        if (!placeable || button != GLFW.GLFW_MOUSE_BUTTON_LEFT || !Screen.hasShiftDown())
            return result

        if (StructureOverlayRenderer.isShowingOverlay(structure.id)) {
            StructureOverlayRenderer.removeAllOverlays(structure.id)
        } else {
            StructureOverlayRenderer.addPendingOverlay(structure.id)
            StructureOverlayRenderer.restrictVisibleLayer(structure.id, visibleLayer)

            MinecraftClient.getInstance().setScreen(null)
        }

        return true
    }

    override fun onMouseDrag(mouseX: Double, mouseY: Double, deltaX: Double, deltaY: Double, button: Int): Boolean {
        val result = super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button)

        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT)
            return result

        rotation += deltaX.toFloat()
        lastInteractionTime = Util.getMeasuringTimeMs()

        return true
    }

    override fun canFocus(source: Component.FocusSource): Boolean {
        return source == Component.FocusSource.MOUSE_CLICK
    }

    companion object {
        fun parse(element: Element): StructureComponent {
            UIParsing.expectAttributes(element, "structure-id")

            val structureId = Identifier.tryParse(element.getAttribute("structure-id"))
                ?: throw UIModelParsingException("Invalid structure id '${element.getAttribute("structure-id")}'")

            val structure = LavenderStructures.get(structureId) ?: throw UIModelParsingException("Unknown structure '$structureId'")

            val displayAngle = if (element.hasAttribute("display-angle")) {
                UIParsing.parseSignedInt(element.getAttributeNode("display-angle"))
            } else {
                35
            }

            return StructureComponent(structure, displayAngle)
        }
    }
}

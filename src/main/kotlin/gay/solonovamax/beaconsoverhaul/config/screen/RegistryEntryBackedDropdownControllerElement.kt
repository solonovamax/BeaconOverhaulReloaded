package gay.solonovamax.beaconsoverhaul.config.screen

import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.YACLScreen
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownControllerElement
import gay.solonovamax.beaconsoverhaul.util.getEntryIdentifiersMatching
import net.minecraft.client.gui.DrawContext
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.jvm.optionals.getOrNull

open class RegistryEntryBackedDropdownControllerElement<T : Any>(
    private val controller: RegistryEntryBackedController<T>,
    screen: YACLScreen,
    dim: Dimension<Int>,
) : AbstractDropdownControllerElement<RegistryEntry<T>, Identifier>(controller, screen, dim) {
    private var currentEntry: RegistryEntry<T>? = null

    override fun drawValueText(graphics: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val oldDimension = dimension
        dimension = dimension.withWidth(dimension.width() - decorationPadding)
        super.drawValueText(graphics, mouseX, mouseY, delta)
        dimension = oldDimension
        if (currentEntry != null) {
            drawCurrentEntry(graphics, dimension.xLimit() - xPadding - decorationPadding + 2, dimension.y() + 2, currentEntry!!)
        }
    }

    open fun drawCurrentEntry(context: DrawContext, x: Int, y: Int, entry: RegistryEntry<T>) {
    }

    override fun renderDropdownEntry(graphics: DrawContext, entryDimension: Dimension<Int>, identifier: Identifier?) {
        super.renderDropdownEntry(graphics, entryDimension, identifier)
        if (controller.registry.containsId(identifier))
            drawCurrentEntry(
                graphics,
                entryDimension.xLimit() - 2,
                entryDimension.y() + 1,
                controller.registry.getEntry(identifier).get()
            )
    }


    override fun computeMatchingValues(): List<Identifier> {
        val id = Identifier.tryParse(inputField)
        currentEntry = if (id != null) controller.registry.getEntry(id).getOrNull() else null
        return controller.registry.getEntryIdentifiersMatching(inputField).toList()
    }

    override fun getString(obj: Identifier): String {
        return obj.toString()
    }

    override fun getControlWidth() = super.getControlWidth() + decorationPadding

    override fun getValueText(): Text {
        if (inputField.isEmpty()) return super.getValueText()

        if (inputFieldFocused) return Text.literal(inputField)

        @Suppress("UNCHECKED_CAST")
        val controller = control as RegistryBackedController<T>
        return Text.literal(controller.registry.getId(controller.option().pendingValue()).toString())
    }
}


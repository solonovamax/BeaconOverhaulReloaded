package gay.solonovamax.beaconsoverhaul.config.screen

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.AbstractWidget
import dev.isxander.yacl3.gui.YACLScreen
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownController
import gay.solonovamax.beaconsoverhaul.util.getEntryIdentifiersMatching
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.InvalidIdentifierException

open class RegistryBackedController<T : Any>(
    option: Option<T>,
    val registry: Registry<T>,
) : AbstractDropdownController<T>(option) {
    override fun getString(): String {
        return registry.getId(option.pendingValue()).toString()
    }

    override fun setFromString(value: String) {
        val id = Identifier(value)
        option.requestSet(registry.getOrEmpty(id).orElse(option.pendingValue()))
    }

    override fun formatValue(): Text = Text.literal(string)


    override fun isValueValid(value: String): Boolean {
        return try {
            registry.containsId(Identifier(value))
        } catch (e: InvalidIdentifierException) {
            false
        }
    }

    override fun getValidValue(value: String, offset: Int): String {
        return registry.getEntryIdentifiersMatching(value).drop(offset).firstOrNull()?.toString() ?: string
    }

    override fun provideWidget(screen: YACLScreen, widgetDimension: Dimension<Int>): AbstractWidget {
        return RegistryBackedDropdownControllerElement(this, screen, widgetDimension)
    }
}


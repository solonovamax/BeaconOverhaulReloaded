package gay.solonovamax.beaconsoverhaul.config.screen

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.AbstractWidget
import dev.isxander.yacl3.gui.YACLScreen
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownController
import gay.solonovamax.beaconsoverhaul.util.getEntryIdentifiersMatching
import net.minecraft.registry.Registry
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.InvalidIdentifierException
import kotlin.jvm.optionals.getOrNull

open class RegistryEntryBackedController<T : Any>(
    option: Option<RegistryEntry<T>>,
    val registry: Registry<T>,
) : AbstractDropdownController<RegistryEntry<T>>(option) {
    override fun getString(): String {
        return registry.getId(option.pendingValue().value()).toString()
    }

    override fun setFromString(value: String) {
        option.requestSet(registry.getEntry(Identifier.of(value)).getOrNull() ?: option.pendingValue())
    }

    override fun formatValue(): Text = Text.literal(string)


    override fun isValueValid(value: String): Boolean {
        return try {
            registry.containsId(Identifier.of(value))
        } catch (e: InvalidIdentifierException) {
            false
        }
    }

    override fun getValidValue(value: String, offset: Int): String {
        return registry.getEntryIdentifiersMatching(value).drop(offset).firstOrNull()?.toString() ?: string
    }

    override fun provideWidget(screen: YACLScreen, widgetDimension: Dimension<Int>): AbstractWidget {
        return RegistryEntryBackedDropdownControllerElement(this, screen, widgetDimension)
    }
}


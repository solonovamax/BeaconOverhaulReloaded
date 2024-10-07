package gay.solonovamax.beaconsoverhaul.config.screen

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.AbstractWidget
import dev.isxander.yacl3.gui.YACLScreen
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.registry.Registries
import net.minecraft.registry.entry.RegistryEntry

class StatusEffectController(
    option: Option<RegistryEntry<StatusEffect>>,
) : RegistryEntryBackedController<StatusEffect>(option, Registries.STATUS_EFFECT) {
    override fun provideWidget(screen: YACLScreen, widgetDimension: Dimension<Int>): AbstractWidget {
        return StatusEffectControllerElement(this, screen, widgetDimension)
    }
}


package gay.solonovamax.beaconsoverhaul.config.screen

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.AbstractWidget
import dev.isxander.yacl3.gui.YACLScreen
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.registry.Registries

class StatusEffectController(
    option: Option<StatusEffect>,
) : RegistryBackedController<StatusEffect>(option, Registries.STATUS_EFFECT) {
    override fun provideWidget(screen: YACLScreen, widgetDimension: Dimension<Int>): AbstractWidget {
        return StatusEffectControllerElement(this, screen, widgetDimension)
    }
}


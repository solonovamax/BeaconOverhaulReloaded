package gay.solonovamax.beaconsoverhaul.config.screen

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.AbstractWidget
import dev.isxander.yacl3.gui.YACLScreen
import net.minecraft.block.Block
import net.minecraft.registry.Registries

class BlockController(
    option: Option<Block>,
) : RegistryBackedController<Block>(option, Registries.BLOCK) {
    override fun provideWidget(screen: YACLScreen, widgetDimension: Dimension<Int>): AbstractWidget {
        return BlockControllerElement(this, screen, widgetDimension)
    }
}


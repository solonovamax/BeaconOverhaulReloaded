package gay.solonovamax.beaconsoverhaul.config.screen

import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.YACLScreen
import net.minecraft.block.Block
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

class BlockControllerElement(
    controller: BlockController,
    screen: YACLScreen,
    dim: Dimension<Int>,
) : RegistryBackedDropdownControllerElement<Block>(controller, screen, dim) {
    override fun drawCurrentEntry(context: DrawContext, x: Int, y: Int, entry: Block) {
        context.drawItemWithoutEntity(ItemStack(entry), x, y)
    }

    override fun getDecorationPadding() = 16
    override fun getDropdownEntryPadding() = 4

    override fun getValueText(): Text {
        if (inputField.isEmpty()) return super.getValueText()

        if (inputFieldFocused) return Text.literal(inputField)

        return (control as BlockController).option().pendingValue().name
    }
}


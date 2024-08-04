package gay.solonovamax.beaconsoverhaul.config.screen

import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.YACLScreen
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.text.Text

class StatusEffectControllerElement(
    controller: StatusEffectController,
    screen: YACLScreen,
    dim: Dimension<Int>,
) : RegistryBackedDropdownControllerElement<StatusEffect>(controller, screen, dim) {
    override fun drawCurrentEntry(
        context: DrawContext,
        x: Int,
        y: Int,
        entry: StatusEffect,
    ) {
        val statusEffectSpriteManager = client.statusEffectSpriteManager

        val sprite = statusEffectSpriteManager.getSprite(entry)
        context.drawSprite(x, y, 0, 16, 16, sprite)
    }

    override fun getDecorationPadding() = 18
    override fun getDropdownEntryPadding() = 4

    override fun getValueText(): Text {
        if (inputField.isEmpty()) return super.getValueText()

        if (inputFieldFocused) return Text.literal(inputField)

        return (control as StatusEffectController).option().pendingValue().name
    }
}


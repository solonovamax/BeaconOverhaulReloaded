package gay.solonovamax.beaconsoverhaul.config.screen

import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.YACLScreen
import dev.isxander.yacl3.gui.controllers.string.IStringController
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement

open class BeaconExpressionControllerElement(
    control: IStringController<*>,
    screen: YACLScreen,
    dim: Dimension<Int>,
    instantApply: Boolean,
) : StringControllerElement(control, screen, dim, instantApply) {

    override fun setFocused(focused: Boolean) {
        if (focused) {
            doSelectAll()
            super.setFocused(true)
        } else unfocus()
    }

    override fun unfocus() {
        val controller = control as BeaconExpressionController<*>
        controller.setFromString(controller.option().pendingValue().expressionString)
    }

    override fun getValueColor(): Int {
        val controller = control as BeaconExpressionController<*>
        if (inputFieldFocused) {
            if (!controller.isValid(inputField)) {
                return 0xFFF06080u.toInt()
            }
        }
        return super.getValueColor()
    }
}

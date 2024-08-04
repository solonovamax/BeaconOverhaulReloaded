package gay.solonovamax.beaconsoverhaul.config.screen

import com.dfsek.paralithic.eval.tokenizer.ParseException
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.AbstractWidget
import dev.isxander.yacl3.gui.YACLScreen
import dev.isxander.yacl3.gui.controllers.string.IStringController
import gay.solonovamax.beaconsoverhaul.config.BeaconConfig.BeaconExpression

class BeaconExpressionController<T : BeaconExpression>(
    private val option: Option<T>,
    private val modifierExpressionSupplier: (String) -> T,
) : IStringController<T> {
    private var current = option.pendingValue().expressionString

    override fun option() = option

    override fun getString() = current

    override fun setFromString(value: String) {
        current = value
        if (isValid(value))
            option.requestSet(modifierExpressionSupplier(value))
    }

    fun isValid(value: String): Boolean {
        return try {
            val expression = modifierExpressionSupplier(value)
            expression.evaluate(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
            true
        } catch (e: ParseException) {
            false
        }
    }

    override fun provideWidget(screen: YACLScreen, widgetDimension: Dimension<Int>): AbstractWidget {
        return BeaconExpressionControllerElement(this, screen, widgetDimension, true)
    }
}


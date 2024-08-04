package gay.solonovamax.beaconsoverhaul.util

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.model.RGBInt
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.gui.entries.ColorEntry
import me.shedaniel.clothconfig2.gui.entries.StringListEntry
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder
import me.shedaniel.clothconfig2.impl.builders.ColorFieldBuilder
import me.shedaniel.clothconfig2.impl.builders.StringFieldBuilder
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import java.util.Optional
import java.util.function.Supplier
import me.shedaniel.math.Color as ClothConfigColor

var ConfigBuilder.transparentBackground: Boolean
    get() = hasTransparentBackground()
    set(value) {
        setTransparentBackground(value)
    }

fun ConfigBuilder.stringEntry(name: Text, value: String, builder: ClothStringFieldBuilder.() -> Unit): StringListEntry {
    return ClothStringFieldBuilder(entryBuilder().startStrField(name, value)).apply(builder).build()
}

fun ConfigBuilder.colorEntry(name: Text, value: TextColor, builder: ClothColorFieldBuilder.() -> Unit): ColorEntry {
    return ClothColorFieldBuilder(entryBuilder().startColorField(name, value)).apply(builder).build()
}

fun ConfigBuilder.colorEntry(name: Text, value: Color, builder: ClothColorFieldBuilder.() -> Unit): ColorEntry {
    val srgbColor = value.toSRGB()
    val clothColor = ClothConfigColor.ofRGBA(srgbColor.redInt, srgbColor.greenInt, srgbColor.blueInt, srgbColor.alphaInt)
    return ClothColorFieldBuilder(entryBuilder().startColorField(name, clothColor)).apply(builder).build()
}

abstract class ClothFieldBuilder<T, O, B : AbstractFieldBuilder<O, R, B>, R : AbstractConfigListEntry<O>>(
    val builder: B,
    private val forwardConverter: (T) -> O,
    private val backwardConverter: (O) -> T,
) {
    var requireRestart: Boolean = false

    private var defaultValue: (() -> T)? = null
    private var errorSupplier: ((T) -> Text?)? = null
    private var saveConsumer: ((T) -> Unit)? = null
    private var tooltipSupplier: ((T) -> List<Text>?)? = null

    fun default(supplier: () -> T) {
        defaultValue = supplier
    }

    fun onError(supplier: (T) -> Text) {
        errorSupplier = supplier
    }

    fun onSave(consumer: (T) -> Unit) {
        saveConsumer = consumer
    }

    fun tooltip(supplier: (T) -> Text) {
        tooltipSupplier = { listOf(supplier(it)) }
    }

    fun tooltipLines(supplier: (T) -> List<Text>) {
        tooltipSupplier = supplier
    }

    fun build(): R {
        builder.requireRestart(requireRestart)
        defaultValue?.let { defaultValue ->
            builder.defaultValue = Supplier {
                forwardConverter(defaultValue())
            }
        }
        errorSupplier?.let { errorSupplier ->
            builder.setErrorSupplier { value ->
                Optional.ofNullable(errorSupplier(backwardConverter(value)))
            }
        }
        saveConsumer?.let { saveConsumer ->
            builder.setSaveConsumer { value ->
                saveConsumer(backwardConverter(value))
            }
        }
        tooltipSupplier?.let { tooltipSupplier ->
            builder.setTooltipSupplier { value ->
                Optional.ofNullable(tooltipSupplier(backwardConverter(value))?.toTypedArray())
            }
        }

        return builder.build()
    }
}

abstract class ClothIdentityFieldBuilder<T, B : AbstractFieldBuilder<T, R, B>, R : AbstractConfigListEntry<T>>(
    builder: B,
) : ClothFieldBuilder<T, T, B, R>(builder, ::identity, ::identity)

class ClothStringFieldBuilder(
    builder: StringFieldBuilder,
) : ClothIdentityFieldBuilder<String, StringFieldBuilder, StringListEntry>(builder)

class ClothColorFieldBuilder(
    builder: ColorFieldBuilder,
) : ClothFieldBuilder<Color, Int, ColorFieldBuilder, ColorEntry>(builder, ::toRGBInt, ::fromIntColor)

private fun fromIntColor(intColor: Int): Color {
    return RGBInt.fromRGBA(intColor.toUInt())
}

private fun toRGBInt(color: Color): Int {
    return color.toSRGB().toRGBInt().argb.toInt()
}

private fun <T> identity(x: T): T = x

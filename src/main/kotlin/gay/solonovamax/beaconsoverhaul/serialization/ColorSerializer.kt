package gay.solonovamax.beaconsoverhaul.serialization

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.RenderCondition
import com.github.ajalt.colormath.model.SRGB
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Block", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): Color {
        return SRGB(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeString(value.toSRGB().toHex(withNumberSign = false, renderAlpha = RenderCondition.NEVER))
    }
}

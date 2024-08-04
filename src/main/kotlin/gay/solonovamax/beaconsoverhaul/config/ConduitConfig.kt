package gay.solonovamax.beaconsoverhaul.config

import com.dfsek.paralithic.Expression
import com.dfsek.paralithic.eval.parser.Parser
import com.dfsek.paralithic.eval.parser.Scope
import gay.solonovamax.beaconsoverhaul.config.BeaconConfig.BeaconExpression
import io.github.xn32.json5k.SerialComment
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class ConduitConfig(
    @SerialComment("If conduit effects should show particles.")
    var effectParticles: Boolean,
    @SerialComment(
        """
            An expression to compute the range of the conduit.
            Variables:
            - tier: the tier of the associated conduit.
        """
    )
    var range: ConduitExpression,
) {
    @Serializable(ConduitExpression.Companion::class)
    data class ConduitExpression(
        override val expressionString: String,
    ) : BeaconExpression {
        private val expression: Expression by lazy {
            val parser = Parser()
            val scope = Scope()
            scope.addInvocationVariable("tier")

            parser.parse(expressionString, scope)
        }

        override fun evaluate(vararg args: Double): Double = expression.evaluate(*args)

        companion object : KSerializer<ConduitExpression> {
            override val descriptor = PrimitiveSerialDescriptor(ConduitExpression::class.simpleName!!, PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder) = ConduitExpression(decoder.decodeString())

            override fun serialize(encoder: Encoder, value: ConduitExpression) = encoder.encodeString(value.expressionString)
        }
    }
}

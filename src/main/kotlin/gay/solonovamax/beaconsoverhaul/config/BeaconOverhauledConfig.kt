@file:UseSerializers(BlockSerializer::class, StatusEffectSerializer::class, ResourceLocationSerializer::class)

package gay.solonovamax.beaconsoverhaul.config

import com.dfsek.paralithic.Expression
import com.dfsek.paralithic.eval.parser.Parser
import com.dfsek.paralithic.eval.parser.Scope
import gay.solonovamax.beaconsoverhaul.serialization.BlockSerializer
import gay.solonovamax.beaconsoverhaul.serialization.StatusEffectSerializer
import io.github.xn32.json5k.SerialComment
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.SharedConstants
import net.minecraft.block.Block
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.silkmc.silk.core.serialization.serializers.ResourceLocationSerializer
import kotlin.math.floor
import kotlin.time.Duration

@Serializable
data class BeaconOverhauledConfig(
    @SerialComment(
        """
            List of addition modifiers
            Variables:
            - blocks: the number of blocks of this specific type.
        """
    )
    val additionModifiers: Map<Identifier, BeaconBlockExpression>,
    @SerialComment("List of multiplication modifiers")
    val multiplicationModifiers: Map<Identifier, BeaconBlockExpression>,
    @SerialComment(
        """
            An expression to compute the range of the beacon.
            Variables:
            - pts: the points of the associated beacon.
        """
    )
    val range: BeaconModifierExpression,
    @SerialComment(
        """
            An expression to compute the duration of the beacon.
            Variables:
            - pts: the points of the associated beacon.
        """
    )
    val duration: BeaconModifierExpression,
    @SerialComment(
        """
            An expression to compute the amplifier of the primary effect of the beacon.
            Variables:
            - pts: the points of the associated beacon.
            - isPotent: whether the effect is a potent effect. 1 when the selected secondary effect is for extra potency, 0 otherwise.
        """
    )
    val primaryAmplifier: BeaconEffectAmplifierExpression,
    @SerialComment(
        """
            An expression to compute the amplifier of the secondary effect of the beacon.
            Variables:
            - pts: the points of the associated beacon.
            - isPotent: whether the effect is a potent effect. Always 0.
        """
    )
    val secondaryAmplifier: BeaconEffectAmplifierExpression,
    @SerialComment("The maximum number of layers the beacon can have")
    val maxBeaconLayers: Int,
    @SerialComment("A list of status effects that can never exceed level 1")
    val levelOneStatusEffects: List<StatusEffect>,
    @SerialComment("A list of blocks that can be in the base of the beacon")
    val beaconBaseBlocks: List<Block>,
    @SerialComment("The different tiers of effects")
    val beaconEffectsByTier: BeaconTierEffects,
    @SerialComment(
        """
            The amount of time before the base of the beacon is re-computed. An ISO-8601 representation of a duration.
            TODO: Make this a more reasonable format
        """
    )
    val beaconUpdateDelay: Duration,
    @SerialComment(
        """
            The amount of time before the base of the beacon is initially re-computed. An ISO-8601 representation of a duration.
            TODO: Make this a more reasonable format
        """
    )
    val initialBeaconUpdateDelay: Duration,
    @SerialComment("If beacon effects should show particles.")
    val effectParticles: Boolean,
    @SerialComment("The maximum number of blocks that a beam can be redirected horizontally.")
    val redirectionHorizontalMoveLimit: Int,
    @SerialComment("If tinted glass should make the beacon beam transparent.")
    val allowTintedGlassTransparency: Boolean,
    @SerialComment("The update frequency of the beacon beam, in ticks.")
    val beamUpdateFrequency: Int,
    @SerialComment("The radius of the beacon beam.")
    val beamRadius: Double,
    @SerialComment("The radius of the beacon beam glow.")
    val beamGlowRadius: Double,
    @SerialComment("The opacity of the beacon beam glow.")
    val beamGlowOpacity: Double,
    @SerialComment("The width of the blended area for beacon beam transitions.")
    val beamBlendPadding: Double,
) {
    val additionModifierBlocks: List<Pair<Block, BeaconBlockExpression>> by lazy {
        additionModifiers.entries.filter {
            Registries.BLOCK.containsId(it.key)
        }.map {
            Registries.BLOCK.get(it.key) to it.value
        }
    }

    val multiplicationModifierBlocks: List<Pair<Block, BeaconExpression>> by lazy {
        multiplicationModifiers.filter {
            Registries.BLOCK.containsId(it.key)
        }.map {
            Registries.BLOCK.get(it.key) to it.value
        }
    }

    fun calculateRange(beaconPoints: Double): Int {
        return floor(range.evaluate(beaconPoints)).toInt()
    }

    fun calculateDuration(beaconPoints: Double): Int {
        return floor(duration.evaluate(beaconPoints)).toInt() * SharedConstants.TICKS_PER_SECOND
    }

    fun calculatePrimaryAmplifier(points: Double, isPotent: Boolean): Int {
        return primaryAmplifier.evaluate(points, /* isPotent = */ if (isPotent) 1.0 else 0.0).toInt()
    }

    fun calculateSecondaryAmplifier(points: Double): Int {
        return secondaryAmplifier.evaluate(points, /* isPotent = false */ 0.0).toInt()
    }

    @Serializable
    data class BeaconTierEffects(
        @SerialComment("A list of available effects at tier 1")
        val tierOne: List<StatusEffect>,
        @SerialComment("A list of available effects at tier 2")
        val tierTwo: List<StatusEffect>,
        @SerialComment("A list of available effects at tier 3")
        val tierThree: List<StatusEffect>,
        @SerialComment("A list of available secondary effects")
        val secondaryEffects: List<StatusEffect>,
    ) {
        companion object
    }

    @Serializable
    sealed interface BeaconExpression {
        val expressionString: String
        fun evaluate(vararg args: Double): Double
    }

    @Serializable(BeaconBlockExpression.Companion::class)
    data class BeaconBlockExpression(
        override val expressionString: String,
    ) : BeaconExpression {
        private val expression: Expression by lazy {
            val parser = Parser()
            val scope = Scope()
            scope.addInvocationVariable("blocks")

            parser.parse(expressionString, scope)
        }

        override fun evaluate(vararg args: Double): Double = expression.evaluate(*args)

        companion object : KSerializer<BeaconBlockExpression> {
            override val descriptor = PrimitiveSerialDescriptor(BeaconBlockExpression::class.simpleName!!, PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder) = BeaconBlockExpression(decoder.decodeString())

            override fun serialize(encoder: Encoder, value: BeaconBlockExpression) = encoder.encodeString(value.expressionString)
        }
    }

    @Serializable(BeaconModifierExpression.Companion::class)
    data class BeaconModifierExpression(
        override val expressionString: String,
    ) : BeaconExpression {
        private val expression: Expression by lazy {
            val parser = Parser()
            val scope = Scope()
            scope.addInvocationVariable("pts")

            parser.parse(expressionString, scope)
        }

        override fun evaluate(vararg args: Double): Double = expression.evaluate(*args)

        companion object : KSerializer<BeaconModifierExpression> {
            override val descriptor = PrimitiveSerialDescriptor(BeaconModifierExpression::class.simpleName!!, PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder) = BeaconModifierExpression(decoder.decodeString())

            override fun serialize(encoder: Encoder, value: BeaconModifierExpression) = encoder.encodeString(value.expressionString)
        }
    }

    @Serializable(BeaconEffectAmplifierExpression.Companion::class)
    data class BeaconEffectAmplifierExpression(
        override val expressionString: String,
    ) : BeaconExpression {
        val expression: Expression by lazy {
            val parser = Parser()
            val scope = Scope()
            scope.addInvocationVariable("pts")
            scope.addInvocationVariable("isPotent")

            parser.parse(expressionString, scope)
        }

        override fun evaluate(vararg args: Double): Double = expression.evaluate(*args)

        companion object : KSerializer<BeaconEffectAmplifierExpression> {
            override val descriptor = PrimitiveSerialDescriptor(BeaconBlockExpression::class.simpleName!!, PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder) = BeaconEffectAmplifierExpression(decoder.decodeString())

            override fun serialize(encoder: Encoder, value: BeaconEffectAmplifierExpression) = encoder.encodeString(value.expressionString)
        }
    }
}

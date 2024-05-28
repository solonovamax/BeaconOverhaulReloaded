@file:UseSerializers(BlockSerializer::class, StatusEffectSerializer::class, ResourceLocationSerializer::class)

package gay.solonovamax.beaconsoverhaul.config

import com.dfsek.paralithic.Expression
import com.dfsek.paralithic.eval.parser.Parser
import com.dfsek.paralithic.eval.parser.Scope
import gay.solonovamax.beaconsoverhaul.serialization.BlockSerializer
import gay.solonovamax.beaconsoverhaul.serialization.StatusEffectSerializer
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
    val additionModifiers: Map<Identifier, BeaconBlockExpression>,
    val multiplicationModifiers: Map<Identifier, BeaconBlockExpression>,
    val range: BeaconModifierExpression,
    val duration: BeaconModifierExpression,
    val primaryAmplifier: BeaconEffectAmplifierExpression,
    val secondaryAmplifier: BeaconEffectAmplifierExpression,
    val maxBeaconLayers: Int,
    val levelOneStatusEffects: List<StatusEffect>,
    val beaconBaseBlocks: List<Block>,
    val beaconEffectsByTier: BeaconTierEffects,
    val beaconUpdateDelay: Duration,
    val initialBeaconUpdateDelay: Duration,
    val effectParticles: Boolean,
    val redirectionHorizontalMoveLimit: Int,
    val allowTintedGlassTransparency: Boolean,
    val beamUpdateFrequency: Int,
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
        val tierOne: List<StatusEffect>,
        val tierTwo: List<StatusEffect>,
        val tierThree: List<StatusEffect>,
        val secondaryEffects: List<StatusEffect>,
    ) {
        companion object
    }

    @Serializable
    sealed interface BeaconExpression {
        val expressionString: String
        fun evaluate(vararg args: Double): Double

        companion object
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

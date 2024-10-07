@file:UseSerializers(
    BlockSerializer::class,
    StatusEffectSerializer::class,
    RegistryEntrySerializer::class,
    RegistryEntryListSerializer::class,
    IdentifierSerializer::class
)

package gay.solonovamax.beaconsoverhaul.config

import com.dfsek.paralithic.Expression
import com.dfsek.paralithic.eval.parser.Parser
import com.dfsek.paralithic.eval.parser.Scope
import gay.solonovamax.beaconsoverhaul.serialization.BlockSerializer
import gay.solonovamax.beaconsoverhaul.serialization.RegistryEntryListSerializer
import gay.solonovamax.beaconsoverhaul.serialization.RegistryEntrySerializer
import gay.solonovamax.beaconsoverhaul.serialization.StatusEffectSerializer
import gay.solonovamax.beaconsoverhaul.util.toRegistryEntryList
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
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.entry.RegistryEntryList
import net.minecraft.util.Identifier
import kotlin.math.floor
import kotlin.time.Duration
import net.silkmc.silk.core.serialization.serializers.ResourceLocationSerializer as IdentifierSerializer

@Serializable
data class BeaconConfig(
    @SerialComment(
        """
            List of addition modifiers
            Variables:
            - blocks: the number of blocks of this specific type.
        """
    )
    var additionModifiers: Map<Identifier, BlockExpression>,
    @SerialComment("List of multiplication modifiers")
    var multiplicationModifiers: Map<Identifier, BlockExpression>,
    @SerialComment(
        """
            An expression to compute the range of the beacon.
            Variables:
            - pts: the points of the associated beacon.
        """
    )
    var range: ModifierExpression,
    @SerialComment(
        """
            An expression to compute the duration of the beacon.
            Variables:
            - pts: the points of the associated beacon.
        """
    )
    var duration: ModifierExpression,
    @SerialComment(
        """
            An expression to compute the amplifier of the primary effect of the beacon.
            Variables:
            - pts: the points of the associated beacon.
            - isPotent: whether the effect is a potent effect. 1 when the selected secondary effect is for extra potency, 0 otherwise.
        """
    )
    var primaryAmplifier: AmplifierExpression,
    @SerialComment(
        """
            An expression to compute the amplifier of the secondary effect of the beacon.
            Variables:
            - pts: the points of the associated beacon.
            - isPotent: whether the effect is a potent effect. Always 0.
        """
    )
    var secondaryAmplifier: AmplifierExpression,
    @SerialComment("The maximum number of layers the beacon can have")
    var maxBeaconLayers: Int,
    @SerialComment("A list of status effects that can never exceed level 1")
    var levelOneStatusEffects: RegistryEntryList<StatusEffect>,
    @SerialComment("A list of blocks that can be in the base of the beacon")
    var beaconBaseBlocks: List<Block>,
    @SerialComment("The different tiers of effects")
    var beaconEffectsByTier: BeaconTierEffects,
    @SerialComment(
        """
            The amount of time before the base of the beacon is re-computed. An ISO-8601 representation of a duration.
            TODO: Make this a more reasonable format
        """
    )
    var beaconUpdateDelay: Duration,
    @SerialComment(
        """
            The amount of time before the base of the beacon is initially re-computed. An ISO-8601 representation of a duration.
            TODO: Make this a more reasonable format
        """
    )
    var initialBeaconUpdateDelay: Duration,
    @SerialComment("If beacon effects should show particles.")
    var effectParticles: Boolean,
    @SerialComment("The maximum number of blocks that a beam can be redirected horizontally.")
    var redirectionHorizontalMoveLimit: Int,
    @SerialComment("If tinted glass should make the beacon beam transparent.")
    var allowTintedGlassTransparency: Boolean,
    @SerialComment("The update frequency of the beacon beam, in ticks.")
    var beamUpdateFrequency: Int,
    @SerialComment("The radius of the beacon beam.")
    var beamRadius: Double,
    @SerialComment("The radius of the beacon beam glow.")
    var beamGlowRadius: Double,
    @SerialComment("The opacity of the beacon beam glow.")
    var beamGlowOpacity: Double,
    @SerialComment("The width of the blended area for beacon beam transitions.")
    var beamBlendPadding: Double,
) {

    constructor(
        additionModifiers: Map<Identifier, String>,
        multiplicationModifiers: Map<Identifier, String>,
        range: String,
        duration: String,
        primaryAmplifier: String,
        secondaryAmplifier: String,
        maxBeaconLayers: Int,
        levelOneStatusEffects: List<RegistryEntry<StatusEffect>>,
        beaconBaseBlocks: List<Block>,
        beaconEffectsByTier: BeaconTierEffects,
        beaconUpdateDelay: Duration,
        initialBeaconUpdateDelay: Duration,
        effectParticles: Boolean,
        redirectionHorizontalMoveLimit: Int,
        allowTintedGlassTransparency: Boolean,
        beamUpdateFrequency: Int,
        beamRadius: Double,
        beamGlowRadius: Double,
        beamGlowOpacity: Double,
        beamBlendPadding: Double,
    ) : this(
        additionModifiers.mapValues { BlockExpression(it.value) },
        multiplicationModifiers.mapValues { BlockExpression(it.value) },
        ModifierExpression(range),
        ModifierExpression(duration),
        AmplifierExpression(primaryAmplifier),
        AmplifierExpression(secondaryAmplifier),
        maxBeaconLayers,
        levelOneStatusEffects.toRegistryEntryList(),
        beaconBaseBlocks,
        beaconEffectsByTier,
        beaconUpdateDelay,
        initialBeaconUpdateDelay,
        effectParticles,
        redirectionHorizontalMoveLimit,
        allowTintedGlassTransparency,
        beamUpdateFrequency,
        beamRadius,
        beamGlowRadius,
        beamGlowOpacity,
        beamBlendPadding
    )

    val additionModifierBlocks: List<Pair<Block, BlockExpression>> by lazy {
        additionModifiers.entries.filter {
            Registries.BLOCK.containsId(it.key)
        }.map {
            Registries.BLOCK.get(it.key) to it.value
        }
    }

    val multiplicationModifierBlocks: List<Pair<Block, BlockExpression>> by lazy {
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
        var tierOne: RegistryEntryList<StatusEffect>,
        @SerialComment("A list of available effects at tier 2")
        var tierTwo: RegistryEntryList<StatusEffect>,
        @SerialComment("A list of available effects at tier 3")
        var tierThree: RegistryEntryList<StatusEffect>,
        @SerialComment("A list of available secondary effects")
        var secondaryEffects: RegistryEntryList<StatusEffect>,
    )

    @Serializable
    sealed interface BeaconExpression {
        val expressionString: String
        fun evaluate(vararg args: Double): Double
    }

    @Serializable(BlockExpression.Companion::class)
    data class BlockExpression(
        override val expressionString: String,
    ) : BeaconExpression {
        private val expression: Expression by lazy {
            val parser = Parser()
            val scope = Scope()
            scope.addInvocationVariable("blocks")

            parser.parse(expressionString, scope)
        }

        override fun evaluate(vararg args: Double): Double = expression.evaluate(*args)

        companion object : KSerializer<BlockExpression> {
            override val descriptor = PrimitiveSerialDescriptor(BlockExpression::class.simpleName!!, PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder) = BlockExpression(decoder.decodeString())

            override fun serialize(encoder: Encoder, value: BlockExpression) = encoder.encodeString(value.expressionString)
        }
    }

    @Serializable(ModifierExpression.Companion::class)
    data class ModifierExpression(
        override val expressionString: String,
    ) : BeaconExpression {
        private val expression: Expression by lazy {
            val parser = Parser()
            val scope = Scope()
            scope.addInvocationVariable("pts")

            parser.parse(expressionString, scope)
        }

        override fun evaluate(vararg args: Double): Double = expression.evaluate(*args)

        companion object : KSerializer<ModifierExpression> {
            override val descriptor = PrimitiveSerialDescriptor(ModifierExpression::class.simpleName!!, PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder) = ModifierExpression(decoder.decodeString())

            override fun serialize(encoder: Encoder, value: ModifierExpression) = encoder.encodeString(value.expressionString)
        }
    }

    @Serializable(AmplifierExpression.Companion::class)
    data class AmplifierExpression(
        override val expressionString: String,
    ) : BeaconExpression {
        private val expression: Expression by lazy {
            val parser = Parser()
            val scope = Scope()
            scope.addInvocationVariable("pts")
            scope.addInvocationVariable("isPotent")

            parser.parse(expressionString, scope)
        }

        override fun evaluate(vararg args: Double): Double = expression.evaluate(*args)

        companion object : KSerializer<AmplifierExpression> {
            override val descriptor = PrimitiveSerialDescriptor(BlockExpression::class.simpleName!!, PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder) = AmplifierExpression(decoder.decodeString())

            override fun serialize(encoder: Encoder, value: AmplifierExpression) = encoder.encodeString(value.expressionString)
        }
    }
}

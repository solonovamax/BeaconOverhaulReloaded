package gay.solonovamax.beaconsoverhaul.config

import com.dfsek.paralithic.Expression
import com.dfsek.paralithic.eval.parser.Parser
import com.dfsek.paralithic.eval.parser.Scope
import net.minecraft.SharedConstants
import net.minecraft.block.Block
import net.minecraft.entity.effect.StatusEffect
import kotlin.math.floor

class BeaconOverhauledConfig private constructor(
    val additionModifiers: List<Pair<Block, Expression>>,
    val multiplicationModifiers: List<Pair<Block, Expression>>,
    val rangeExpression: Expression,
    val durationExpression: Expression,
    val primaryAmplifierExpression: Expression,
    val secondaryAmplifierExpression: Expression,
    val maxBeaconLayers: Int,
    val levelOneStatusEffects: List<StatusEffect>,
    val beaconBaseBlocks: List<Block>,
    val beaconEffectsByTier: SerializedBeaconOverhauledConfig.BeaconTierEffects,
) {
    fun calculateRange(beaconPoints: Double): Int {
        return floor(rangeExpression.evaluate(beaconPoints)).toInt()
    }

    fun calculateDuration(beaconPoints: Double): Int {
        return floor(durationExpression.evaluate(beaconPoints)).toInt() * SharedConstants.TICKS_PER_SECOND
    }

    fun calculatePrimaryAmplifier(points: Double, isPotent: Boolean): Int {
        @Suppress("InconsistentCommentForJavaParameter")
        return primaryAmplifierExpression.evaluate(points, /* isPotent = */ if (isPotent) 1.0 else 0.0).toInt()
    }

    fun calculateSecondaryAmplifier(points: Double): Int {
        return secondaryAmplifierExpression.evaluate(points, 0.0 /* isPotent = false */).toInt()
    }

    companion object {
        fun from(config: SerializedBeaconOverhauledConfig): BeaconOverhauledConfig {
            val parser = Parser()

            val blockModifierParserScope = Scope()
            blockModifierParserScope.addInvocationVariable("blocks")

            val additionModifiers = config.pointsModifiers.filter {
                it.operation == SerializedBeaconOverhauledConfig.AttributeModifier.Operation.ADDITION
            }.map {
                it.block to parser.parse(it.expression, blockModifierParserScope)
            }
            val multiplicationModifiers = config.pointsModifiers.filter {
                it.operation == SerializedBeaconOverhauledConfig.AttributeModifier.Operation.MULTIPLICATION
            }.map {
                it.block to parser.parse(it.expression, blockModifierParserScope)
            }

            val totalsParserScope = Scope()
            totalsParserScope.addInvocationVariable("pts")

            val range = parser.parse(config.range, totalsParserScope)
            val duration = parser.parse(config.duration, totalsParserScope)

            val amplifierExpressionScope = Scope()
            amplifierExpressionScope.addInvocationVariable("pts")
            amplifierExpressionScope.addInvocationVariable("isPotent")

            val primaryAmplifier = parser.parse(config.primaryAmplifier, amplifierExpressionScope)
            val secondaryAmplifier = parser.parse(config.secondaryAmplifier, amplifierExpressionScope);

            return BeaconOverhauledConfig(
                additionModifiers,
                multiplicationModifiers,
                range,
                duration,
                primaryAmplifier,
                secondaryAmplifier,
                config.maxBeaconLayers,
                config.levelOneStatusEffects,
                config.beaconBaseBlocks,
                config.beaconEffectsByTier,
            )
        }
    }
}

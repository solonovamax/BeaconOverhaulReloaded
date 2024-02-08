package gay.solonovamax.beaconsoverhaul.config

import com.dfsek.paralithic.Expression
import com.dfsek.paralithic.eval.parser.Parser
import com.dfsek.paralithic.eval.parser.Scope
import net.minecraft.SharedConstants
import net.minecraft.block.Block
import net.minecraft.entity.effect.StatusEffect
import kotlin.math.floor

class BeaconOverhauledConfig private constructor(
    val additionModifiers: List<Pair<Block, BeaconExpression>>,
    val multiplicationModifiers: List<Pair<Block, BeaconExpression>>,
    val rangeExpression: BeaconExpression,
    val durationExpression: BeaconExpression,
    val primaryAmplifierExpression: BeaconExpression,
    val secondaryAmplifierExpression: BeaconExpression,
    val maxBeaconLayers: Int,
    val levelOneStatusEffects: List<StatusEffect>,
    val beaconBaseBlocks: List<Block>,
    val beaconEffectsByTier: SerializedBeaconOverhauledConfig.BeaconTierEffects,
    val beaconUpdateDelayTicks: Int,
    val beaconQuickCheckDelayTicks: Int,
) {
    fun calculateRange(beaconPoints: Double): Int {
        return floor(rangeExpression.evaluate(beaconPoints)).toInt()
    }

    fun calculateDuration(beaconPoints: Double): Int {
        return floor(durationExpression.evaluate(beaconPoints)).toInt() * SharedConstants.TICKS_PER_SECOND
    }

    fun calculatePrimaryAmplifier(points: Double, isPotent: Boolean): Int {
        return primaryAmplifierExpression.evaluate(points, /* isPotent = */ if (isPotent) 1.0 else 0.0).toInt()
    }

    fun calculateSecondaryAmplifier(points: Double): Int {
        return secondaryAmplifierExpression.evaluate(points, /* isPotent = false */ 0.0).toInt()
    }

    data class BeaconExpression(
        val expression: Expression,
        val expressionString: String,
    ) {
        fun evaluate(vararg args: Double): Double = expression.evaluate(*args)
    }

    companion object {
        fun from(config: SerializedBeaconOverhauledConfig): BeaconOverhauledConfig {
            val parser = Parser()

            val blockModifiersScope = Scope()
            blockModifiersScope.addInvocationVariable("blocks")

            val additionModifiers = config.pointsModifiers.filter {
                it.operation == SerializedBeaconOverhauledConfig.AttributeModifier.Operation.ADDITION
            }.map {
                it.block to BeaconExpression(parser.parse(it.expression, blockModifiersScope), it.expression)
            }
            val multiplicationModifiers = config.pointsModifiers.filter {
                it.operation == SerializedBeaconOverhauledConfig.AttributeModifier.Operation.MULTIPLICATION
            }.map {
                it.block to BeaconExpression(parser.parse(it.expression, blockModifiersScope), it.expression)
            }

            val totalsScope = Scope()
            totalsScope.addInvocationVariable("pts")

            val range = BeaconExpression(parser.parse(config.range, totalsScope), config.range)
            val duration = BeaconExpression(parser.parse(config.duration, totalsScope), config.duration)

            val amplifierScope = Scope()
            amplifierScope.addInvocationVariable("pts")
            amplifierScope.addInvocationVariable("isPotent")

            val primaryAmplifier = BeaconExpression(parser.parse(config.primaryAmplifier, amplifierScope), config.primaryAmplifier)
            val secondaryAmplifier = BeaconExpression(parser.parse(config.secondaryAmplifier, amplifierScope), config.secondaryAmplifier)

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
                config.beaconUpdateDelayTicks,
                config.beaconQuickCheckDelayTicks,
            )
        }
    }
}

package gay.solonovamax.beaconsoverhaul.block.conduit

import gay.solonovamax.beaconsoverhaul.util.identifierOf
import io.wispforest.lavender.structure.LavenderStructures
import io.wispforest.lavender.structure.StructureTemplate
import net.minecraft.util.Identifier

sealed interface ConduitTier {
    val ordinal: Int
    val structureId: Identifier

    fun next(): NonZeroConduitTier = forTier(ordinal + 1) as NonZeroConduitTier
    fun isLast(): Boolean = next().nullableStructure() == null

    data class NonZeroConduitTier(
        override val ordinal: Int,
        override val structureId: Identifier,
    ) : ConduitTier {
        val structure: StructureTemplate by lazy {
            LavenderStructures.get(structureId)!!
        }

        fun nullableStructure() = LavenderStructures.get(structureId)
    }

    data object ZeroConduitTier : ConduitTier {
        override val ordinal: Int = 0
        override val structureId: Identifier = identifierOf(STRUCTURE_ID_FORMAT.format(0))
    }

    companion object {
        private const val STRUCTURE_ID_FORMAT = "conduit/tier_%s"
        fun forTier(tier: Int): ConduitTier {
            return if (tier == 0)
                ZeroConduitTier
            else
                NonZeroConduitTier(tier, identifierOf(STRUCTURE_ID_FORMAT.format(tier)))
        }
    }

}

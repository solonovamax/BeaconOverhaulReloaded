package gay.solonovamax.beaconsoverhaul.integration.lavender

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.mojang.brigadier.exceptions.CommandSyntaxException
import gay.solonovamax.beaconsoverhaul.util.contains
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import io.wispforest.lavender.structure.BlockStatePredicate.MatchCategory
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.registry.entry.RegistryEntryList
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.state.property.Property
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World
import net.silkmc.silk.core.math.vector.minus
import net.silkmc.silk.core.math.vector.plus
import io.wispforest.lavender.structure.BlockStatePredicate as LavenderBlockStatePredicate


@JvmField
val EMPTY_STRUCTURE_TEMPLATE = LavenderStructureTemplate(identifierOf("empty"), arrayOf(), 0, 0, 0, Vec3i.ZERO)

fun beaconStructureIdentifier(tier: Int) = identifierOf("beacon/tier_$tier")

fun createBeaconStructureTemplate(tier: Int): LavenderStructureTemplate {
    val size = tier * 2 + 1

    val jsonString = buildJsonObject {
        putJsonObject("keys") {
            put("b", "minecraft:beacon")
            put("B", "#minecraft:beacon_base_blocks")
            put("anchor", "#minecraft:beacon_base_blocks")
        }
        putJsonArray("layers") {
            for (level in tier downTo 0) {
                addJsonArray {
                    when (level) {
                        0 -> {
                            repeat(tier) { add(" ".repeat(size)) }
                            add(" ".repeat(tier) + "b" + " ".repeat(tier))
                            repeat(tier) { add(" ".repeat(size)) }
                        }

                        tier -> {
                            repeat(tier) { add("B".repeat(size)) }
                            add("B".repeat(tier) + "#" + "B".repeat(tier))
                            repeat(tier) { add("B".repeat(size)) }
                        }

                        else -> {
                            repeat(tier - level) { add(" ".repeat(size)) }
                            repeat(level * 2 + 1) {
                                add(" ".repeat(tier - level) + "B".repeat(level * 2 + 1) + " ".repeat(tier - level))
                            }
                            repeat(tier - level) { add(" ".repeat(size)) }
                        }
                    }

                }
            }
        }
    }.toString()

    return parseStructureTemplate(beaconStructureIdentifier(tier), JsonParser.parseString(jsonString).asJsonObject)
}

@Suppress("UNCHECKED_CAST")
fun parseStructureTemplate(resourceId: Identifier, json: JsonObject): LavenderStructureTemplate {
    val keyObject = JsonHelper.getObject(json, "keys")
    val keys = Char2ObjectOpenHashMap<BlockStatePredicate>()
    var anchor: Vec3i? = null

    for ((k, predicate) in keyObject.entrySet()) {
        var key: Char
        when {
            k.length == 1 -> {
                key = k[0]
                if (key == '#') {
                    throw JsonParseException("Key '#' is reserved for 'anchor' declarations")
                }
            }

            k == "anchor" -> key = '#'
            else -> continue
        }

        try {
            when {
                predicate.isJsonArray -> {
                    predicate as JsonArray
                    val predicates = predicate.map { nested ->
                        when (val result = StructureBlockParser.blockOrTag(Registries.BLOCK.readOnlyWrapper, nested.asString)) {
                            is StructureBlockParser.BlockResult -> {
                                LavenderBlockPredicate(result.blockState, result.properties)
                            }

                            is StructureBlockParser.TagResult -> {
                                LavenderTagStatePredicate(result.tag, result.vagueProperties)
                            }

                            is StructureBlockParser.AnyBlockResult -> {
                                LavenderFuzzyBlockPredicate(result.previewState, result.vagueProperties)
                            }
                        }
                    }
                    keys[key] = LavenderNestedBlockStatePredicate(predicates)
                }

                predicate.isJsonPrimitive -> {
                    keys[key] = when (val result = StructureBlockParser.blockOrTag(Registries.BLOCK.readOnlyWrapper, predicate.asString)) {
                        is StructureBlockParser.BlockResult -> {
                            LavenderBlockPredicate(result.blockState, result.properties)
                        }

                        is StructureBlockParser.TagResult -> {
                            LavenderTagStatePredicate(result.tag, result.vagueProperties)
                        }

                        is StructureBlockParser.AnyBlockResult -> {
                            LavenderFuzzyBlockPredicate(result.previewState, result.vagueProperties)
                        }
                    }
                }
            }
        } catch (e: CommandSyntaxException) {
            throw JsonParseException("Failed to parse block state predicate", e)
        }
    }

    val layersArray = JsonHelper.getArray(json, "layers")
    var xSize = 0
    val ySize = layersArray.size()
    var zSize = 0

    for (element in layersArray) {
        if (element !is JsonArray) {
            throw JsonParseException("Every element in the 'layers' array must itself be an array")
        }

        if (zSize == 0) {
            zSize = element.size()
        } else if (zSize != element.size()) {
            throw JsonParseException("Every layer must have the same amount of rows")
        }

        for (rowElement in element) {
            if (!rowElement.isJsonPrimitive) {
                throw JsonParseException("Every element in a row must be a primitive")
            }
            if (xSize == 0) {
                xSize = rowElement.asString.length
            } else if (xSize != rowElement.asString.length) {
                throw JsonParseException("Every row must have the same length")
            }
        }
    }

    val result = arrayOfNulls<Array<Array<BlockStatePredicate>>>(xSize) as Array<Array<Array<BlockStatePredicate>>>
    for (x in 0 until xSize) {
        result[x] = arrayOfNulls<Array<BlockStatePredicate>>(ySize) as Array<Array<BlockStatePredicate>>
        for (y in 0 until ySize) {
            result[x][y] = arrayOfNulls<BlockStatePredicate>(zSize) as Array<BlockStatePredicate>
        }
    }

    for (y in 0 until layersArray.size()) {
        val layer: JsonArray = layersArray[y] as JsonArray
        for (z in 0 until layer.size()) {
            val row = layer.get(z).asString
            for (x in row.indices) {
                val key = row[x]
                val predicate = when {
                    keys.containsKey(key) -> {
                        if (key == '#') {
                            if (anchor != null)
                                throw JsonParseException("Anchor key '#' cannot be used twice within the same structure")

                            anchor = Vec3i(x, y, z)
                        }

                        keys[key]
                    }

                    key == ' ' -> NULL_PREDICATE

                    key == '_' -> AIR_PREDICATE

                    else -> throw JsonParseException("Unknown key '$key'")
                }

                result[x][y][z] = predicate
            }
        }
    }

    return LavenderStructureTemplate(resourceId, result, xSize, ySize, zSize, anchor)
}

fun LavenderStructureTemplate.tryPlaceNextMatching(state: BlockState, pos: BlockPos, world: World): Boolean {
    val realPos = BlockPos.Mutable()
    for ((predicate, localPos) in this) {
        realPos.set(pos + localPos - anchor)

        if (predicate.test(state) != LavenderBlockStatePredicate.Result.STATE_MATCH)
            continue

        val currentState = world.getBlockState(realPos)

        if (predicate.test(currentState) == LavenderBlockStatePredicate.Result.STATE_MATCH)
            continue

        val targetState = world.getBlockState(realPos)
        if (!targetState.isAir && !targetState.isReplaceable && targetState !in BlockTags.FIRE && targetState.fluidState.isEmpty)
            continue

        world.setBlockState(realPos, state)
        return true
    }
    return false
}

val NULL_PREDICATE: BlockStatePredicate = object : BlockStatePredicate {
    override val previewStates: List<BlockState> = listOf()

    override fun preview(): BlockState {
        return Blocks.AIR.defaultState
    }

    override fun test(blockState: BlockState?): LavenderBlockStatePredicate.Result {
        return LavenderBlockStatePredicate.Result.STATE_MATCH
    }

    override fun isOf(type: MatchCategory): Boolean {
        return type == MatchCategory.ANY || type == MatchCategory.NULL
    }
}

val AIR_PREDICATE: BlockStatePredicate = object : BlockStatePredicate {
    override val previewStates: List<BlockState> = listOf(Blocks.AIR.defaultState)

    override fun preview(): BlockState {
        return Blocks.AIR.defaultState
    }

    override fun test(blockState: BlockState): LavenderBlockStatePredicate.Result {
        return if (blockState.isAir) LavenderBlockStatePredicate.Result.STATE_MATCH else LavenderBlockStatePredicate.Result.NO_MATCH
    }

    override fun isOf(type: MatchCategory): Boolean {
        return type == MatchCategory.ANY || type == MatchCategory.NON_NULL || type == MatchCategory.AIR
    }
}

interface BlockStatePredicate : LavenderBlockStatePredicate {
    val previewStates: List<BlockState>

    override fun preview(): BlockState {
        return when {
            previewStates.isEmpty() -> Blocks.AIR.defaultState
            previewStates.size == 1 -> previewStates.first()
            else -> previewStates[(System.currentTimeMillis() / 1000 % previewStates.size).toInt()]
        }
    }
}

data class LavenderNestedBlockStatePredicate(
    val predicates: List<BlockStatePredicate>,
) : BlockStatePredicate {
    override val previewStates: List<BlockState> = buildList {
        for (predicate in predicates) {
            addAll(predicate.previewStates)
        }
    }

    override fun test(state: BlockState): LavenderBlockStatePredicate.Result {
        var hasBlockMatch = false
        for (predicate in predicates) {
            when (val result = predicate.test(state)) {
                LavenderBlockStatePredicate.Result.STATE_MATCH -> return result
                LavenderBlockStatePredicate.Result.BLOCK_MATCH -> hasBlockMatch = true
                LavenderBlockStatePredicate.Result.NO_MATCH -> {}
            }
        }

        return if (hasBlockMatch)
            LavenderBlockStatePredicate.Result.BLOCK_MATCH
        else
            LavenderBlockStatePredicate.Result.NO_MATCH
    }
}

data class LavenderBlockPredicate(
    val state: BlockState,
    val properties: Map<Property<*>, Comparable<*>> = mapOf(),
) : BlockStatePredicate {
    override val previewStates: List<BlockState> = listOf(state)

    override fun test(state: BlockState): LavenderBlockStatePredicate.Result {
        if (state.block !== this.state.block)
            return LavenderBlockStatePredicate.Result.NO_MATCH

        for ((property, value) in properties.entries) {
            if (state[property] != value)
                return LavenderBlockStatePredicate.Result.BLOCK_MATCH
        }

        return LavenderBlockStatePredicate.Result.STATE_MATCH
    }
}

data class LavenderFuzzyBlockPredicate(
    val state: BlockState,
    val vagueProperties: Map<String, String> = mapOf(),
) : BlockStatePredicate {
    override val previewStates: List<BlockState> = listOf(state)

    override fun test(state: BlockState): LavenderBlockStatePredicate.Result {
        for ((vagueProperty, value) in vagueProperties.entries) {
            val prop = state.block.stateManager.getProperty(vagueProperty) ?: return LavenderBlockStatePredicate.Result.NO_MATCH

            val expected = prop.parse(value)
            if (expected.isEmpty)
                return LavenderBlockStatePredicate.Result.NO_MATCH

            if (state[prop] != expected.get())
                return LavenderBlockStatePredicate.Result.NO_MATCH
        }

        return LavenderBlockStatePredicate.Result.STATE_MATCH
    }
}

data class LavenderTagStatePredicate(
    val tagEntries: RegistryEntryList.Named<Block>,
    val vagueProperties: Map<String, String> = mapOf(),
) : BlockStatePredicate {
    val tag: TagKey<Block>
        get() = tagEntries.tag

    override val previewStates: List<BlockState> = buildList {
        for (entry in tagEntries) {
            var state = entry.value().defaultState

            for (propAndValue in vagueProperties.entries) {
                @Suppress("UNCHECKED_CAST")
                val prop = entry.value().stateManager.getProperty(propAndValue.key) as Property<Comparable<Any>>? ?: continue

                val value = prop.parse(propAndValue.value)
                if (value.isEmpty)
                    continue

                state = state.with(prop, value.get())
            }

            add(state)
        }
    }

    override fun test(state: BlockState): LavenderBlockStatePredicate.Result {
        if (state !in tag)
            return LavenderBlockStatePredicate.Result.NO_MATCH

        for ((vagueProperty, value) in vagueProperties.entries) {
            val prop = state.block.stateManager.getProperty(vagueProperty) ?: return LavenderBlockStatePredicate.Result.BLOCK_MATCH

            val expected = prop.parse(value)
            if (expected.isEmpty)
                return LavenderBlockStatePredicate.Result.BLOCK_MATCH

            if (state[prop] != expected.get())
                return LavenderBlockStatePredicate.Result.BLOCK_MATCH
        }

        return LavenderBlockStatePredicate.Result.STATE_MATCH
    }
}

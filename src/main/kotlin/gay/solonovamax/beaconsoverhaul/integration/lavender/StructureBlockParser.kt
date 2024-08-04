package gay.solonovamax.beaconsoverhaul.integration.lavender

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.entry.RegistryEntryList
import net.minecraft.registry.tag.TagKey
import net.minecraft.state.StateManager
import net.minecraft.state.property.Property
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object StructureBlockParser {
    private val INVALID_BLOCK_ID_EXCEPTION: DynamicCommandExceptionType = DynamicCommandExceptionType { block ->
        Text.translatable("argument.block.id.invalid", block)
    }
    private val UNKNOWN_PROPERTY_EXCEPTION = Dynamic2CommandExceptionType { block, property ->
        Text.translatable("argument.block.property.unknown", block, property)
    }
    private val DUPLICATE_PROPERTY_EXCEPTION = Dynamic2CommandExceptionType { block, property ->
        Text.translatable("argument.block.property.duplicate", property, block)
    }
    private val INVALID_PROPERTY_EXCEPTION = Dynamic3CommandExceptionType { block, property, value ->
        Text.translatable("argument.block.property.invalid", block, value, property)
    }
    private val EMPTY_PROPERTY_EXCEPTION = Dynamic2CommandExceptionType { block, property ->
        Text.translatable("argument.block.property.novalue", block, property)
    }
    private val UNCLOSED_PROPERTIES_EXCEPTION = SimpleCommandExceptionType(
        Text.translatable("argument.block.property.unclosed")
    )
    private val UNKNOWN_BLOCK_TAG_EXCEPTION = DynamicCommandExceptionType { tag ->
        Text.translatable("arguments.block.tag.unknown", tag)
    }
    private const val PROPERTIES_OPENING = '['
    private const val PROPERTIES_CLOSING = ']'
    private const val PROPERTY_DEFINER = '='
    private const val PROPERTY_SEPARATOR = ','
    private const val TAG_PREFIX = '#'
    private const val GLOB_PREFIX = '*'
    private const val DEFAULT_FUZZY_BLOCK_SEPARATOR = '/'

    fun block(registryWrapper: RegistryWrapper<Block>, string: String): BlockResult {
        return block(registryWrapper, StringReader(string))
    }

    fun block(registryWrapper: RegistryWrapper<Block>, reader: StringReader): BlockResult {
        val cursor = reader.cursor

        try {
            return parseBlock(registryWrapper, reader)
        } catch (e: CommandSyntaxException) {
            reader.cursor = cursor
            throw e
        }
    }

    fun blockOrTag(registryWrapper: RegistryWrapper<Block>, string: String): ParsedResult {
        return blockOrTag(registryWrapper, StringReader(string))
    }

    fun blockOrTag(registryWrapper: RegistryWrapper<Block>, reader: StringReader): ParsedResult {
        val cursor = reader.cursor

        try {
            return parse(registryWrapper, reader)
        } catch (e: CommandSyntaxException) {
            reader.cursor = cursor
            throw e
        }
    }

    private fun parse(registryWrapper: RegistryWrapper<Block>, reader: StringReader): ParsedResult {
        return if (reader.canRead() && reader.peek() == TAG_PREFIX) {
            parseTag(registryWrapper, reader)
        } else if (reader.canRead() && reader.peek() == GLOB_PREFIX) {
            parseAnyBlock(registryWrapper, reader)
        } else {
            parseBlock(registryWrapper, reader)
        }
    }

    private fun parseTag(registryWrapper: RegistryWrapper<Block>, reader: StringReader): TagResult {
        val tagId = parseTagId(registryWrapper, reader)
        val fuzzyProperties = if (reader.canRead() && reader.peek() == PROPERTIES_OPENING)
            parseFuzzyProperties(reader)
        else
            mapOf()

        return TagResult(tagId, fuzzyProperties)
    }

    private fun parseAnyBlock(registryWrapper: RegistryWrapper<Block>, reader: StringReader): AnyBlockResult {
        val cursor = reader.cursor
        reader.expect(GLOB_PREFIX)

        val fuzzyProperties = if (reader.canRead() && reader.peek() == PROPERTIES_OPENING)
            parseFuzzyProperties(reader)
        else
            mapOf()

        reader.expect(DEFAULT_FUZZY_BLOCK_SEPARATOR)
        val blockId = Identifier.fromCommandInput(reader)
        val block = registryWrapper.getOptional(RegistryKey.of(RegistryKeys.BLOCK, blockId)).orElseThrow {
            reader.cursor = cursor
            INVALID_BLOCK_ID_EXCEPTION.createWithContext(reader, blockId.toString())
        }.value()


        return AnyBlockResult(block.defaultState, fuzzyProperties)
    }

    private fun parseBlock(registryWrapper: RegistryWrapper<Block>, reader: StringReader): BlockResult {
        val cursor = reader.cursor
        val blockId = Identifier.fromCommandInput(reader)
        val block = registryWrapper.getOptional(RegistryKey.of(RegistryKeys.BLOCK, blockId)).orElseThrow {
            reader.cursor = cursor
            INVALID_BLOCK_ID_EXCEPTION.createWithContext(reader, blockId.toString())
        }.value()
        val stateFactory = block.stateManager
        val defaultState = block.defaultState

        val (blockState, blockProperties) = if (reader.canRead() && reader.peek() == PROPERTIES_OPENING)
            parseBlockProperties(reader, defaultState, stateFactory, blockId)
        else
            defaultState to mapOf()

        return BlockResult(blockState, blockProperties)
    }

    private fun parseTagId(registryWrapper: RegistryWrapper<Block>, reader: StringReader): RegistryEntryList.Named<Block> {
        val cursor = reader.cursor
        reader.expect(TAG_PREFIX)
        val identifier = Identifier.fromCommandInput(reader)
        return registryWrapper.getOptional(TagKey.of(RegistryKeys.BLOCK, identifier)).orElseThrow {
            reader.cursor = cursor
            UNKNOWN_BLOCK_TAG_EXCEPTION.createWithContext(reader, identifier.toString())
        }
    }

    private fun parseBlockProperties(
        reader: StringReader,
        state: BlockState,
        stateFactory: StateManager<Block, BlockState>,
        blockId: Identifier,
    ): Pair<BlockState, Map<Property<*>, Comparable<*>>> {
        reader.skip()
        reader.skipWhitespace()

        var blockState = state
        val blockProperties = mutableMapOf<Property<*>, Comparable<*>>()
        while (reader.canRead() && reader.peek() != PROPERTIES_CLOSING) {
            reader.skipWhitespace()
            val cursor = reader.cursor
            val string = reader.readString()
            val property = stateFactory.getProperty(string)
            if (property == null) {
                reader.cursor = cursor
                throw UNKNOWN_PROPERTY_EXCEPTION.createWithContext(reader, blockId.toString(), string)
            }

            if (blockProperties.containsKey(property)) {
                reader.cursor = cursor
                throw DUPLICATE_PROPERTY_EXCEPTION.createWithContext(reader, blockId.toString(), string)
            }

            reader.skipWhitespace()
            if (!reader.canRead() || reader.peek() != PROPERTY_DEFINER) {
                throw EMPTY_PROPERTY_EXCEPTION.createWithContext(reader, blockId.toString(), string)
            }

            reader.skip()
            reader.skipWhitespace()
            blockState = parsePropertyValue(reader, blockState, blockProperties, property, reader.readString(), reader.cursor)
            reader.skipWhitespace()
            if (reader.canRead()) {
                if (reader.peek() != PROPERTY_SEPARATOR) {
                    if (reader.peek() != PROPERTIES_CLOSING) {
                        throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext(reader)
                    }
                    break
                }

                reader.skip()
            }
        }

        if (reader.canRead()) {
            reader.skip()
        } else {
            throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext(reader)
        }

        return blockState to blockProperties
    }

    private fun parseFuzzyProperties(reader: StringReader): MutableMap<String, String> {
        reader.skip()
        var pos = -1
        reader.skipWhitespace()

        val fuzzyProperties = mutableMapOf<String, String>()

        while (reader.canRead() && reader.peek() != PROPERTIES_CLOSING) {
            reader.skipWhitespace()
            val cursor = reader.cursor
            val string = reader.readString()
            if (fuzzyProperties.containsKey(string)) {
                reader.cursor = cursor
                throw DUPLICATE_PROPERTY_EXCEPTION.createWithContext(reader, "", string)
            }

            reader.skipWhitespace()
            if (!reader.canRead() || reader.peek() != PROPERTY_DEFINER) {
                reader.cursor = cursor
                throw EMPTY_PROPERTY_EXCEPTION.createWithContext(reader, "", string)
            }

            reader.skip()
            reader.skipWhitespace()
            pos = reader.cursor
            val property = reader.readString()
            fuzzyProperties[string] = property
            reader.skipWhitespace()
            if (reader.canRead()) {
                pos = -1
                if (reader.peek() != PROPERTY_SEPARATOR) {
                    if (reader.peek() != PROPERTIES_CLOSING) {
                        throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext(reader)
                    }
                    break
                }

                reader.skip()
            }
        }

        if (reader.canRead()) {
            reader.skip()
        } else {
            if (pos >= 0) {
                reader.cursor = pos
            }

            throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext(reader)
        }

        return fuzzyProperties
    }

    private fun <T : Comparable<T>?> parsePropertyValue(
        reader: StringReader,
        state: BlockState,
        blockProperties: MutableMap<Property<*>, Comparable<*>>,
        property: Property<T>,
        value: String,
        cursor: Int,
    ): BlockState {
        val optional = property.parse(value)
        if (optional.isPresent) {
            val blockState = state.with<T, T>(property, optional.get())
            blockProperties[property] = optional.get() as Comparable<*>
            return blockState
        } else {
            reader.cursor = cursor
            throw INVALID_PROPERTY_EXCEPTION.createWithContext(reader, "", property.name, value)
        }
    }

    sealed interface ParsedResult

    data class BlockResult(val blockState: BlockState, val properties: Map<Property<*>, Comparable<*>>) : ParsedResult

    data class TagResult(val tag: RegistryEntryList.Named<Block>, val vagueProperties: Map<String, String>) : ParsedResult

    data class AnyBlockResult(val previewState: BlockState, val vagueProperties: Map<String, String>) : ParsedResult
}

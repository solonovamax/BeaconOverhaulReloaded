package gay.solonovamax.beaconsoverhaul.integration.lavender

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import gay.solonovamax.beaconsoverhaul.BeaconConstants.NAMESPACE
import gay.solonovamax.beaconsoverhaul.util.childById
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import gay.solonovamax.beaconsoverhaul.util.template
import io.wispforest.lavender.md.compiler.BookCompiler
import io.wispforest.lavendermd.Lexer
import io.wispforest.lavendermd.Lexer.LexFunction
import io.wispforest.lavendermd.MarkdownFeature
import io.wispforest.lavendermd.Parser
import io.wispforest.lavendermd.compiler.MarkdownCompiler
import io.wispforest.lavendermd.compiler.OwoUICompiler
import io.wispforest.owo.ui.core.ParentComponent
import net.minecraft.entity.boss.WitherEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.StringNbtReader
import net.minecraft.util.Identifier

class EntityModelFeature(
    private val componentSource: BookCompiler.ComponentSource,
) : MarkdownFeature {

    override fun name(): String = "$NAMESPACE:entity"

    override fun supportsCompiler(compiler: MarkdownCompiler<*>?): Boolean = compiler is OwoUICompiler

    override fun registerTokens(registrar: MarkdownFeature.TokenRegistrar) {
        registrar.registerToken(structureLexer("$NAMESPACE:entity"), '<')
    }

    private fun structureLexer(token: String): LexFunction {
        return LexFunction { nibbler, tokens ->
            if (!nibbler.tryConsume("<$token;"))
                return@LexFunction false

            val parsedString = nibbler.consumeUntil('>') ?: return@LexFunction false

            try {
                val nbtIndex = parsedString.indexOf('{').takeIf { it != -1 }
                val nbt = nbtIndex?.let { StringNbtReader(StringReader(parsedString.substring(nbtIndex))).parseCompound() }
                val idString = nbtIndex?.let { parsedString.substring(0, nbtIndex) } ?: parsedString
                val entityId = Identifier.tryParse(idString) ?: return@LexFunction false

                tokens.add(EntityModelToken(idString, entityId, nbt))
                return@LexFunction true
            } catch (e: CommandSyntaxException) {
                return@LexFunction false
            } catch (e: NoSuchElementException) {
                return@LexFunction false
            }
        }
    }

    override fun registerNodes(registrar: MarkdownFeature.NodeRegistrar) {
        registrar.registerNode(
            { _, structureToken, _ ->
                EntityModelNode(
                    structureToken.entityId,
                    structureToken.nbt,
                    componentSource
                )
            },
            { token, _ -> if (token is EntityModelToken) token else null }
        )
    }

    private class EntityModelToken(
        content: String,
        val entityId: Identifier,
        val nbt: NbtCompound?,
    ) : Lexer.Token(content)

    class EntityModelNode(
        private val entityId: Identifier,
        private val nbt: NbtCompound?,
        private val componentSource: BookCompiler.ComponentSource,
    ) : Parser.Node() {
        override fun visitStart(compiler: MarkdownCompiler<*>) {
            compiler as OwoUICompiler
            val structureComponent = componentSource.template<ParentComponent>(
                identifierOf("book_components"),
                "$NAMESPACE:entity-model-preview",
                mapOf("entity" to entityId.toString())
            )

            val entityPreview = structureComponent.childById<EntityModelComponent<WitherEntity>>("entity-model")!!

            if (nbt != null)
                entityPreview.entity.readNbt(nbt)

            entityPreview.lookAtCursor = true
            // entityPreview.rotate = true

            compiler.visitComponent(structureComponent)
        }

        override fun visitEnd(compiler: MarkdownCompiler<*>?) {}
    }
}

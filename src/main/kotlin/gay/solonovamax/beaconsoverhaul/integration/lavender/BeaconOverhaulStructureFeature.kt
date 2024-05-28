package gay.solonovamax.beaconsoverhaul.integration.lavender

import com.google.common.primitives.Ints
import gay.solonovamax.beaconsoverhaul.BeaconConstants.NAMESPACE
import gay.solonovamax.beaconsoverhaul.util.childById
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import gay.solonovamax.beaconsoverhaul.util.template
import io.wispforest.lavender.client.StructureOverlayRenderer
import io.wispforest.lavender.md.compiler.BookCompiler.ComponentSource
import io.wispforest.lavender.structure.LavenderStructures
import io.wispforest.lavender.structure.StructureTemplate
import io.wispforest.lavendermd.Lexer
import io.wispforest.lavendermd.Lexer.LexFunction
import io.wispforest.lavendermd.MarkdownFeature
import io.wispforest.lavendermd.MarkdownFeature.TokenRegistrar
import io.wispforest.lavendermd.Parser
import io.wispforest.lavendermd.compiler.MarkdownCompiler
import io.wispforest.lavendermd.compiler.OwoUICompiler
import io.wispforest.owo.ui.component.SlimSliderComponent
import io.wispforest.owo.ui.component.SmallCheckboxComponent
import io.wispforest.owo.ui.core.ParentComponent
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class BeaconOverhaulStructureFeature(
    val bookComponentSource: ComponentSource,
) : MarkdownFeature {

    override fun name(): String {
        return "$NAMESPACE:structures"
    }

    override fun supportsCompiler(compiler: MarkdownCompiler<*>): Boolean {
        return compiler is OwoUICompiler
    }

    override fun registerTokens(registrar: TokenRegistrar) {
        registrar.registerToken(structureLexer("$NAMESPACE:structure", true), '<')
        registrar.registerToken(structureLexer("$NAMESPACE:structure-visualizer", false), '<')
    }

    private fun structureLexer(token: String, placeable: Boolean): LexFunction {
        return LexFunction { nibbler, tokens ->
            if (!nibbler.tryConsume("<$token;"))
                return@LexFunction false

            val parsedString = nibbler.consumeUntil('>') ?: return@LexFunction false

            val angleIndex = parsedString.indexOf(';').takeIf { it != -1 }
            val angle = angleIndex?.let { Ints.tryParse(parsedString.substring(0, angleIndex)) ?: return@LexFunction false } ?: 35
            val idString = angleIndex?.let { parsedString.substring(angleIndex + 1) } ?: parsedString

            val structureId = Identifier.tryParse(idString) ?: return@LexFunction false

            val structure = LavenderStructures.get(structureId) ?: return@LexFunction false

            tokens.add(StructureToken(idString, structure, angle, placeable))
            true
        }
    }

    override fun registerNodes(registrar: MarkdownFeature.NodeRegistrar) {
        registrar.registerNode(
            { _, structureToken, _ ->
                StructureNode(structureToken.structure, structureToken.angle, structureToken.placeable, bookComponentSource)
            },
            { token, _ -> if (token is StructureToken) token else null }
        )
    }

    class StructureToken(
        content: String,
        val structure: StructureTemplate,
        val angle: Int,
        val placeable: Boolean,
    ) : Lexer.Token(content)

    class StructureNode(
        private val structure: StructureTemplate,
        private val angle: Int,
        private val placeable: Boolean,
        private val bookComponentSource: ComponentSource,
    ) : Parser.Node() {
        override fun visitStart(compiler: MarkdownCompiler<*>) {
            val structureComponent: ParentComponent = bookComponentSource.template<ParentComponent>(
                identifierOf("book_components"),
                if (structure.ySize > 1) "$NAMESPACE:structure-preview-with-layers" else "$NAMESPACE:structure-preview",
                mapOf(
                    "structure" to structure.id.toString(),
                    "angle" to angle.toString(),
                )
            )

            val structurePreview = structureComponent.childById<BeaconOverhaulStructureComponent>("structure")!!
            structurePreview.placeable = placeable

            val layerSlider = structureComponent.childById<SlimSliderComponent>("layer-slider")
            val showLayersCheckbox = structureComponent.childById<SmallCheckboxComponent>("show-layers-checkbox")

            layerSlider?.max(0.0)?.min(structure.ySize.toDouble())?.tooltipSupplier { layer ->
                if (layer > 0)
                    Text.translatable("text.lavender.structure_component.layer_tooltip", layer.toInt())
                else
                    Text.translatable("text.lavender.structure_component.all_layers_tooltip")
            }?.onChanged()?.subscribe(SlimSliderComponent.OnChanged { layer ->
                structurePreview.visibleLayer = layer.toInt() - 1
            })

            layerSlider?.value((StructureOverlayRenderer.getLayerRestriction(structure.id) + 1).toDouble())

            showLayersCheckbox?.tooltip(Text.translatable("guidebook.beaconoverhauled.structure_component.layer_checkbox"))
            showLayersCheckbox?.onChanged()?.subscribe(SmallCheckboxComponent.OnChanged { nowChecked ->
                structurePreview.showLayersBelowVisible = nowChecked
            })

            (compiler as OwoUICompiler).visitComponent(structureComponent)
        }

        override fun visitEnd(compiler: MarkdownCompiler<*>?) {}
    }
}

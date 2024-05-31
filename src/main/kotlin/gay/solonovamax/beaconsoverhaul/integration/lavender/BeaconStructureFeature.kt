package gay.solonovamax.beaconsoverhaul.integration.lavender

import com.google.common.primitives.Ints
import gay.solonovamax.beaconsoverhaul.BeaconConstants.NAMESPACE
import gay.solonovamax.beaconsoverhaul.config.BeaconOverhaulConfigManager
import gay.solonovamax.beaconsoverhaul.util.childById
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import gay.solonovamax.beaconsoverhaul.util.template
import io.wispforest.lavender.client.StructureOverlayRenderer
import io.wispforest.lavender.md.compiler.BookCompiler.ComponentSource
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

class BeaconStructureFeature(
    private val componentSource: ComponentSource,
) : MarkdownFeature {

    override fun name(): String {
        return "$NAMESPACE:beacon-structure"
    }

    override fun supportsCompiler(compiler: MarkdownCompiler<*>): Boolean {
        return compiler is OwoUICompiler
    }

    override fun registerTokens(registrar: TokenRegistrar) {
        registrar.registerToken(LexFunction { nibbler, tokens ->
            if (!nibbler.tryConsume("<$NAMESPACE:beacon-structure"))
                return@LexFunction false

            val parsedString = nibbler.consumeUntil('>') ?: return@LexFunction false

            val angle = if (parsedString.isNotEmpty()) Ints.tryParse(parsedString.removePrefix(";")) ?: return@LexFunction false else 35
            tokens.add(BeaconStructureToken(angle, true))

            true
        }, '<')
    }

    override fun registerNodes(registrar: MarkdownFeature.NodeRegistrar) {
        registrar.registerNode(
            { _, structureToken, _ ->
                StructureNode(structureToken.angle, structureToken.placeable, componentSource)
            },
            { token, _ -> if (token is BeaconStructureToken) token else null }
        )
    }

    class BeaconStructureToken(
        val angle: Int,
        val placeable: Boolean,
    ) : Lexer.Token("beacon.structure")

    class StructureNode(
        private val angle: Int,
        private val placeable: Boolean,
        private val componentSource: ComponentSource,
    ) : Parser.Node() {
        override fun visitStart(compiler: MarkdownCompiler<*>) {
            val structureComponent: ParentComponent = componentSource.template<ParentComponent>(
                identifierOf("book_components"),
                "$NAMESPACE:beacon-structure-preview",
                mapOf(
                    "angle" to angle.toString(),
                )
            )

            val structurePreview = structureComponent.childById<BeaconStructureComponent>("structure")!!
            structurePreview.placeable = placeable

            val tierSlider = structureComponent.childById<SlimSliderComponent>("tier-slider")!!
            val layerSlider = structureComponent.childById<SlimSliderComponent>("layer-slider")!!
            val showLayersCheckbox = structureComponent.childById<SmallCheckboxComponent>("show-layers-checkbox")!!

            tierSlider.min(1.0).max(BeaconOverhaulConfigManager.config.maxBeaconLayers.toDouble()).tooltipSupplier { layer ->
                Text.translatable("guidebook.beaconoverhauled.beacon_structure_component.tier_tooltip", layer.toInt())
            }.onChanged().subscribe(SlimSliderComponent.OnChanged { layer ->
                structurePreview.structureId = beaconStructureIdentifier(layer.toInt())
                layerSlider.min(layer + 1.0).max(0.0)
                layerSlider.value(layerSlider.value())
            })
            tierSlider.value(2.0)
            tierSlider.value(1.0)

            layerSlider.max(0.0).min(2.0).tooltipSupplier { layer ->
                if (layer > 0)
                    Text.translatable("text.lavender.structure_component.layer_tooltip", layer.toInt())
                else
                    Text.translatable("text.lavender.structure_component.all_layers_tooltip")
            }.onChanged().subscribe(SlimSliderComponent.OnChanged { layer ->
                structurePreview.visibleLayer = layer.toInt() - 1
            })

            layerSlider.value((StructureOverlayRenderer.getLayerRestriction(beaconStructureIdentifier(1)) + 1).toDouble())

            showLayersCheckbox.tooltip(Text.translatable("guidebook.beaconoverhauled.structure_component.layer_checkbox"))
            showLayersCheckbox.onChanged().subscribe(SmallCheckboxComponent.OnChanged { nowChecked ->
                structurePreview.showLayersBelowVisible = nowChecked
            })

            (compiler as OwoUICompiler).visitComponent(structureComponent)
        }

        override fun visitEnd(compiler: MarkdownCompiler<*>) {}
    }
}

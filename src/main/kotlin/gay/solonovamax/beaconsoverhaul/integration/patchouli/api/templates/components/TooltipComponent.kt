package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("patchouli:tooltip")
data class TooltipComponent(
    @SerialName("tooltip")
    val tooltips: List<String>,
    @SerialName("width")
    val width: Int,
    @SerialName("height")
    val height: Int,
    override val group: String? = null,
    override val x: Int? = null,
    override val y: Int? = null,
    override val flag: String? = null,
    override val advancement: String? = null,
    override val negateAdvancement: Boolean? = null,
    override val guard: String? = null,
) : TemplateComponent


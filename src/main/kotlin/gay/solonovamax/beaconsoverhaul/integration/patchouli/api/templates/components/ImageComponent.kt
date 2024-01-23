package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("patchouli:image")
data class ImageComponent(
    @SerialName("image")
    val image: String,
    @SerialName("width")
    val width: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("u")
    val u: Int? = null,
    @SerialName("v")
    val v: Int? = null,
    @SerialName("texture_width")
    val textureWidth: Int? = null,
    @SerialName("texture_height")
    val textureHeight: Int? = null,
    @SerialName("scale")
    val scale: Double? = null,
    override val group: String? = null,
    override val x: Int? = null,
    override val y: Int? = null,
    override val flag: String? = null,
    override val advancement: String? = null,
    override val negateAdvancement: Boolean? = null,
    override val guard: String? = null,
) : TemplateComponent

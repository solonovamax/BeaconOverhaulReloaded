@file:UseSerializers(ColorSerializer::class)

package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.components

import com.github.ajalt.colormath.Color
import gay.solonovamax.beaconsoverhaul.serialization.ColorSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
@SerialName("patchouli:text")
data class TextComponent(
    @SerialName("text")
    val text: String,
    @SerialName("color")
    val color: Color? = null,
    @SerialName("max_width")
    val maxWidth: Int? = null,
    @SerialName("line_height")
    val lineHeight: Int? = null,
    override val group: String? = null,
    override val x: Int? = null,
    override val y: Int? = null,
    override val flag: String? = null,
    override val advancement: String? = null,
    override val negateAdvancement: Boolean? = null,
    override val guard: String? = null,
) : TemplateComponent

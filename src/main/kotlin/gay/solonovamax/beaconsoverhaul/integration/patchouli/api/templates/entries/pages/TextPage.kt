@file:UseSerializers(ColorSerializer::class)

package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries.pages

import gay.solonovamax.beaconsoverhaul.serialization.ColorSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
@SerialName("patchouli:text")
data class TextPage(
    @SerialName("text")
    val text: String,
    @SerialName("title")
    val title: String? = null,
    override val flag: String? = null,
    override val advancement: String? = null,
    override val anchor: String? = null,
) : EntryPage

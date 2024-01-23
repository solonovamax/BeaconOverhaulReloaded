@file:UseSerializers(ResourceLocationSerializer::class)

package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries.pages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.minecraft.util.Identifier
import net.silkmc.silk.core.serialization.serializers.ResourceLocationSerializer

@Serializable
@SerialName("patchouli:image")
data class ImagePage(
    @SerialName("images")
    val images: List<Identifier>,
    @SerialName("title")
    val title: String? = null,
    @SerialName("border")
    val border: Boolean? = null,
    @SerialName("text")
    val text: String? = null,
    override val flag: String? = null,
    override val advancement: String? = null,
    override val anchor: String? = null,
) : EntryPage

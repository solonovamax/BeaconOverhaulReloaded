@file:UseSerializers(ItemStackSerializer::class)

package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries.pages

import gay.solonovamax.beaconsoverhaul.serialization.ItemStackSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
@SerialName("patchouli:multiblock")
data class MultiblockPage(
    @SerialName("name")
    val name: String,
    @SerialName("multiblock_id")
    val multiblockId: String? = null,
    @SerialName("multiblock")
    val multiblock: Multiblock? = null,
    @SerialName("enable_visualize")
    val canVisualize: Boolean? = null,
    @SerialName("text")
    val text: String? = null,
    override val flag: String? = null,
    override val advancement: String? = null,
    override val anchor: String? = null,
) : EntryPage {
    @Serializable
    data class Multiblock(
        val mapping: Map<String, String>,
        val pattern: List<List<String>>,
        val symmetrical: Boolean? = null,
        val offset: List<Int>? = null,
    )
}


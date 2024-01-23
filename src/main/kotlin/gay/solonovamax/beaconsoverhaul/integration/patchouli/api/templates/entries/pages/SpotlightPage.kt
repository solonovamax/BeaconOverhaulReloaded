@file:UseSerializers(ItemSerializer::class)

package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries.pages

import gay.solonovamax.beaconsoverhaul.serialization.ItemSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.minecraft.item.Item

@Serializable
@SerialName("patchouli:spotlight")
data class SpotlightPage(
    @SerialName("item")
    val item: Item,
    @SerialName("title")
    val title: String? = null,
    @SerialName("link_recipe")
    val linkRecipe: Boolean? = null,
    @SerialName("text")
    val text: String? = null,
    override val flag: String? = null,
    override val advancement: String? = null,
    override val anchor: String? = null,
) : EntryPage

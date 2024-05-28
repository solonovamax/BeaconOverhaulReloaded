@file:UseSerializers(ItemSerializer::class)

package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries.pages

import gay.solonovamax.beaconsoverhaul.serialization.ItemSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.tag.TagKey

@Serializable
@SerialName("patchouli:spotlight")
data class SpotlightPage private constructor(
    @SerialName("item")
    val item: String,
    @SerialName("title")
    val title: String? = null,
    @SerialName("link_recipe")
    val linkRecipe: Boolean? = null,
    @SerialName("text")
    val text: String? = null,
    override val flag: String? = null,
    override val advancement: String? = null,
    override val anchor: String? = null,
) : EntryPage {
    constructor(
        item: Item,
        title: String? = null,
        linkRecipe: Boolean? = null,
        text: String? = null,
        flag: String? = null,
        advancement: String? = null,
        anchor: String? = null,
    ) : this(Registries.ITEM.getId(item).toString(), title, linkRecipe, text, flag, advancement, anchor)

    constructor(
        items: List<Item>,
        title: String? = null,
        linkRecipe: Boolean? = null,
        text: String? = null,
        flag: String? = null,
        advancement: String? = null,
        anchor: String? = null,
    ) : this(
        items.joinToString { item -> Registries.ITEM.getId(item).toString() },
        title,
        linkRecipe,
        text,
        flag,
        advancement,
        anchor
    )

    constructor(
        itemTag: TagKey<Item>,
        title: String? = null,
        linkRecipe: Boolean? = null,
        text: String? = null,
        flag: String? = null,
        advancement: String? = null,
        anchor: String? = null,
    ) : this("tag:" + itemTag.id.toString(), title, linkRecipe, text, flag, advancement, anchor)
}

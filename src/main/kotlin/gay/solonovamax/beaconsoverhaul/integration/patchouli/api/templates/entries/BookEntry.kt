@file:UseSerializers(ResourceLocationSerializer::class)

package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries

import gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries.pages.EntryPage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.silkmc.silk.core.serialization.serializers.ResourceLocationSerializer

@Serializable
data class BookEntry private constructor(
    @SerialName("name")
    val name: String,
    @SerialName("category")
    val category: Identifier,
    @SerialName("icon")
    val icon: String,
    @SerialName("pages")
    val pages: List<EntryPage>,
    @SerialName("advancement")
    val advancement: String? = null,
    @SerialName("flag")
    val flag: String? = null,
    @SerialName("priority")
    val priority: Boolean? = null,
    @SerialName("secret")
    val secret: Boolean? = null,
    @SerialName("read_by_default")
    val readByDefault: Boolean? = null,
    @SerialName("sortnum")
    val sortingNumber: Int? = null,
    @SerialName("turnin")
    val turnIn: String? = null,
    @SerialName("extra_recipe_mappings")
    val extraRecipeMappings: Map<String, Int>? = null,
) {
    constructor(
        name: String,
        category: Identifier,
        icon: Item,
        pages: List<EntryPage>,
        advancement: String? = null,
        flag: String? = null,
        priority: Boolean? = null,
        secret: Boolean? = null,
        readByDefault: Boolean? = null,
        sortingNumber: Int? = null,
        turnIn: String? = null,
        extraRecipeMappings: Map<String, Int>? = null,
    ) : this(
        name,
        category,
        Registries.ITEM.getId(icon).toString(),
        pages,
        advancement,
        flag,
        priority,
        secret,
        readByDefault,
        sortingNumber,
        turnIn,
        extraRecipeMappings
    )

    constructor(
        name: String,
        category: Identifier,
        icons: List<Item>,
        pages: List<EntryPage>,
        advancement: String? = null,
        flag: String? = null,
        priority: Boolean? = null,
        secret: Boolean? = null,
        readByDefault: Boolean? = null,
        sortingNumber: Int? = null,
        turnIn: String? = null,
        extraRecipeMappings: Map<String, Int>? = null,
    ) : this(
        name,
        category,
        icons.joinToString { item -> Registries.ITEM.getId(item).toString() },
        pages,
        advancement,
        flag,
        priority,
        secret,
        readByDefault,
        sortingNumber,
        turnIn,
        extraRecipeMappings
    )

    constructor(
        name: String,
        category: Identifier,
        icon: TagKey<Item>,
        pages: List<EntryPage>,
        advancement: String? = null,
        flag: String? = null,
        priority: Boolean? = null,
        secret: Boolean? = null,
        readByDefault: Boolean? = null,
        sortingNumber: Int? = null,
        turnIn: String? = null,
        extraRecipeMappings: Map<String, Int>? = null,
    ) : this(
        name,
        category,
        "tag:" + icon.id.toString(),
        pages,
        advancement,
        flag,
        priority,
        secret,
        readByDefault,
        sortingNumber,
        turnIn,
        extraRecipeMappings
    )
}

@file:UseSerializers(ResourceLocationSerializer::class)

package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries

import gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries.pages.EntryPage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.minecraft.util.Identifier
import net.silkmc.silk.core.serialization.serializers.ResourceLocationSerializer

@Serializable
data class BookEntry(
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
)

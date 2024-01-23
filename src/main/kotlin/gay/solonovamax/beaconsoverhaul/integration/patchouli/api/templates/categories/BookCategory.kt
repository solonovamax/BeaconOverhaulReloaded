@file:UseSerializers(ResourceLocationSerializer::class)

package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.categories

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.minecraft.util.Identifier
import net.silkmc.silk.core.serialization.serializers.ResourceLocationSerializer

@Serializable
data class BookCategory(
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("icon")
    val icon: String,
    @SerialName("parent")
    val parent: Identifier? = null,
    @SerialName("flag")
    val flag: String? = null,
    @SerialName("sortnum")
    val sortingNumber: Int? = null,
    @SerialName("secret")
    val secret: Boolean? = null,
)

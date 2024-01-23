@file:UseSerializers(ResourceLocationSerializer::class)

package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries.pages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.minecraft.util.Identifier
import net.silkmc.silk.core.serialization.serializers.ResourceLocationSerializer

@Serializable
@SerialName("patchouli:smelting")
data class SmeltingPage(
    @SerialName("recipe")
    val recipe: Identifier,
    @SerialName("recipe2")
    val secondRecipe: Identifier? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("text")
    val text: String? = null,
    override val flag: String? = null,
    override val advancement: String? = null,
    override val anchor: String? = null,
) : EntryPage


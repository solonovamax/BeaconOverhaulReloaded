@file:UseSerializers(ItemStackSerializer::class)

package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.components

import gay.solonovamax.beaconsoverhaul.serialization.ItemStackSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.minecraft.item.ItemStack

@Serializable
@SerialName("patchouli:item")
data class ItemComponent(
    @SerialName("item")
    val item: ItemStack,
    @SerialName("framed")
    val framed: Boolean? = null,
    @SerialName("link_recipe")
    val linkRecipe: Boolean? = null,
    override val group: String? = null,
    override val x: Int? = null,
    override val y: Int? = null,
    override val flag: String? = null,
    override val advancement: String? = null,
    override val negateAdvancement: Boolean? = null,
    override val guard: String? = null,
) : TemplateComponent


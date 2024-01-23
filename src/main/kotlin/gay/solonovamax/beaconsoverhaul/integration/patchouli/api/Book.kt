@file:UseSerializers(ResourceLocationSerializer::class, ItemStackSerializer::class, ColorSerializer::class)

package gay.solonovamax.beaconsoverhaul.integration.patchouli.api

import com.github.ajalt.colormath.Color
import gay.solonovamax.beaconsoverhaul.serialization.ColorSerializer
import gay.solonovamax.beaconsoverhaul.serialization.ItemStackSerializer
import gay.solonovamax.beaconsoverhaul.util.TranslationKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.silkmc.silk.core.serialization.serializers.ResourceLocationSerializer
import vazkii.patchouli.api.PatchouliConfigAccess.TextOverflowMode

@Serializable
data class Book(
    @SerialName("name")
    val name: TranslationKey,
    @SerialName("landing_text")
    val landingText: TranslationKey? = null,
    @SerialName("book_texture")
    val bookTexture: Identifier? = null,
    @SerialName("filler_texture")
    val fillerTexture: Identifier? = null,
    @SerialName("crafting_texture")
    val craftingTexture: Identifier? = null,
    @SerialName("model")
    val model: Identifier? = null,
    @SerialName("use_blocky_font")
    val useBlockyFont: Boolean? = null,
    @SerialName("text_color")
    val textColor: Color? = null,
    @SerialName("header_color")
    val headerColor: Color? = null,
    @SerialName("nameplate_color")
    val nameplateColor: Color? = null,
    @SerialName("link_color")
    val linkColor: Color? = null,
    @SerialName("link_hover_color")
    val linkHoverColor: Color? = null,
    @SerialName("progress_bar_color")
    val progressBarColor: Color? = null,
    @SerialName("progress_bar_background")
    val progressBarBackgroundColor: Color? = null,
    @SerialName("open_sound")
    val openSound: Identifier? = null,
    @SerialName("flip_sound")
    val flipSound: Identifier? = null,
    @SerialName("show_progress")
    val showProgress: Boolean? = null,
    @SerialName("index_icon")
    val indexIcon: String? = null,
    @SerialName("version")
    val version: String? = null,
    @SerialName("subtitle")
    val subtitle: String? = null,
    @SerialName("creative_tab")
    val creativeTab: Identifier? = null,
    @SerialName("advancements_tab")
    val advancementsTab: Identifier? = null,
    @SerialName("dont_generate_book")
    val noBook: Boolean? = null,
    @SerialName("show_toasts")
    val showToasts: Boolean? = null,
    @SerialName("pause_game")
    val pauseGame: Boolean? = null,
    @SerialName("pamphlet")
    val isPamphlet: Boolean? = null,
    @SerialName("i18n")
    val i18n: Boolean? = null,
    @SerialName("text_overflow_mode")
    val overflowMode: TextOverflowMode? = null,
    @SerialName("use_resource_pack")
    val useResourcePack: Boolean = true,
    @SerialName("custom_book_item")
    val customBookItem: ItemStack? = null,
)

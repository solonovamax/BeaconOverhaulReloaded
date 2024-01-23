package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface TemplateComponent {
    @SerialName("group")
    val group: String?

    @SerialName("x")
    val x: Int?

    @SerialName("y")
    val y: Int?

    @SerialName("flag")
    val flag: String?

    @SerialName("advancement")
    val advancement: String?

    @SerialName("negate_advancement")
    val negateAdvancement: Boolean?

    @SerialName("guard")
    val guard: String?
}

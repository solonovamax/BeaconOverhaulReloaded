package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("patchouli:separator")
data class SeparatorComponent(
    override val group: String? = null,
    override val x: Int? = null,
    override val y: Int? = null,
    override val flag: String? = null,
    override val advancement: String? = null,
    override val negateAdvancement: Boolean? = null,
    override val guard: String? = null,
) : TemplateComponent


@file:UseSerializers(ResourceLocationSerializer::class)

package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.minecraft.util.Identifier
import net.silkmc.silk.core.serialization.serializers.ResourceLocationSerializer

@Serializable
@SerialName("patchouli:entity")
data class EntityComponent(
    @SerialName("entity")
    val entity: Identifier,
    @SerialName("render_size")
    val renderSize: Int? = null,
    @SerialName("rotate")
    val rotate: Boolean? = null,
    @SerialName("default_rotation")
    val defaultRotation: Double? = null,
    override val group: String? = null,
    override val x: Int? = null,
    override val y: Int? = null,
    override val flag: String? = null,
    override val advancement: String? = null,
    override val negateAdvancement: Boolean? = null,
    override val guard: String? = null,
) : TemplateComponent


@file:UseSerializers(ResourceLocationSerializer::class)

package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries.pages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.minecraft.util.Identifier
import net.silkmc.silk.core.serialization.serializers.ResourceLocationSerializer

@Serializable
@SerialName("patchouli:entity")
data class EntityPage(
    @SerialName("entity")
    val entity: Identifier,
    @SerialName("scale")
    val scale: Double? = null,
    @SerialName("offset")
    val offset: Double? = null,
    @SerialName("rotate")
    val rotate: Boolean? = null,
    @SerialName("default_rotation")
    val defaultRotation: Double? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("text")
    val text: String? = null,
    override val flag: String? = null,
    override val advancement: String? = null,
    override val anchor: String? = null,
) : EntryPage


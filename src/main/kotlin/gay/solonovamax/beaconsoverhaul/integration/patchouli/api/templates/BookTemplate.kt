@file:UseSerializers(KClassSerializer::class)

package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates

import gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.components.TemplateComponent
import gay.solonovamax.beaconsoverhaul.serialization.KClassSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonObject
import vazkii.patchouli.api.IComponentProcessor
import kotlin.reflect.KClass

@Serializable
data class BookTemplate(
    @SerialName("components")
    val components: List<TemplateComponent>,
    @SerialName("include")
    val include: List<NestedBookTemplate>,
    @SerialName("processor")
    val processor: KClass<IComponentProcessor>,
) {
    @Serializable
    data class NestedBookTemplate(
        @SerialName("template")
        val template: String,
        @SerialName("as")
        val asName: String,
        @SerialName("using")
        val using: JsonObject? = null,
        val x: Int? = null,
        val y: Int? = null,
    )
}

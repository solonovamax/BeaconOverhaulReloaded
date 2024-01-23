package gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries.pages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface EntryPage {
    @SerialName("advancement")
    val advancement: String?

    @SerialName("flag")
    val flag: String?

    @SerialName("anchor")
    val anchor: String?
}


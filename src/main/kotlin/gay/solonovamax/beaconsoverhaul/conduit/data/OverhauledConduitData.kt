package gay.solonovamax.beaconsoverhaul.conduit.data

import kotlinx.serialization.Serializable

@Serializable
data class OverhauledConduitData(
    val someData: Int,
) {
    companion object {
        val UNIT = OverhauledConduitData(0)
    }
}

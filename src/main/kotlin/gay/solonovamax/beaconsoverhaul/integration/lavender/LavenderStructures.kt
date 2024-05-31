package gay.solonovamax.beaconsoverhaul.integration.lavender

import com.google.gson.JsonParser
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import net.minecraft.util.math.Vec3i
import io.wispforest.lavender.structure.StructureTemplate as LavenderStructureTemplate

@JvmField
val EMPTY_STRUCTURE_TEMPLATE = LavenderStructureTemplate(identifierOf("empty"), arrayOf(), 0, 0, 0, Vec3i.ZERO)

fun createBeaconStructureTemplate(tier: Int): LavenderStructureTemplate {
    val size = tier * 2 + 1

    val jsonString = buildJsonObject {
        putJsonObject("keys") {
            put("b", "minecraft:beacon")
            put("B", "#minecraft:beacon_base_blocks")
            put("anchor", "#minecraft:beacon_base_blocks")
        }
        putJsonArray("layers") {
            for (level in tier downTo 0) {
                addJsonArray {
                    when (level) {
                        0 -> {
                            repeat(tier) { add(" ".repeat(size)) }
                            add(" ".repeat(tier) + "b" + " ".repeat(tier))
                            repeat(tier) { add(" ".repeat(size)) }
                        }

                        tier -> {
                            repeat(tier) { add("B".repeat(size)) }
                            add("B".repeat(tier) + "#" + "B".repeat(tier))
                            repeat(tier) { add("B".repeat(size)) }
                        }

                        else -> {
                            repeat(tier - level) { add(" ".repeat(size)) }
                            repeat(level * 2 + 1) {
                                add(" ".repeat(tier - level) + "B".repeat(level * 2 + 1) + " ".repeat(tier - level))
                            }
                            repeat(tier - level) { add(" ".repeat(size)) }
                        }
                    }

                }
            }
        }
    }.toString()

    return LavenderStructureTemplate.parse(beaconStructureIdentifier(tier), JsonParser.parseString(jsonString).asJsonObject)
}


fun beaconStructureIdentifier(tier: Int) = identifierOf("structure_gen/beacon/$tier")

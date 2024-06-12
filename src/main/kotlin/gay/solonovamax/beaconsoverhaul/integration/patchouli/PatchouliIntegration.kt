package gay.solonovamax.beaconsoverhaul.integration.patchouli

import gay.solonovamax.beaconsoverhaul.config.BeaconOverhaulConfigManager
import gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.categories.BookCategory
import gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries.BookEntry
import gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries.pages.MultiblockPage
import gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries.pages.MultiblockPage.Multiblock
import gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries.pages.SpotlightPage
import gay.solonovamax.beaconsoverhaul.integration.patchouli.api.templates.entries.pages.TextPage
import gay.solonovamax.beaconsoverhaul.util.addAsset
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import net.devtech.arrp.api.RuntimeResourcePack
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.item.Items

object PatchouliIntegration {
    // val GUIDE_IDENTIFIER = identifierOf("guide")
    val PATCHOULI_BOOK_JSON = identifierOf("patchouli_books/guide/book.json")
    val BEACON_CATEGORY = identifierOf("beacon")
    val CONDUIT_CATEGORY = identifierOf("conduit")

    fun writePatchouliBook(resourcePack: RuntimeResourcePack) {
        resourcePack.addAsset(
            identifierOf("patchouli_books/guide/en_us/categories/beacon.json"),
            BookCategory(
                name = "Beacons",
                description = "Information about Beacons and how they work.",
                icon = "minecraft:beacon"
            )
        )
        resourcePack.addAsset(
            identifierOf("patchouli_books/guide/en_us/categories/corrupted_beacon.json"),
            BookCategory(
                name = "Corrupted Beacons",
                description = """
                    |Information about Corrupted Beacons and how they work.
                    |
                    |TODO. Not yet implemented/complete.
                """.trimMargin("|").replace("\n", "\$(br)"),
                icon = "minecraft:crying_obsidian"
            )
        )
        resourcePack.addAsset(
            identifierOf("patchouli_books/guide/en_us/categories/conduit.json"),
            BookCategory(
                name = "Conduits",
                description = """
                    |Information about Conduits and how they work.
                    |
                    |TODO. Not yet implemented/complete.
                """.trimMargin("|").replace("\n", "\$(br)"),
                icon = "minecraft:conduit"
            )
        )

        resourcePack.addAsset(
            identifierOf("patchouli_books/guide/en_us/entries/beacon/beacon_structure.json"),
            BookEntry(
                name = "Beacon Structure",
                icon = Items.BEACON,
                category = BEACON_CATEGORY,
                pages = buildList {
                    SpotlightPage(
                        item = Items.BEACON,
                        title = "The Beacon",
                        text = """
                            |A beacon is a block that provides players with various status effects in a radius around it.
                        """.trimMargin("|").replace("\n", "\$(br)"),
                    ).run(::add)
                    TextPage(
                        title = "Activation",
                        text = """
                            |To activate the beacon and gain its effects, you need to construct a pyramid-shaped structures underneath of it, built out of valuable resources, such as gold or iron.
                            |The beacon must not have any non-transparent blocks above it, in order to activate.
                        """.trimMargin("|").replace("\n", "\$(br)"),
                    ).run(::add)
                    TextPage(
                        title = "Beacon Tiers",
                        text = "Beacons can have serval tiers. The higher the tier, the more effects you gain access to.\nConstructing a larger beacon has the additional benefit of making your effects more powerful, through longer durations, and higher effect levels.".replace(
                            "\n",
                            "\$(br)"
                        ),
                    ).run(::add)
                    for (tier in 1..6) {
                        MultiblockPage(
                            name = "Tier $tier beacon",
                            multiblock = Multiblock(
                                mapping = blockMappings,
                                pattern = multiblockForTier(tier),
                            )
                        ).run(::add)
                    }
                },
                sortingNumber = 0,
            )
        )

        resourcePack.addAsset(
            identifierOf("patchouli_books/guide/en_us/entries/beacon/beacon_customization.json"),
            BookEntry(
                name = "Beacon Customization",
                icon = Items.PURPLE_STAINED_GLASS,
                category = BEACON_CATEGORY,
                pages = buildList {
                    SpotlightPage(
                        itemTag = ConventionalItemTags.GLASS_BLOCKS,
                        title = "Colour",
                        text = """
                            |You can change the colour of a beacon by placing a piece of stained glass in front of it.
                        """.trimMargin().replace("\n", "\$(br)")
                    ).run(::add)
                    SpotlightPage(
                        item = Items.AMETHYST_CLUSTER,
                        title = "Redirection",
                        text = """
                            |Beacon beams can be redirected using Amethyst Clusters!
                            |
                            |Simply place a cluster in the path of the beam, and the beam will be redirected in the direction of the cluster.
                        """.trimMargin("|").replace("\n", "\$(br)")
                    ).run(::add)
                }
            )
        )

        resourcePack.addAsset(
            identifierOf("patchouli_books/guide/en_us/entries/beacon/beacon_base_blocks.json"),
            BookEntry(
                name = "Beacon Base Blocks",
                icon = Items.EMERALD_BLOCK,
                category = BEACON_CATEGORY,
                pages = buildList {
                    for ((material, expression) in BeaconOverhaulConfigManager.beaconConfig.additionModifierBlocks) {
                        SpotlightPage(
                            item = material.asItem(),
                            text = """
                                For every ${material.name.string}, points are added to the total according to:
                                ${expression.expressionString}
                            """.trimIndent().replace("\n", "\$(br)")
                        ).run(::add)
                    }

                    for ((material, expression) in BeaconOverhaulConfigManager.beaconConfig.multiplicationModifierBlocks) {
                        SpotlightPage(
                            item = material.asItem(),
                            text = """
                                For every ${material.name.string}, the total number of points is multiplied by:
                                ${expression.expressionString}
                            """.trimIndent().replace("\n", "\$(br)")
                        ).run(::add)
                    }
                },
                sortingNumber = 1,
            )
        )

        resourcePack.addAsset(
            identifierOf("patchouli_books/guide/en_us/entries/beacon/beacon_points.json"),
            BookEntry(
                name = "Beacon Formulas",
                icon = Items.REDSTONE,
                category = BEACON_CATEGORY,
                pages = buildList {
                    TextPage(
                        title = "Points",
                        text = """
                            |What are points?
                            |
                            |Points are used to calculate different properties of a beacon, such as their range, the duration of effects, and the level of effects.
                            |Different block types add more or less points, based on the formula associated with it.
                        """.trimMargin("|").replace("\n", "\$(br)")
                    ).run(::add)
                    TextPage(
                        title = "Range Formula",
                        text = """
                            |The range of the beacon (in blocks) is computed according to:
                            |${BeaconOverhaulConfigManager.beaconConfig.range.expressionString}
                            |
                            |Where 'pts' is the number of points the beacon has.
                        """.trimMargin("|").replace("\n", "\$(br)")
                    ).run(::add)
                    TextPage(
                        title = "Duration Formula",
                        text = """
                            |The duration of the beacon effects (in seconds) is computed according to:
                            |${BeaconOverhaulConfigManager.beaconConfig.duration.expressionString}
                            |
                            |Where 'pts' is the number of points the beacon has.
                        """.trimMargin("|").replace("\n", "\$(br)")
                    ).run(::add)
                    TextPage(
                        title = "Effect Level Formula",
                        text = """
                            |The level of the primary effect from beacons is computed according to:
                            |${BeaconOverhaulConfigManager.beaconConfig.primaryAmplifier.expressionString}
                            |
                            |The level of the secondary effect from beacons is computed according to:
                            |${BeaconOverhaulConfigManager.beaconConfig.secondaryAmplifier.expressionString}
                            |
                            |Where 'pts' is the number of points the beacon has, and 'isPotent' 1 if no secondary effect is selected, and 0 if a secondary effect is selected, at tier 4 or higher.
                        """.trimMargin("|").replace("\n", "\$(br)")
                    ).run(::add)
                },
                sortingNumber = 1,
            )
        )
    }

    private val blockMappings = mapOf(
        "b" to "minecraft:beacon",
        "B" to "#minecraft:beacon_base_blocks",
        "0" to "#minecraft:beacon_base_blocks",
    )

    private fun multiblockForTier(tier: Int): List<List<String>> {
        return buildList {
            for (level in 0..tier) {
                buildList {
                    val lineWidth = tier * 2 + 1
                    when (level) {
                        0 -> {
                            repeat(tier) { add("_".repeat(lineWidth)) }
                            add("_".repeat(tier) + "b" + "_".repeat(tier))
                            repeat(tier) { add("_".repeat(lineWidth)) }
                        }

                        tier -> {
                            repeat(tier) { add("B".repeat(lineWidth)) }
                            add("B".repeat(tier) + "0" + "B".repeat(tier))
                            repeat(tier) { add("B".repeat(lineWidth)) }
                        }

                        else -> {
                            repeat(tier - level) { add("_".repeat(lineWidth)) }
                            repeat(level * 2 + 1) {
                                add("_".repeat(tier - level) + "B".repeat(level * 2 + 1) + "_".repeat(tier - level))
                            }
                            repeat(tier - level) { add("_".repeat(lineWidth)) }
                        }
                    }
                }.run(::add)
            }
        }
    }

    // private fun registerMultiblock(name: String, multiblock: Array<Array<String>>): IMultiblock {
    //     val multiblock = PatchouliAPI.get().makeMultiblock(multiblock, targetBlocks)
    //     return PatchouliAPI.get().registerMultiblock(identifierOf(name), multiblock)
    // }
}

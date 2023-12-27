package gay.solonovamax.beaconsoverhaul

import ca.solostudios.guava.kotlin.collect.MutableMultiset
import net.minecraft.block.Block

interface OverhauledBeacon {
    val baseBlocks: MutableMultiset<Block>
    var vanillaLevel: Int
}

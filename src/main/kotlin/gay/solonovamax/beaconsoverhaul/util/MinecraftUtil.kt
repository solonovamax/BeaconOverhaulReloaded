package gay.solonovamax.beaconsoverhaul.util

import gay.solonovamax.beaconsoverhaul.BeaconConstants
import net.minecraft.util.Identifier

fun identifierOf(namespace: String = BeaconConstants.NAMESPACE, path: String): Identifier = Identifier(namespace, path)

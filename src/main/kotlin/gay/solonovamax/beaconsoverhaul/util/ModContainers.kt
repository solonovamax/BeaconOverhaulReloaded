package gay.solonovamax.beaconsoverhaul.util

import net.fabricmc.loader.api.FabricLoader

private const val EMI_MOD_ID = "emi"
private const val REI_MOD_ID = "rei"
private const val JEI_MOD_ID = "jei"

val EMI = FabricLoader.getInstance().getModContainer(EMI_MOD_ID)
val REI = FabricLoader.getInstance().getModContainer(REI_MOD_ID)
val JEI = FabricLoader.getInstance().getModContainer(JEI_MOD_ID)

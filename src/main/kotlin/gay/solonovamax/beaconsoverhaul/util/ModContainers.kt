package gay.solonovamax.beaconsoverhaul.util

import net.fabricmc.loader.api.ModContainer
import java.util.Optional


private const val EMI_MOD_ID = "emi"
private const val REI_MOD_ID = "rei"
private const val JEI_MOD_ID = "jei"

val EMI: Optional<ModContainer> = FabricLoader.getModContainer(EMI_MOD_ID)
val REI: Optional<ModContainer> = FabricLoader.getModContainer(REI_MOD_ID)
val JEI: Optional<ModContainer> = FabricLoader.getModContainer(JEI_MOD_ID)

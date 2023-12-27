package gay.solonovamax.beaconsoverhaul.util

import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Path

fun FabricLoader.configDir(name: String): Path = FabricLoader.getInstance().configDir.resolve(name)

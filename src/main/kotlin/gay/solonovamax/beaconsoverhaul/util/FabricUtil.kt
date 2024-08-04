package gay.solonovamax.beaconsoverhaul.util

import java.nio.file.Path
import net.fabricmc.loader.api.FabricLoader as RealFabricLoader

object FabricLoader : RealFabricLoader by RealFabricLoader.getInstance() {
    fun configDir(name: String): Path = configDir.resolve(name)
}

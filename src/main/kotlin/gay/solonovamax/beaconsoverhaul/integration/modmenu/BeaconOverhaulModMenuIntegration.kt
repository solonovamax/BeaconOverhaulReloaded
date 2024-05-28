package gay.solonovamax.beaconsoverhaul.integration.modmenu

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import gay.solonovamax.beaconsoverhaul.config.BeaconOverhaulConfigManager

class BeaconOverhaulModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory(BeaconOverhaulConfigManager::createConfigScreen)
    }
}

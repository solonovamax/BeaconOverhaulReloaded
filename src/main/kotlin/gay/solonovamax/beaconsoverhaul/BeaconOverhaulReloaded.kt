package gay.solonovamax.beaconsoverhaul

import gay.solonovamax.beaconsoverhaul.block.beacon.data.OverhauledBeaconData
import gay.solonovamax.beaconsoverhaul.block.conduit.data.OverhauledConduitData
import gay.solonovamax.beaconsoverhaul.config.ConfigManager
import gay.solonovamax.beaconsoverhaul.register.BlockRegistry
import gay.solonovamax.beaconsoverhaul.register.CriterionRegistry
import gay.solonovamax.beaconsoverhaul.register.ItemGroupRegistry
import gay.solonovamax.beaconsoverhaul.register.ItemRegistry
import gay.solonovamax.beaconsoverhaul.register.ScreenHandlerRegistry
import gay.solonovamax.beaconsoverhaul.register.StatusEffectRegistry
import gay.solonovamax.beaconsoverhaul.register.TagRegistry
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.block.entity.BeaconBlockEntity
import net.silkmc.silk.network.packet.s2cPacket
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.info


object BeaconOverhaulReloaded : ModInitializer {
    // val RESOURCE_PACK = RuntimeResourcePack.create(identifierOf("beacon-overhaul"))

    private val logger by getLogger()

    val updateBeaconPacket = s2cPacket<OverhauledBeaconData>(identifierOf("beacon_update"))

    val updateConduitPacket = s2cPacket<OverhauledConduitData>(identifierOf("conduit_update"))

    override fun onInitialize() {
        logger.info { "Loading ${BeaconConstants.MOD_NAME}" }

        // Make sure we register shit first lol
        BlockRegistry.register()
        ItemRegistry.register()
        ItemGroupRegistry.register()
        StatusEffectRegistry.register()
        ScreenHandlerRegistry.register()
        CriterionRegistry.register()
        TagRegistry.register()

        createRuntimeResourcepack()

        ServerLifecycleEvents.SERVER_STARTING.register {
            // add the status effects a bit later in the lifecycle
            addStatusEffectsToBeacon()
        }
    }

    private fun addStatusEffectsToBeacon() {
        val effectsByLevel = listOf(
            ConfigManager.beaconConfig.beaconEffectsByTier.tierOne,
            ConfigManager.beaconConfig.beaconEffectsByTier.tierTwo,
            ConfigManager.beaconConfig.beaconEffectsByTier.tierThree,
            ConfigManager.beaconConfig.beaconEffectsByTier.secondaryEffects,
        )

        BeaconBlockEntity.EFFECTS_BY_LEVEL = effectsByLevel.map { it.toList() }
        BeaconBlockEntity.EFFECTS = effectsByLevel.flatMapTo(mutableSetOf()) { it }
    }

    private fun createRuntimeResourcepack() {
        // TODO: 2024-10-07 Add runtime resource pack for beacon_base_blocks
        // val beaconBaseBlocksTag = JTag().apply {
        //     for (block in ConfigManager.beaconConfig.beaconBaseBlocks)
        //         add(block.id)
        //
        //     RESOURCE_PACK.addTag(identifierOf("minecraft", "blocks/beacon_base_blocks"), this)
        // }
        //
        // RRPCallback.AFTER_VANILLA.register { resourcePacks: MutableList<ResourcePack> ->
        //     resourcePacks.add(RESOURCE_PACK)
        // }
        //
        // RESOURCE_PACK.dump()
    }

}

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
import gay.solonovamax.beaconsoverhaul.util.id
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import net.devtech.arrp.api.RRPCallback
import net.devtech.arrp.api.RuntimeResourcePack
import net.devtech.arrp.json.tags.JTag
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.resource.ResourcePack
import net.minecraft.world.GameRules
import net.minecraft.world.GameRules.Category
import net.silkmc.silk.network.packet.s2cPacket
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.info


object BeaconOverhaulReloaded : ModInitializer {
    val LONG_REACH_INCREMENT = GameRules.register("longReachIncrement", Category.PLAYER, GameRules.IntRule.create(2))

    val RESOURCE_PACK = RuntimeResourcePack.create(identifierOf("beacon-overhaul"))

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
        val effectsByLevel = arrayOf(
            ConfigManager.beaconConfig.beaconEffectsByTier.tierOne.toTypedArray(),
            ConfigManager.beaconConfig.beaconEffectsByTier.tierTwo.toTypedArray(),
            ConfigManager.beaconConfig.beaconEffectsByTier.tierThree.toTypedArray(),
            ConfigManager.beaconConfig.beaconEffectsByTier.secondaryEffects.toTypedArray(),
        )

        BeaconBlockEntity.EFFECTS_BY_LEVEL = effectsByLevel
        BeaconBlockEntity.EFFECTS = effectsByLevel.flatMapTo(mutableSetOf()) { it.asIterable() }
    }

    private fun createRuntimeResourcepack() {
        val beaconBaseBlocksTag = JTag().apply {
            for (block in ConfigManager.beaconConfig.beaconBaseBlocks)
                add(block.id)

            RESOURCE_PACK.addTag(identifierOf("minecraft", "blocks/beacon_base_blocks"), this)
        }

        RRPCallback.AFTER_VANILLA.register { resourcePacks: MutableList<ResourcePack> ->
            resourcePacks.add(RESOURCE_PACK)
        }

        RESOURCE_PACK.dump()
    }

}

package gay.solonovamax.beaconsoverhaul

import gay.solonovamax.beaconsoverhaul.block.beacon.data.OverhauledBeaconData
import gay.solonovamax.beaconsoverhaul.conduit.data.OverhauledConduitData
import gay.solonovamax.beaconsoverhaul.config.BeaconOverhaulConfigManager
import gay.solonovamax.beaconsoverhaul.integration.patchouli.PatchouliIntegration
import gay.solonovamax.beaconsoverhaul.mixin.BeaconBlockEntityAccessor
import gay.solonovamax.beaconsoverhaul.mixin.GameRulesAccessor
import gay.solonovamax.beaconsoverhaul.registry.BlockRegistry
import gay.solonovamax.beaconsoverhaul.registry.CriterionRegistry
import gay.solonovamax.beaconsoverhaul.registry.ScreenHandlerRegistry
import gay.solonovamax.beaconsoverhaul.registry.StatusEffectRegistry
import gay.solonovamax.beaconsoverhaul.registry.TagRegistry
import gay.solonovamax.beaconsoverhaul.util.id
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import net.devtech.arrp.api.RRPCallback
import net.devtech.arrp.api.RuntimeResourcePack
import net.devtech.arrp.json.tags.JTag
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.resource.ResourcePack
import net.minecraft.world.GameRules.Category
import net.silkmc.silk.network.packet.s2cPacket
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.info
import gay.solonovamax.beaconsoverhaul.mixin.IntRuleAccessor as IntRule


object BeaconOverhaulReloaded : ModInitializer {
    val LONG_REACH_INCREMENT = GameRulesAccessor.register("longReachIncrement", Category.PLAYER, IntRule.create(2))

    val RESOURCE_PACK = RuntimeResourcePack.create(identifierOf("beacon-overhaul"))

    private val logger by getLogger()

    val updateBeaconPacket = s2cPacket<OverhauledBeaconData>(identifierOf("beacon_update"))

    val updateConduitPacket = s2cPacket<OverhauledConduitData>(identifierOf("conduit_update"))

    override fun onInitialize() {
        logger.info { "Loading ${BeaconConstants.MOD_NAME}" }

        // Make sure we register shit first lol
        BlockRegistry.register()
        StatusEffectRegistry.register()
        ScreenHandlerRegistry.register()
        CriterionRegistry.register()
        TagRegistry.register()

        createRuntimeResourcepack()

        ServerLifecycleEvents.SERVER_STARTING.register {
            logger.info { "Applying status effect shit" }
            // add the status effects a bit later in the lifecycle
            addStatusEffectsToBeacon()
        }
    }

    private fun addStatusEffectsToBeacon() {
        val effectsByLevel = arrayOf(
            BeaconOverhaulConfigManager.config.beaconEffectsByTier.tierOne.toTypedArray(),
            BeaconOverhaulConfigManager.config.beaconEffectsByTier.tierTwo.toTypedArray(),
            BeaconOverhaulConfigManager.config.beaconEffectsByTier.tierThree.toTypedArray(),
            BeaconOverhaulConfigManager.config.beaconEffectsByTier.secondaryEffects.toTypedArray(),
        )

        BeaconBlockEntityAccessor.setEffectsByLevel(effectsByLevel)

        BeaconBlockEntityAccessor.setEffects(effectsByLevel.flatMapTo(mutableSetOf()) { it.asIterable() })
    }

    private fun createRuntimeResourcepack() {
        val beaconBaseBlocksTag = JTag().apply {
            for (block in BeaconOverhaulConfigManager.config.beaconBaseBlocks)
                add(block.id)

            RESOURCE_PACK.addTag(identifierOf("minecraft", "blocks/beacon_base_blocks"), this)
        }

        PatchouliIntegration.writePatchouliBook(RESOURCE_PACK)
        // loadPatchouliBook(RESOURCE_PACK)

        RRPCallback.AFTER_VANILLA.register { resourcePacks: MutableList<ResourcePack> ->
            resourcePacks.add(RESOURCE_PACK)
        }

        RESOURCE_PACK.dump()
    }

    // private fun loadPatchouliBook(resourcePack: RuntimeResourcePack) {
    //     val fabricXplatMod = FabricXplatModContainer(modContainer)
    //     val bookResourceSupplier = resourcePack.open(ResourceType.SERVER_DATA, PatchouliIntegration.PATCHOULI_BOOK_JSON)
    //     if (bookResourceSupplier != null) {
    //         logger.info { "Loading patchouli book" }
    //         BookRegistry.INSTANCE.loadBook(fabricXplatMod, PatchouliIntegration.GUIDE_IDENTIFIER, bookResourceSupplier.get(), false)
    //     } else {
    //         logger.info { "Patchouli book could not be loaded" }
    //     }
    // }
}

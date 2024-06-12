package gay.solonovamax.beaconsoverhaul

import gay.solonovamax.beaconsoverhaul.config.BeaconOverhauledConfig
import gay.solonovamax.beaconsoverhaul.config.BeaconOverhauledConfig.BeaconBlockExpression
import gay.solonovamax.beaconsoverhaul.config.BeaconOverhauledConfig.BeaconEffectAmplifierExpression
import gay.solonovamax.beaconsoverhaul.config.BeaconOverhauledConfig.BeaconModifierExpression
import gay.solonovamax.beaconsoverhaul.config.BeaconOverhauledConfig.BeaconTierEffects
import gay.solonovamax.beaconsoverhaul.config.ConduitConfig
import gay.solonovamax.beaconsoverhaul.register.StatusEffectRegistry
import gay.solonovamax.beaconsoverhaul.util.id
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import net.minecraft.block.Blocks
import net.minecraft.entity.effect.StatusEffects
import kotlin.time.Duration.Companion.seconds

object BeaconConstants {
    const val MOD_NAME = "Beacons Overhaul Reloaded"

    const val NAMESPACE = "beaconoverhaul"

    // TODO: add all of these
    // minecraft:amethyst_block
    // minecraft:copper_block
    // minecraft:diamond_block
    // minecraft:emerald_block
    // minecraft:gold_block
    // minecraft:iron_block
    // minecraft:netherite_block
    //
    // ad_astra:calorite_block
    // ad_astra:desh_block
    // ad_astra:etrium_block
    // ad_astra:ostrum_block
    // ad_astra:steel_block
    //
    // advancednetherite:netherite_diamond_block
    // advancednetherite:netherite_emerald_block
    // advancednetherite:netherite_gold_block
    // advancednetherite:netherite_iron_block
    //
    // betterend:aeternium_block
    // betterend:terminite_block
    // betterend:thallasium_block
    //
    // betternether:nether_ruby_block
    //
    // bewitchment:silver_block
    //
    // indrev:silver_block
    //
    // techreborn:silver_storage_block
    //
    // create:brass_block
    // create:experience_block
    // create:zinc_block
    //
    // mythicmetals:adamantite_block
    // mythicmetals:aquarium_block
    // mythicmetals:banglum_block
    // mythicmetals:bronze_block
    // mythicmetals:carmot_block
    // mythicmetals:celestium_block
    // mythicmetals:durasteel_block
    // mythicmetals:hallowed_block
    // mythicmetals:kyber_block
    // mythicmetals:manganese_block
    // mythicmetals:metallurgium_block
    // mythicmetals:midas_gold_block
    // mythicmetals:mythril_block
    // mythicmetals:orichalcum_block
    // mythicmetals:osmium_block
    // mythicmetals:palladium_block
    // mythicmetals:platinum_block
    // mythicmetals:prometheum_block
    // mythicmetals:quadrillum_block
    // mythicmetals:runite_block
    // mythicmetals:silver_block
    // mythicmetals:star_platinum_block
    // mythicmetals:starrite_block
    // mythicmetals:steel_block
    // mythicmetals:stormyx_block
    // mythicmetals:unobtainium_block
    //
    // winterly:cryomarble_block

    // aether:enchanted_gravitite
    // aether:zanite_block
    //
    // allthemodium:allthemodium_block
    // allthemodium:unobtainium_block
    // allthemodium:vibranium_block
    //
    // alltheores:aluminum_block
    // alltheores:lead_block
    // alltheores:nickel_block
    // alltheores:osmium_block
    // alltheores:platinum_block
    // alltheores:silver_block
    // alltheores:tin_block
    // alltheores:uranium_block
    // alltheores:zinc_block
    //
    // blockus:legacy_diamond_block
    // blockus:legacy_explosion_proof_gold_block
    // blockus:legacy_gold_block
    // blockus:legacy_iron_block
    // blockus:nether_stars_block
    // blockus:netherite_slab
    // blockus:netherite_stairs
    //
    // bloodmagic:dungeon_metal
    //
    // botania:dragonstone_block
    // botania:elementium_block
    // botania:mana_diamond_block
    // botania:manasteel_block
    // botania:terrasteel_block
    //
    // byg:ametrine_block
    // byg:pendorite_block
    //
    // elementalcraft:drenched_iron_block
    // elementalcraft:fireite_block
    // elementalcraft:swift_alloy_block
    //
    // enlightened_end:adamantite_plate_block
    // enlightened_end:voidsteel_block
    //
    // evilcraft:dark_block
    // evilcraft:dark_power_gem_block
    //
    // mekanism:block_bronze
    // mekanism:block_lead
    // mekanism:block_osmium
    // mekanism:block_refined_glowstone
    // mekanism:block_refined_obsidian
    // mekanism:block_steel
    // mekanism:block_tin
    // mekanism:block_uranium
    //
    // mythicbotany:alfsteel_block
    //
    // securitycraft:reinforced_diamond_block
    // securitycraft:reinforced_emerald_block
    // securitycraft:reinforced_gold_block
    // securitycraft:reinforced_iron_block
    // securitycraft:reinforced_netherite_block
    //
    // spectrum:azurite_block
    // spectrum:bismuth_block
    // spectrum:malachite_block
    // spectrum:shimmerstone_block
    //
    // thermal:bronze_block
    // thermal:constantan_block
    // thermal:electrum_block
    // thermal:invar_block
    // thermal:lead_block
    // thermal:nickel_block
    // thermal:rose_gold_block
    // thermal:ruby_block
    // thermal:sapphire_block
    // thermal:silver_block
    // thermal:steel_block
    // thermal:tin_block
    //
    // undergarden:cloggrum_block
    // undergarden:forgotten_block
    // undergarden:froststeel_block
    // undergarden:regalium_block
    // undergarden:utherium_block
    //
    // xps:block_soul_copper

    val DEFAULT_BEACON_CONFIG = BeaconOverhauledConfig(
        additionModifiers = mapOf(
            identifierOf("minecraft:copper_block") to "(blocks)^0.45 * 2",
            identifierOf("minecraft:iron_block") to "(blocks)^0.6 * 2",
            identifierOf("minecraft:gold_block") to "(blocks)^0.95 * 0.5",
            identifierOf("minecraft:amethyst_block") to "min(blocks, 8) * 8",
            identifierOf("minecraft:emerald_block") to "(blocks)^0.95",
            identifierOf("minecraft:diamond_block") to "(blocks)^0.75 * 5",
        ).map { it.key to BeaconBlockExpression(it.value) }.toMap(),
        multiplicationModifiers = mapOf(
            Blocks.NETHERITE_BLOCK to "1 + (blocks * 0.05)",
        ).map { it.key.id to BeaconBlockExpression(it.value) }.toMap(),
        range = BeaconModifierExpression("min(10 + pts * 2, 4096)"),
        duration = BeaconModifierExpression("10 + pts / 15"),
        primaryAmplifier = BeaconEffectAmplifierExpression("if(pts > 256, if(pts > 512, 3, 2), 1) + isPotent"),
        secondaryAmplifier = BeaconEffectAmplifierExpression("1"),
        maxBeaconLayers = 6,
        levelOneStatusEffects = listOf(
            StatusEffects.FIRE_RESISTANCE,
            StatusEffects.SLOW_FALLING,
            StatusEffects.NIGHT_VISION,
        ),
        beaconBaseBlocks = listOf(
            Blocks.COPPER_BLOCK,
            Blocks.IRON_BLOCK,
            Blocks.GOLD_BLOCK,
            Blocks.AMETHYST_BLOCK,
            Blocks.EMERALD_BLOCK,
            Blocks.DIAMOND_BLOCK,
            Blocks.NETHERITE_BLOCK,
        ),
        beaconEffectsByTier = BeaconTierEffects(
            tierOne = listOf(StatusEffects.SPEED, StatusEffects.HASTE),
            tierTwo = listOf(StatusEffects.RESISTANCE, StatusEffects.JUMP_BOOST, StatusEffectRegistry.LONG_REACH),
            tierThree = listOf(StatusEffects.STRENGTH, StatusEffectRegistry.NUTRITION, StatusEffects.HEALTH_BOOST),
            secondaryEffects = listOf(
                StatusEffects.REGENERATION,
                StatusEffects.FIRE_RESISTANCE,
                StatusEffects.SLOW_FALLING,
                StatusEffects.NIGHT_VISION,
            ),
        ),
        beaconUpdateDelay = 4.seconds,
        initialBeaconUpdateDelay = 0.5.seconds,
        effectParticles = false,
        redirectionHorizontalMoveLimit = 64,
        allowTintedGlassTransparency = true,
        beamUpdateFrequency = 4,
        beamRadius = 0.2,
        beamGlowRadius = 0.25,
        beamGlowOpacity = 0.125,
        beamBlendPadding = 0.125
    )

    val DEFAULT_CONDUIT_CONFIG = ConduitConfig(
        test = true
    )
}

@file:Suppress("UnstableApiUsage")

import org.apache.commons.text.StringEscapeUtils


plugins {
    `maven-publish`

    alias(libs.plugins.fabric.loom)

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.axion.release)

    alias(libs.plugins.nyx)
}

fabricApi {
    configureDataGeneration()
}

nyx {
    compile {
        // withJavadocJar()
        withSourcesJar()

        allWarnings = true
        // warningsAsErrors = true
        distributeLicense = true
        buildDependsOnJar = true
        jvmTarget = 17
        reproducibleBuilds = true

        kotlin {
            compilerArgs.add("-Xcontext-receivers")
            optIn.add("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }

    project {
        name = "Beacon Overhaul Reloaded"
        group = "gay.solonovamax"
        module = "beacon-overhaul-reloaded"
        version = scmVersion.version
        description = """
            A mod for Minecraft that improves game mechanics around beacons

            - Night vision, fire resistance, nutrition, long reach, slow falling, and health boost are new effects that can be given by a beacon.
            - A points system for beacons, with where more expensive materials grant a more potent effect.
            - Higher potency of night vision, allowing the player to see everything with midday lighting (full bright) and no fog effects.
            - 2 new effects, Long Reach, which increases interaction reach, and Nutrition, which passively restoring food levels.
            - Adds a dropping mechanic to slow falling, allowing sneaking to cause a fall at normal velocity whilst still negating damage.
            - Increases step height with jump boost, allowing the player to step up blocks instantaneously when the effect is applied.
              - Note: Auto-jump takes precedence, and will need to be disabled for this to have any effect.
            - Beacon beam redirection using amethyst clusters. (Credit to vazkii & Quark contributors)
            - Smooths out beacon beam colour changes
            - Use tinted glass to make the beacon beam transparent
            - More blocks can be used in the beacon base: Copper & Amethyst
            - New & Improved beacon UI
            - Extremely configurable
        """.trimIndent()

        developer {
            id = "solonovamax"
            name = "solonovamax"
            email = "solonovamax@12oclockpoint.com"
            url = "https://solonovamax.gay"
        }
        developer {
            id = "ChloeDawn"
            name = "Chloe Dawn"
            email = "chloe@sapphic.dev"
            url = "https://github.com/ChloeDawn"
        }

        repository.fromGithub("solonovamax", "BeaconOverhaulReloaded")
        license.useApachev2()
    }

    minecraft {
        accessWidener("beaconoverhaulreloaded")

        mixin {
            hotswapMixins = true
            debug = false
            verbose = true
            dumpTargetOnFailure = true
            checks = false
            verify = false

            mixinRefmapName(name)
        }
    }
}

repositories {
    maven("https://maven.solo-studios.ca/releases/") {
        name = "Solo Studios"
    }
    maven("https://maven.fabricmc.net/") {
        name = "Fabric"
        content {
            includeGroupAndSubgroups("net.fabricmc")
            includeModule("me.zeroeightsix", "fiber")
            includeModule("io.github.llamalad7", "mixinextras-fabric")
        }
    }
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft)

    // the last item has the highest priority
    mappings(loom.layered {
        mappings(variantOf(libs.quilt.mappings) { classifier("intermediary-v2") })
        mappings(variantOf(libs.yarn.mappings) { classifier("v2") })
    })

    modImplementation(libs.fabric.loader)

    implementation("org.checkerframework:checker-qual:3.35.0")

    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.language.kotlin)


    annotationProcessor(libs.sponge.mixin)

    // modImplementation(libs.bundles.cloud) {
    //     exclude(group = "net.fabricmc.fabric-api")
    //     include(this)
    // }
    modImplementationInclude(libs.bundles.silk) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    implementationInclude(libs.slf4k)
    implementationInclude(libs.guava.kotlin)

    implementationInclude(libs.paralithic)
    implementationInclude(libs.colormath)

    modImplementationInclude(libs.cloth.config) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    modImplementationInclude(libs.entityAttributes.reach) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    modImplementationInclude(libs.arrp) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    modImplementationInclude(libs.patchouli) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    modImplementation(libs.lavender)
    modImplementation(libs.owo.lib)
    include(libs.owo.sentinel)

    modImplementation(libs.modmenu)

    modCompileOnly(libs.emi)

    modCompileOnly(libs.bundles.rei) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    modCompileOnly(libs.jei.fabric) {
        exclude(group = "mezz.jei")
    }
}

tasks {
    val runDatagen by named("runDatagen")

    processResources {
        filesMatching("/fabric.mod.json") {
            expand(
                "description" to StringEscapeUtils.escapeJson(project.description),
                "version" to project.version,
                "versions" to mapOf(
                    "fabric" to mapOf(
                        "api" to libs.versions.fabric.api.get(),
                        "loader" to libs.versions.fabric.loader.get(),
                        "languageKotlin" to libs.versions.fabric.language.kotlin.get(),
                    ),
                    "arrp" to libs.versions.arrp.get(),
                    "clothconfig" to libs.versions.cloth.config.get(),
                    "minecraft" to libs.versions.minecraft.get(),
                    "reachEntityAttributes" to libs.versions.reach.entity.attributes.get(),
                    "silk" to libs.versions.silk.get(),
                    "patchouli" to libs.versions.patchouli.get(),
                ),
            )
        }
    }

    jar {
        dependsOn(runDatagen)
    }
    runClient {
        dependsOn(runDatagen)
    }
    runServer {
        dependsOn(runDatagen)
    }
}


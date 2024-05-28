@file:Suppress("UnstableApiUsage")

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
            Introduces a tier system and better effect scaling for beacons
            - Adds night vision, fire resistance, nutrition, long reach, and slow falling as beacon effects
            - Adds a tier system for beacons, with diamond and netherite structures providing progressively increased effect potency
            - Adds a higher potency of night vision, allowing the player to see everything with midday lighting (full bright) and no fog effects
            - Adds 2 new effects to the game, Long Reach, increasing interaction reach, and Nutrition, passively restoring food levels
            - Adds a dropping mechanic to slow falling, allowing sneaking to cause a fall at normal velocity whilst still negating damage
            - Adds an increased step height to jump boost, allowing the player to step up blocks instantaneously when the effect is applied (Note: Auto-jump takes precedence, and will need to be disabled for this to have any effect)
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
    modImplementationInclude(libs.owo.sentinel)

    modImplementation(libs.modmenu)

    modCompileOnly(libs.emi)

    modCompileOnly(libs.bundles.rei) {
        exclude(group = "net.fabricmc.fabric-api")
    }
    // modCompileOnlyApi(libs.jei.common.api)
    modCompileOnly(libs.jei.fabric) {
        exclude(group = "mezz.jei")
    }
}

tasks {
    val runDatagen by named("runDatagen")

    processResources {
        filesMatching("/fabric.mod.json") {
            expand(
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


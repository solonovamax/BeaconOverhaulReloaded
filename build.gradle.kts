@file:Suppress("UnstableApiUsage")

import ca.solostudios.nyx.plugin.minecraft.NyxMinotaurExtension.VersionType
import ca.solostudios.nyx.util.fabric
import ca.solostudios.nyx.util.soloStudios
import net.fabricmc.loom.task.RunGameTask
import org.apache.commons.text.StringEscapeUtils

plugins {
    `maven-publish`

    alias(libs.plugins.fabric.loom)

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.axion.release)

    alias(libs.plugins.nyx)

    alias(libs.plugins.minotaur)
}

nyx {
    compile {
        // javadocJar = true
        sourcesJar = true

        allWarnings = true
        // warningsAsErrors = true
        distributeLicense = true
        buildDependsOnJar = true
        jvmTarget = 21
        reproducibleBuilds = true

        kotlin {
            compilerArgs.add("-Xcontext-receivers")
            optIn.add("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }

    info {
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
        configureDataGeneration {
            createSourceSet = true
            // strictValidation = true
            modId = "beaconoverhaul-datagen"
        }

        accessWidener("beaconoverhaul")

        additionalJvmProperties.putAll(
            mapOf(
                "fabric-tag-conventions-v2.missingTagTranslationWarning" to "FAIL",
                "fabric-tag-conventions-v1.legacyTagWarning" to "FAIL"
            )
        )

        mixin {
            hotswap = true
            verbose = true
            export = true

            mixinRefmapName("beaconoverhaul")
        }
        minotaur {
            versionType = if (isSnapshot) VersionType.ALPHA else VersionType.BETA
            projectId = "beacon-overhaul-reloaded"
            detectLoaders = true
            dependencies {
                required("fabric-api")
                required("fabric-language-kotlin")
                required("lavender")
                required("owo-lib")
                required("geckolib")

                embedded("silk")
                // embedded("arrp")

                optional("modmenu")

                optional("emi")
                optional("rei")
                optional("jei")
            }
        }
    }
}

repositories {
    mavenLocal()
    soloStudios()
    fabric()
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft)

    // the last item has the highest priority
    mappings(loom.layered {
        mappings(variantOf(libs.yarn.mappings) { classifier("v2") })
    })

    modImplementation(libs.fabric.loader)

    implementation("org.checkerframework:checker-qual:3.35.0")

    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.language.kotlin)


    annotationProcessor(libs.sponge.mixin)
    implementation(libs.sponge.mixin)
    annotationProcessor(libs.mixinextras)
    implementation(libs.mixinextras)

    // modImplementationInclude(libs.bundles.cloud) {
    //     exclude(group = "net.fabricmc.fabric-api")
    // }

    modImplementationInclude(libs.bundles.silk) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    implementation(libs.kotlinx.serialization.json)
    implementationInclude(libs.kotlinx.serialization.hocon)
    implementationInclude(libs.kotlinx.serialization.json5k)

    implementation(libs.bundles.kotlinx.coroutines)

    implementationInclude(libs.slf4k)
    implementationInclude(libs.guava.kotlin)

    implementationInclude(libs.korlibs.io)
    implementationInclude(libs.korlibs.io.fs)

    implementationInclude(libs.paralithic)

    implementationInclude(libs.colormath)

    modImplementation(libs.yacl)

    // modImplementationInclude(libs.arrp) {
    //     exclude(group = "net.fabricmc.fabric-api")
    // }

    modImplementation(libs.lavender)
    modImplementation(libs.owo.lib)
    include(libs.owo.sentinel)

    // modImplementation(libs.azurelib.fabric)

    implementation("com.eliotlash.mclib:mclib:20")
    modImplementation(libs.geckolib.fabric)

    modImplementation(libs.modmenu)

    modCompileOnly(libs.emi)

    modApi(libs.bundles.rei)
    modCompileOnly(libs.bundles.rei) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    modCompileOnly(libs.jei.fabric) {
        exclude(group = "mezz.jei")
    }
}

tasks {
    val runDatagen by named<RunGameTask>("runDatagen")

    java.sourceSets.configureEach {
        val processResources by named<ProcessResources>(processResourcesTaskName) {
            val expansion = mapOf(
                "description" to StringEscapeUtils.escapeJson(project.description),
                "version" to project.version,
                "versions" to mapOf(
                    "fabric" to mapOf(
                        "api" to libs.versions.fabric.api.get(),
                        "loader" to libs.versions.fabric.loader.get(),
                        "languageKotlin" to libs.versions.fabric.language.kotlin.get(),
                    ),
                    "minecraft" to libs.versions.minecraft.get(),
                    "silk" to libs.versions.silk.get(),
                    "owo" to libs.versions.owo.get(),
                    "lavender" to libs.versions.lavender.get(),
                ),
            )
            inputs.property("expansions", expansion)
            filesMatching("/fabric.mod.json") {
                expand(expansion)
            }
        }
    }

    withType<RunGameTask>().matching { it != runDatagen }.configureEach {
        dependsOn(runDatagen)
    }
    withType<Jar>().configureEach {
        dependsOn(runDatagen)
    }
}

val Project.isSnapshot: Boolean
    get() = version.toString().endsWith("-SNAPSHOT")


// val TARGET_MINECRAFT_VERSION_ATTRIBUTE = Attribute.of("net.minecraft.version", String::class.java)
//
// dependencies.attributesSchema {
//     attribute(TARGET_MINECRAFT_VERSION_ATTRIBUTE) {
//         // disambiguationRules.add(TestDisambiguationRule::class)
//     }
//     // getMatchingStrategy(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE).disambiguationRules.add(TestDisambiguationRuleInt::class)
// }
//
// // class TestDisambiguationRule : AttributeDisambiguationRule<String> {
// //     override fun execute(details: MultipleCandidatesDetails<String>) {
// //         println("candidate values = ${details.candidateValues}, consumer value = ${details.consumerValue}")
// //         if (details.consumerValue == null) {
// //             for (candidate in details.candidateValues) {
// //                 if (candidate == loom.minecraftVersion.get()) {
// //
// //                 }
// //             }
// //         }
// //     }
// // }
//
// // class TestDisambiguationRuleInt : AttributeDisambiguationRule<Int> {
// //     override fun execute(details: MultipleCandidatesDetails<Int>) {
// //         println("candidate values = ${details.candidateValues}, consumer value = ${details.consumerValue}")
// //     }
// // }
//
// configurations.configureEach {
//     if (name == "archives" || name == "default")
//         return@configureEach
//
//     attributes {
//         attribute(TARGET_MINECRAFT_VERSION_ATTRIBUTE, "1.20.6")
//         // attributeProvider(TARGET_MINECRAFT_VERSION_ATTRIBUTE, loom.minecraftVersion)
//     }
// }
//
// // loom.remapConfigurations.configureEach {
// //     sourceConfiguration.configure {
// //         attributes {
// //             attributeProvider(TARGET_MINECRAFT_VERSION_ATTRIBUTE, loom.minecraftVersion)
// //         }
// //     }
// //     configurations.findByName(remappedConfigurationName)?.apply {
// //         attributes {
// //             attributeProvider(TARGET_MINECRAFT_VERSION_ATTRIBUTE, loom.minecraftVersion)
// //         }
// //     }
// // }

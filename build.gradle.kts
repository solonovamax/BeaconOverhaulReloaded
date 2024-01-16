import java.time.Instant

plugins {
    `maven-publish`

    alias(libs.plugins.fabric.loom)

    alias(libs.plugins.kotlin.jvm)

    alias(libs.plugins.kotlin.serialization)
}

group = "gay.solonovamax"
version = "2.0.0+1.20"

repositories {
    mavenCentral()
    maven("https://maven.solo-studios.ca/releases/") {
        name = "Solo Studios"
    }
    maven("https://maven.fabricmc.net/") {
        name = "FabricMC"
    }
    maven("https://masa.dy.fi/maven") {
        name = "Masa Modding"
    }
    maven("https://maven.shedaniel.me/") {
        name = "Shedaniel"
    }
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://maven.wispforest.io") {
        name = "Wisp Forest"
    }
    maven("https://ueaj.dev/maven")
    maven("https://maven.jamieswhiteshirt.com/libs-release") {
        content {
            includeGroup("com.jamieswhiteshirt")
        }
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots") {
        mavenContent {
            snapshotsOnly()
        }
    }
}

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain(17)
}

loom {
    accessWidenerPath = sourceSets["main"].resources.srcDirs.map { it.resolve("beaconoverhaulreloaded.accesswidener") }
        .first { it.exists() }

    mixin {
        defaultRefmapName.set("mixins/beaconoverhaul/refmap.json")
    }
}

dependencies {
    minecraft(libs.minecraft)

    mappings(variantOf(libs.yarn.mappings) { classifier("v2") })
    // mappings(loom.layered {
    //     officialMojangMappings {
    //         nameSyntheticMembers = true
    //     }
    // })

    modImplementation(libs.fabric.loader)

    implementation("org.checkerframework:checker-qual:3.35.0")

    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.language.kotlin)


    annotationProcessor(libs.sponge.mixin)

    // fun fabricApiModule(moduleName: String): Dependency =
    //     fabricApi.module(moduleName, "0.85.0+1.20.1")
    // modImplementation(include(fabricApiModule("fabric-api-base"))!!)
    // modImplementation(include(fabricApiModule("fabric-networking-api-v1"))!!)
    // modImplementation(include(fabricApiModule("fabric-registry-sync-v0"))!!)
    // modImplementation(include(fabricApiModule("fabric-resource-loader-v0"))!!)

    // modImplementation(libs.bundles.adventure) {
    //     exclude(group = "net.fabricmc.fabric-api")
    //     include(this)
    // }
    //
    // modImplementation(libs.bundles.cloud) {
    //     exclude(group = "net.fabricmc.fabric-api")
    //     include(this)
    // }
    modImplementation(libs.bundles.silk) {
        include(this)
    }

    implementation(libs.slf4k) {
        include(this)
    }

    implementation(libs.guava.kotlin) {
        include(this)
    }

    implementation(libs.paralithic) {
        include(this)
    }

    modImplementation(libs.cloth.config) {
        exclude(group = "net.fabricmc.fabric-api")
    }

    modImplementation(libs.entityAttributes.reach) {
        exclude(group = "net.fabricmc.fabric-api")
        include(this)
    }

    modImplementation(libs.arrp) {
        exclude(group = "net.fabricmc.fabric-api")
        modLocalRuntime(this)
    }

    modLocalRuntime(libs.modmenu)
}

tasks {
    withType<JavaCompile>().configureEach {
        with(options) {
            isDeprecation = true
            encoding = "UTF-8"
            isFork = true
            compilerArgs.add("-Xlint:all")
        }
    }


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
                )
            )
        }
    }

    withType<Jar>().configureEach {
        from("LICENSE") {
            rename { "${it}_${rootProject.name}" }
        }

        manifest.attributes(
            "Build-Timestamp" to Instant.now(),
            // "Build-Revision" to versioning.info.commit,
            "Build-Jvm" to "${
                System.getProperty("java.version")
            } (${
                System.getProperty("java.vendor")
            } ${
                System.getProperty("java.vm.version")
            })",
            "Built-By" to GradleVersion.current(),

            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to project.group,

            "Specification-Title" to "FabricMod",
            "Specification-Version" to "1.0.0",
            "Specification-Vendor" to project.group,

            "Sealed" to "true"
        )
    }
}

afterEvaluate {
    loom {
        runs {
            configureEach {
                vmArgs("-Xmx2G", "-XX:+UseShenandoahGC")

                property("fabric.development", "true")
                property("mixin.debug", "true")
                property("mixin.debug.export.decompile", "false")
                property("mixin.debug.verbose", "true")
                property("mixin.dumpTargetOnFailure", "true")
                property("paralithic.debug.dump", "true")
                // makes silent failures into hard-failures
                // property("mixin.checks", "true")
                // property("mixin.hotSwap", "true")

                val mixinJarFile = configurations.compileClasspath.get().files {
                    it.group == "net.fabricmc" && it.name == "sponge-mixin"
                }.firstOrNull()
                if (mixinJarFile != null)
                    vmArg("-javaagent:$mixinJarFile")

                ideConfigGenerated(true)
            }
        }
    }
}

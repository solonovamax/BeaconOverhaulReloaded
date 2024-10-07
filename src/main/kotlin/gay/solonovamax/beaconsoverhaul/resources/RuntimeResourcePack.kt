package gay.solonovamax.beaconsoverhaul.resources

import gay.solonovamax.beaconsoverhaul.BeaconConstants
import net.minecraft.resource.InputSupplier
import net.minecraft.resource.ResourcePack
import net.minecraft.resource.ResourcePackInfo
import net.minecraft.resource.ResourcePackSource
import net.minecraft.resource.ResourceType
import net.minecraft.resource.metadata.ResourceMetadataReader
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.io.InputStream
import java.util.Optional

class RuntimeResourcePack : ResourcePack {
    private val info = ResourcePackInfo(
        "Runtime ${BeaconConstants.MOD_NAME} Resources",
        Text.literal("Runtime ${BeaconConstants.MOD_NAME} Resources"),
        ResourcePackSource.BUILTIN,
        Optional.empty(),
    )

    override fun openRoot(vararg segments: String): InputSupplier<InputStream>? {
        TODO("Not yet implemented")
    }

    override fun open(type: ResourceType, id: Identifier): InputSupplier<InputStream>? {
        TODO("Not yet implemented")
    }

    override fun findResources(type: ResourceType, namespace: String, prefix: String, consumer: ResourcePack.ResultConsumer) {
        TODO("Not yet implemented")
    }

    override fun getNamespaces(type: ResourceType): MutableSet<String> {
        TODO("Not yet implemented")
    }

    override fun <T> parseMetadata(metaReader: ResourceMetadataReader<T>): T? {
        TODO("Not yet implemented")
    }

    override fun getInfo(): ResourcePackInfo = info

    // override fun getName(): String = "Runtime ${BeaconConstants.MOD_NAME} Resources"

    // override fun isAlwaysStable() = true

    override fun close() {
        TODO("Not yet implemented")
    }
}

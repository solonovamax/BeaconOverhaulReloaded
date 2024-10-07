package gay.solonovamax.beaconsoverhaul.datagen.lang

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import gay.solonovamax.beaconsoverhaul.BeaconConstants
import gay.solonovamax.beaconsoverhaul.datagen.BeaconOverhaulReloadedDataGenerator
import gay.solonovamax.beaconsoverhaul.datagen.util.asTranslationKey
import gay.solonovamax.beaconsoverhaul.datagen.util.flattenTranslation
import gay.solonovamax.beaconsoverhaul.util.identifierOf
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.block.Block
import net.minecraft.data.DataOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.DataWriter
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EntityType
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.stat.StatType
import net.minecraft.text.TranslatableTextContent
import net.minecraft.util.Identifier
import net.silkmc.silk.core.annotations.DelicateSilkApi
import net.silkmc.silk.core.task.silkCoroutineScope
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.bufferedReader
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.walk

class LanguageProvider(
    private val output: FabricDataOutput,
) : DataProvider {
    @OptIn(ExperimentalPathApi::class)
    private fun generateTranslations(langPath: Path, builder: (translationKey: String, value: JsonElement) -> Unit) {
        val translationBuilder = object : TranslationBuilder() {
            override fun add(translationKey: String, value: JsonElement) {
                builder(translationKey, value)
            }
        }
        langPath.walk().filter {
            it.isRegularFile()
        }.forEach(translationBuilder::add)
    }

    @OptIn(DelicateSilkApi::class)
    override fun run(writer: DataWriter): CompletableFuture<*> {
        return silkCoroutineScope.launch {
            val langRoot = BeaconOverhaulReloadedDataGenerator.MOD.findPath("assets/${BeaconConstants.NAMESPACE}/lang-partial/").get()
            val languageCodes = langRoot.listDirectoryEntries().filter {
                it.isDirectory()
            }

            for (code in languageCodes) {
                launch {
                    val translationEntries = sortedMapOf<String, JsonElement>()

                    generateTranslations(code) { key, value ->
                        if (translationEntries.containsKey(key))
                            throw RuntimeException("Existing translation key found - $key - Duplicate will be ignored.")
                        else
                            translationEntries[key] = value
                    }

                    val langEntryJson = JsonObject()

                    for ((key, value) in translationEntries) {
                        langEntryJson.add(key, value)
                    }

                    DataProvider.writeToPath(writer, langEntryJson, getLangFilePath(code.name)).join()
                }
            }
        }.asCompletableFuture()
    }

    private fun getLangFilePath(code: String): Path {
        return output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "lang").resolveJson(identifierOf(output.modId, code))
    }

    override fun getName(): String {
        return "Language"
    }

    abstract class TranslationBuilder {
        abstract fun add(translationKey: String, value: JsonElement)

        fun add(translationKey: String, value: String) {
            add(translationKey, JsonPrimitive(value))
        }

        fun add(item: Item, value: String) {
            add(item.translationKey, value)
        }

        fun add(block: Block, value: String) {
            add(block.translationKey, value)
        }

        @JvmName("addItemGroup")
        fun add(registryKey: RegistryKey<ItemGroup>, value: String) {
            val group = Registries.ITEM_GROUP.getOrThrow(registryKey)
            val content = group.displayName.content

            if (content is TranslatableTextContent)
                add(content.key, value)
            else
                throw UnsupportedOperationException("Cannot add language entry for ItemGroup (${group.displayName.string}) as the display name is not translatable.")
        }

        fun add(entityType: EntityType<*>, value: String) {
            add(entityType.translationKey, value)
        }

        @JvmName("addEnchantment")
        fun add(enchantment: RegistryKey<Enchantment>, value: String) {
            add(enchantment.asTranslationKey("enchantment"), value)
        }

        @JvmName("addEntityAttribute")
        fun add(entityAttribute: RegistryEntry<EntityAttribute>, value: String) {
            add(entityAttribute.value().translationKey, value)
        }

        fun add(statType: StatType<*>, value: String) {
            add(Registries.STAT_TYPE.getId(statType).asTranslationKey("stat_type"), value)
        }

        fun add(statusEffect: StatusEffect, value: String) {
            add(statusEffect.translationKey, value)
        }

        fun add(identifier: Identifier, value: String) {
            add(identifier.toTranslationKey(), value)
        }

        fun add(existingLanguageFile: Path) {
            existingLanguageFile.bufferedReader().use { reader ->
                val translationObject = JsonParser.parseReader(reader).asJsonObject
                var prefix = ""
                val translations = when {
                    translationObject.has("translations") -> {
                        prefix = translationObject["prefix"].asString + "."
                        val translations = translationObject["translations"].asJsonObject
                        translations.asMap()
                    }

                    else -> {
                        translationObject.asMap()
                    }
                }

                for ((key, value) in translations) {
                    flattenTranslation(value, key) { k, v ->
                        add("$prefix$k", v)
                    }
                }

            }
        }
    }
}

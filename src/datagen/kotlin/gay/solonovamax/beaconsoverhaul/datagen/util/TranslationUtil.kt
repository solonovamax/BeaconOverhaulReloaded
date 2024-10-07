package gay.solonovamax.beaconsoverhaul.datagen.util

import com.google.gson.JsonElement
import gay.solonovamax.beaconsoverhaul.BeaconConstants
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import net.minecraft.util.Language
import net.minecraft.util.Util
import java.util.function.BiConsumer

fun RegistryKey<*>.asTranslationKey(type: String): String = Util.createTranslationKey(type, value)
fun Identifier?.asTranslationKey(type: String): String = Util.createTranslationKey(type, this)

fun flattenTranslation(curElement: JsonElement, keyPart: String, add: BiConsumer<String, JsonElement>) {
    if (curElement.isJsonObject) {
        for (entry in curElement.getAsJsonObject().entrySet()) {
            val key = when {
                keyPart.isEmpty() -> entry.key
                entry.key == "_" -> keyPart
                else -> "$keyPart.${entry.key}"
            }
            flattenTranslation(entry.value, key, add)
        }
    } else {
        add.accept(keyPart, curElement)
    }
}

context(Registry<T>)
fun <T> MutableSet<String>.collectMissingTranslations(keyExtractor: (T) -> List<String>) {
    val language = Language.getInstance()
    for (entry in this@Registry.entrySet) {
        if (entry.key.value.namespace != BeaconConstants.NAMESPACE)
            continue

        val translations = keyExtractor(entry.value)
        for (translation in translations) {
            if (!language.hasTranslation(translation)) {
                this@MutableSet.add(translation)
            }
        }
    }
}

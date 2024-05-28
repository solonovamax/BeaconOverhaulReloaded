package gay.solonovamax.beaconsoverhaul.util

import io.wispforest.lavender.md.compiler.BookCompiler.ComponentSource
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.ParentComponent
import io.wispforest.owo.ui.parsing.UIModel
import net.minecraft.util.Identifier

inline fun <reified T : Component> ComponentSource.template(
    id: Identifier,
    name: String,
    params: Map<String, String> = mapOf(),
): T = template(id, T::class.java, name, params)

inline fun <reified T : Component> ComponentSource.template(
    model: UIModel,
    name: String,
    params: Map<String, String> = mapOf(),
): T = template(model, T::class.java, name, params)

inline fun <reified T : Component> ComponentSource.builtinTemplate(
    name: String,
    params: Map<String, String> = mapOf(),
): T = builtinTemplate(T::class.java, name, params)


inline fun <reified T : Component> ParentComponent.childById(id: String): T? = childById(T::class.java, id)


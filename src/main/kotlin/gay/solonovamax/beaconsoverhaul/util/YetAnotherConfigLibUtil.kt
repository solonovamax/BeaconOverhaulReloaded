package gay.solonovamax.beaconsoverhaul.util

import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.ListOption
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.dsl.Buildable
import dev.isxander.yacl3.dsl.ButtonOptionDsl
import dev.isxander.yacl3.dsl.GroupDsl
import dev.isxander.yacl3.dsl.OptionDsl
import dev.isxander.yacl3.dsl.OptionRegistrar
import dev.isxander.yacl3.dsl.OptionRegistrarImpl
import dev.isxander.yacl3.dsl.RegisterableActionDelegateProvider
import dev.isxander.yacl3.dsl.RegisterableDelegateProvider
import dev.isxander.yacl3.dsl.TextLineBuilderDsl
import dev.isxander.yacl3.dsl.addDefaultText
import dev.isxander.yacl3.dsl.descriptionBuilder
import net.minecraft.text.Text
import java.util.concurrent.CompletableFuture
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun OptionRegistrar.registeringLabel(id: String?, builder: TextLineBuilderDsl.() -> Unit): RegisterableDelegateProvider<LabelOption> {
    return RegisterableDelegateProvider(this::registerLabel, id)
}

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalTypeInference::class)
fun <T> OptionRegistrar.registerList(id: String, @BuilderInference block: ListOptionDsl<T>.() -> Unit): ListOption<T> {
    val registrarClass = OptionRegistrarImpl::class
    val groupKeyProperty = registrarClass.memberProperties.first { it.name == "groupKey" } as KProperty1<OptionRegistrarImpl, String>
    groupKeyProperty.isAccessible = true
    return register(id, ListOptionDslImpl<T>(id, groupKeyProperty.get(this as OptionRegistrarImpl)).apply(block).build())
}

fun <T> OptionRegistrar.registeringList(
    id: String? = null,
    block: ListOptionDsl<T>.() -> Unit,
): RegisterableActionDelegateProvider<ListOptionDsl<T>, ListOption<T>> {
    return RegisterableActionDelegateProvider(this::registerList, block, id)
}

fun <T : Any> ListOption.Builder<T>.binding(property: KMutableProperty0<List<T>>, default: List<T>) {
    binding(default, { property.get() }, { property.set(it) })
}

fun OptionDsl<*>.defaultDescription() {
    descriptionBuilder {
        addDefaultText()
    }
}

fun ButtonOptionDsl.defaultDescription() {
    descriptionBuilder {
        addDefaultText()
    }

}

fun GroupDsl.defaultDescription() {
    descriptionBuilder {
        addDefaultText()
    }
}

fun ListOptionDsl<*>.defaultDescription() {
    descriptionBuilder {
        addDefaultText()
    }
}

fun ListOption.Builder<*>.descriptionBuilder(block: OptionDescription.Builder.() -> Unit) {
    description(OptionDescription.createBuilder().apply(block).build())
}

interface ListOptionDsl<T> : ListOption.Builder<T>, Buildable<ListOption<T>> {
    val optionKey: String
    val optionId: String
    val thisOption: CompletableFuture<ListOption<T>>

    fun OptionDescription.Builder.addDefaultText(lines: Int? = null) = addDefaultText("$optionKey.description", lines)
}


class ListOptionDslImpl<T>(
    override val optionId: String,
    groupKey: String,
    private val builder: ListOption.Builder<T> = ListOption.createBuilder(),
) : ListOptionDsl<T>, ListOption.Builder<T> by builder {
    override val optionKey = "$groupKey.option.$optionId"

    override val thisOption = CompletableFuture<ListOption<T>>()
    override val built = thisOption

    init {
        builder.name(Text.translatable(optionKey))
    }

    override fun OptionDescription.Builder.addDefaultText(lines: Int?) = addDefaultText(prefix = "$optionKey.description", lines = lines)

    override fun build(): ListOption<T> = builder.build().also { thisOption.complete(it) }
}

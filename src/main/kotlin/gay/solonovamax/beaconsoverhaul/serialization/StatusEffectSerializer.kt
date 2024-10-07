package gay.solonovamax.beaconsoverhaul.serialization

import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.registry.Registries

class StatusEffectSerializer : RegistrySerializer<StatusEffect>(Registries.STATUS_EFFECT, StatusEffect::class) {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StatusEffect", PrimitiveKind.STRING)
}

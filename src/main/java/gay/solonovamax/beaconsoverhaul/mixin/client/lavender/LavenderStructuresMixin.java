package gay.solonovamax.beaconsoverhaul.mixin.client.lavender;

import gay.solonovamax.beaconsoverhaul.config.BeaconOverhaulConfigManager;
import gay.solonovamax.beaconsoverhaul.integration.lavender.LavenderStructuresKt;
import io.wispforest.lavender.structure.LavenderStructures;
import io.wispforest.lavender.structure.StructureTemplate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(LavenderStructures.class)
public class LavenderStructuresMixin {
    @Final
    @Shadow
    private static Map<Identifier, StructureTemplate> LOADED_STRUCTURES;

    @Inject(
            method = "tryParseStructures",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V", shift = At.Shift.AFTER),
            remap = false
    )
    private static void loadStructure(CallbackInfo ci) {
        int maxTier = BeaconOverhaulConfigManager.getConfig().getMaxBeaconLayers();

        for (int tier = 0; tier < maxTier; tier++) {
            StructureTemplate template = LavenderStructuresKt.createBeaconStructureTemplate(tier + 1);
            LavenderStructuresMixin.LOADED_STRUCTURES.put(template.id, template);
        }

        LavenderStructuresMixin.LOADED_STRUCTURES.put(LavenderStructuresKt.EMPTY_STRUCTURE_TEMPLATE.id, LavenderStructuresKt.EMPTY_STRUCTURE_TEMPLATE);
    }
}

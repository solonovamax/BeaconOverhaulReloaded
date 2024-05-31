package gay.solonovamax.beaconsoverhaul.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.TranslationStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(TranslationStorage.class)
public class TranslationStorageMixin {
    @Final
    @Shadow
    private Map<String, String> translations;

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void get(String key, String fallback, CallbackInfoReturnable<String> cir) {
        if (key.startsWith("structure.beaconoverhaul.structure_gen")) {
            int periodIndex = key.lastIndexOf('.');
            String structureKey = key.substring(0, periodIndex)
                    .replace("structure.beaconoverhaul.structure_gen", "structure.beaconoverhaul.structure");

            cir.setReturnValue(this.translations.getOrDefault(structureKey, fallback).formatted(key.substring(periodIndex + 1)));
        }
    }
}

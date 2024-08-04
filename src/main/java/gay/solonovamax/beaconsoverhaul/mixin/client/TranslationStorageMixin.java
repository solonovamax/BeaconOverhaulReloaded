package gay.solonovamax.beaconsoverhaul.mixin.client;

import gay.solonovamax.beaconsoverhaul.util.TranslationUtilKt;
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
        String translated = TranslationUtilKt.rewriteStructureTranslation(key, fallback, this.translations);
        if (translated != null) {
            cir.setReturnValue(translated);
        }
    }
}

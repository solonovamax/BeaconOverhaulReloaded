package gay.solonovamax.beaconsoverhaul.mixin.client.lavender;

import com.google.gson.JsonObject;
import gay.solonovamax.beaconsoverhaul.integration.lavender.LavenderStructuresKt;
import io.wispforest.lavender.structure.StructureTemplate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {
    @Inject(
            method = "parse",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void parse(Identifier resourceId, JsonObject json, CallbackInfoReturnable<StructureTemplate> cir) {
        cir.setReturnValue(LavenderStructuresKt.parseStructureTemplate(resourceId, json));
    }
}

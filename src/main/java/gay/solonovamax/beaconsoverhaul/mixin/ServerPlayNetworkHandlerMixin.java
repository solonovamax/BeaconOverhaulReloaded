package gay.solonovamax.beaconsoverhaul.mixin;

import gay.solonovamax.beaconsoverhaul.beacon.screen.OverhauledBeaconScreenHandler;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Final
    @Shadow
    static Logger LOGGER;
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onUpdateBeacon", at = @At("TAIL"))
    private void onUpdateBeacon(UpdateBeaconC2SPacket packet, CallbackInfo ci) {
        ScreenHandler screenHandler = this.player.currentScreenHandler;
        if (screenHandler instanceof OverhauledBeaconScreenHandler beaconScreenHandler) {
            if (!this.player.currentScreenHandler.canUse(this.player)) {
                LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.currentScreenHandler);
                return;
            }

            beaconScreenHandler.setEffects(packet.getPrimaryEffectId(), packet.getSecondaryEffectId());
        }
    }
}

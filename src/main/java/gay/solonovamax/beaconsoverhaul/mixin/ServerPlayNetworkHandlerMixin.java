package gay.solonovamax.beaconsoverhaul.mixin;

import gay.solonovamax.beaconsoverhaul.screen.OverhauledBeaconScreenHandler;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.slf4j.LoggerFactory.getLogger;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Unique
    private static final Logger logger = getLogger(ServerPlayNetworkHandlerMixin.class);

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onUpdateBeacon", at = @At("TAIL"))
    private void onUpdateBeacon(final UpdateBeaconC2SPacket packet, final CallbackInfo ci) {
        final ScreenHandler screenHandler = this.player.currentScreenHandler;
        if (screenHandler instanceof final OverhauledBeaconScreenHandler beaconScreenHandler) {
            if (!this.player.currentScreenHandler.canUse(this.player)) {
                ServerPlayNetworkHandlerMixin.logger.debug("Player {} interacted with invalid menu {}", this.player, this.player.currentScreenHandler);
                return;
            }

            beaconScreenHandler.setEffects(packet.getPrimaryEffectId(), packet.getSecondaryEffectId());
        }
    }
}

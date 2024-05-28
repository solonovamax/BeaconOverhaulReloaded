package gay.solonovamax.beaconsoverhaul.screen

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity

abstract class OverhauledScreenHandler<D>(
    syncId: Int,
    val player: PlayerEntity,
    var data: D,
    val delegate: PropertyDelegate,
    val context: ScreenHandlerContext,
    val onClose: (PlayerEntity) -> Unit,
    type: ScreenHandlerType<*>?,
) : ScreenHandler(type, syncId) {
    override fun onClosed(player: PlayerEntity) {
        super.onClosed(player)

        if (player is ServerPlayerEntity) {
            this.onClose(player)
        }
    }
}

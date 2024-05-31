package gay.solonovamax.beaconsoverhaul.screen

import gay.solonovamax.beaconsoverhaul.block.conduit.data.OverhauledConduitData
import gay.solonovamax.beaconsoverhaul.registry.ScreenHandlerRegistry
import gay.solonovamax.beaconsoverhaul.util.PropertyDelegate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandlerContext
import org.slf4j.kotlin.getLogger

class OverhauledConduitScreenHandler private constructor(
    syncId: Int,
    player: PlayerEntity,
    data: OverhauledConduitData,
    delegate: PropertyDelegate,
    context: ScreenHandlerContext,
    onClose: (PlayerEntity) -> Unit,
) : OverhauledScreenHandler<OverhauledConduitData>(
    syncId,
    player,
    data,
    delegate,
    context,
    onClose,
    ScreenHandlerRegistry.OVERHAULED_CONDUIT_SCREEN_HANDLER
) {
    private val logger by getLogger()

    /**
     * Client constructor
     */
    @OptIn(ExperimentalSerializationApi::class)
    constructor(
        syncId: Int,
        inv: PlayerInventory,
        buf: PacketByteBuf,
    ) : this(
        syncId,
        inv.player,
        Cbor.decodeFromByteArray(buf.readByteArray()),
        PropertyDelegate(PROPERTY_COUNT),
        ScreenHandlerContext.EMPTY,
        {},
    )

    /**
     * Server constructor
     */
    constructor(
        syncId: Int,
        player: PlayerEntity,
        propertyDelegate: PropertyDelegate,
        context: ScreenHandlerContext,
        onClosed: (PlayerEntity) -> Unit,
    ) : this(syncId, player, OverhauledConduitData.UNIT, propertyDelegate, context, onClosed)

    init {
        checkDataCount(delegate, PROPERTY_COUNT)
        addProperties(delegate)
    }

    override fun quickMove(player: PlayerEntity?, slot: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun canUse(player: PlayerEntity?): Boolean = canUse(this.context, player, Blocks.CONDUIT)

    override fun setProperty(id: Int, value: Int) {
        super.setProperty(id, value)
        this.sendContentUpdates()
    }

    companion object {
        private const val PROPERTY_COUNT = 0
    }
}

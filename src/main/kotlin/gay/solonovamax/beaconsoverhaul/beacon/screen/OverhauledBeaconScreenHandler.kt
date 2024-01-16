package gay.solonovamax.beaconsoverhaul.beacon.screen

import gay.solonovamax.beaconsoverhaul.beacon.serializable.OverhauledBeaconData
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import net.minecraft.block.Blocks
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.tag.ItemTags
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.world.World
import java.util.Optional

class OverhauledBeaconScreenHandler private constructor(
    syncId: Int,
    inventory: PlayerInventory,
    val beaconData: OverhauledBeaconData,
    val propertyDelegate: PropertyDelegate,
    val context: ScreenHandlerContext,
) : ScreenHandler(ScreenHandlerRegistry.OVERHAULED_BEACON_SCREEN_HANDLER, syncId) {
    private val PAYMENT_SLOT_ID = 0
    private val BEACON_INVENTORY_SIZE = 1
    private val PROPERTY_COUNT = 3
    private val INVENTORY_START = 1
    private val INVENTORY_END = 28
    private val HOTBAR_START = 28
    private val HOTBAR_END = 37
    private val payment: Inventory = PaymentInventory()


    /**
     * Client constructor
     */
    constructor(
        syncId: Int,
        inventory: PlayerInventory,
        buf: PacketByteBuf,
    ) : this(syncId, inventory, Cbor.decodeFromByteArray(buf.readByteArray()), ArrayPropertyDelegate(3), ScreenHandlerContext.EMPTY)

    /**
     * Server constructor
     */
    constructor(
        syncId: Int,
        inventory: PlayerInventory,
        propertyDelegate: PropertyDelegate,
        context: ScreenHandlerContext,
    ) : this(syncId, inventory, OverhauledBeaconData.EMPTY, propertyDelegate, context)

    /**
     * Magic number
     */
    private var paymentSlot: PaymentSlot = PaymentSlot(payment, 0, 136, 110)

    init {
        println("In screen handler with data $beaconData")
        checkDataCount(propertyDelegate, 3)
        addSlot(paymentSlot)
        addProperties(propertyDelegate)
        val i = 36
        val j = 137

        for (k in 0 until 3) {
            for (l in 0 until 9) {
                // Magic numbers!!
                this.addSlot(Slot(inventory, l + k * 9 + 9, i + l * 18, j + k * 18))
            }
        }

        for (k in 0 until 9) {
            this.addSlot(Slot(inventory, k, 36 + k * 18, 195))
        }
    }

    override fun onClosed(player: PlayerEntity) {
        super.onClosed(player)
        if (!player.world.isClient) {
            val itemStack = paymentSlot.takeStack(paymentSlot.maxItemCount)

            if (!itemStack.isEmpty)
                player.dropItem(itemStack, false)
        }
    }

    override fun canUse(player: PlayerEntity?): Boolean {
        return canUse(this.context, player, Blocks.BEACON)
    }

    override fun setProperty(id: Int, value: Int) {
        super.setProperty(id, value)
        this.sendContentUpdates()
    }

    override fun quickMove(player: PlayerEntity?, slotId: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot = slots[slotId]
        if (slot.hasStack()) {
            val itemStack2 = slot.stack
            itemStack = itemStack2.copy()
            if (slotId == 0) {
                if (!this.insertItem(itemStack2, INVENTORY_START, HOTBAR_END, false))
                    return ItemStack.EMPTY

                slot.onQuickTransfer(itemStack2, itemStack)
            } else if (!paymentSlot.hasStack() && paymentSlot.canInsert(itemStack2) && !this.insertItem(itemStack2, 0, 1, false)) {
                return ItemStack.EMPTY
            }

            // else if (!paymentSlot.hasStack() && paymentSlot.canInsert(itemStack2)) {
            //     if (!this.insertItem(itemStack2, 0, 1, false))
            //         return ItemStack.EMPTY
            // } else if (slotId in INVENTORY_START until HOTBAR_START) {
            //     if (!this.insertItem(itemStack2, HOTBAR_START, HOTBAR_END, false))
            //         return ItemStack.EMPTY
            // } else if (slotId in HOTBAR_START until HOTBAR_END) {
            //     if (!this.insertItem(itemStack2, INVENTORY_START, HOTBAR_START, false))
            //         return ItemStack.EMPTY
            // } else if (!this.insertItem(itemStack2, INVENTORY_START, HOTBAR_END, false)) {
            //     return ItemStack.EMPTY
            // }

            if (itemStack2.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }

            if (itemStack2.count == itemStack.count) {
                return ItemStack.EMPTY
            }

            slot.onTakeItem(player, itemStack2)
        }

        return itemStack
    }

    fun getProperties(): Int {
        return propertyDelegate[0]
    }

    fun getPrimaryEffect(): StatusEffect? {
        return StatusEffect.byRawId(propertyDelegate[1])
    }

    fun getSecondaryEffect(): StatusEffect? {
        return StatusEffect.byRawId(propertyDelegate[2])
    }

    fun setEffects(primary: Optional<StatusEffect?>, secondary: Optional<StatusEffect?>) {
        if (paymentSlot.hasStack()) {
            propertyDelegate[1] = primary.map(StatusEffect::getRawId).orElse(-1)
            propertyDelegate[2] = secondary.map(StatusEffect::getRawId).orElse(-1)
            paymentSlot.takeStack(1)
            context.run(World::markDirty)
        }
    }

    fun hasPayment(): Boolean {
        return !payment.getStack(0).isEmpty
    }

    internal class PaymentSlot(inventory: Inventory?, index: Int, x: Int, y: Int) : Slot(inventory, index, x, y) {
        override fun canInsert(stack: ItemStack): Boolean {
            return stack.isIn(ItemTags.BEACON_PAYMENT_ITEMS)
        }

        override fun getMaxItemCount(): Int {
            return 1
        }
    }

    internal class PaymentInventory : SimpleInventory(1) {
        override fun isValid(slot: Int, stack: ItemStack): Boolean {
            return stack.isIn(ItemTags.BEACON_PAYMENT_ITEMS)
        }

        override fun getMaxCountPerStack(): Int {
            return 1
        }
    }

}

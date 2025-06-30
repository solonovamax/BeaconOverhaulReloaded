package gay.solonovamax.beaconsoverhaul.render

import com.github.ajalt.colormath.model.SRGB
import gay.solonovamax.beaconsoverhaul.block.beacon.OverhauledBeacon
import gay.solonovamax.beaconsoverhaul.block.conduit.OverhauledConduitBlockEntity
import gay.solonovamax.beaconsoverhaul.block.conduit.OverhauledConduitBlockEntity.Companion.structureForTier
import gay.solonovamax.beaconsoverhaul.integration.lavender.LavenderStructureTemplate
import gay.solonovamax.beaconsoverhaul.integration.lavender.canPlaceNextMatching
import gay.solonovamax.beaconsoverhaul.util.drawTextWithShadow
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult

const val ITEM_ICON_SIZE = 16
const val TOOLTIP_OFFSET_X = 24
const val FONT_OFFSET_SPACING = 4
val TEXT_COLOR = SRGB(1.0, 1.0, 1.0)
val RIGHT_CLICK_TO_PLACE_MESSAGE = Text.translatable("hud.beaconoverhaul.right_click_to_place")
val SNEAK_TO_PLACE_MESSAGE = Text.translatable("hud.beaconoverhaul.sneak_to_place")

context(DrawContext)
fun MinecraftClient.renderTooltip() {
    val world = world
    val player = player

    if (world == null || player == null)
        return

    var posX = scaledWindowWidth / 2 + TOOLTIP_OFFSET_X
    val posY = scaledWindowHeight / 2

    val mainHandStack = player.mainHandStack
    val mainHandItem = mainHandStack.item

    if (mainHandItem !is BlockItem)
        return

    val hitResult = crosshairTarget as? BlockHitResult ?: return
    val targetState = world.getBlockState(hitResult.blockPos)
    val targetBlock = targetState.block

    if (targetBlock != Blocks.CONDUIT && targetBlock != Blocks.BEACON)
        return

    val targetBlockEntity = world.getBlockEntity(hitResult.blockPos) ?: return
    val placementState = mainHandItem.block.getPlacementState(ItemPlacementContext(player, Hand.MAIN_HAND, mainHandStack, hitResult)) ?: return

    when (targetBlock) {
        Blocks.CONDUIT -> {
            if (targetBlockEntity !is OverhauledConduitBlockEntity)
                return

            val nextTier = targetBlockEntity.tier + 1
            val template = structureForTier(nextTier) as LavenderStructureTemplate? ?: return

            if (!template.canPlaceNextMatching(placementState, hitResult.blockPos, world))
                return
        }

        Blocks.BEACON -> {
            if (targetBlockEntity !is OverhauledBeacon)
                return

            if (!targetBlockEntity.canPlaceNextMatching(placementState))
                return
        }

        else -> return
    }

    drawItem(mainHandStack, posX - ITEM_ICON_SIZE / 2, posY - ITEM_ICON_SIZE / 2)
    posX += ITEM_ICON_SIZE / 2 + FONT_OFFSET_SPACING

    val text = if (player.isSneaking) RIGHT_CLICK_TO_PLACE_MESSAGE else SNEAK_TO_PLACE_MESSAGE
    drawTextWithShadow(textRenderer, text, posX, posY - textRenderer.fontHeight / 2, TEXT_COLOR)
}

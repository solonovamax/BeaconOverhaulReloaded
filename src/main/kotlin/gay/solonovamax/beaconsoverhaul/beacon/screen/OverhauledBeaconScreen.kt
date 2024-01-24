package gay.solonovamax.beaconsoverhaul.beacon.screen

import com.github.ajalt.colormath.model.SRGB
import com.google.common.collect.Lists
import gay.solonovamax.beaconsoverhaul.BeaconOverhaulReloaded
import gay.solonovamax.beaconsoverhaul.beacon.serializable.OverhauledBeaconData
import gay.solonovamax.beaconsoverhaul.util.asRomanNumeral
import gay.solonovamax.beaconsoverhaul.util.drawCenteredTextWithShadow
import gay.solonovamax.beaconsoverhaul.util.drawItem
import gay.solonovamax.beaconsoverhaul.util.drawTextWithShadow
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.gui.widget.PressableWidget
import net.minecraft.client.texture.Sprite
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket
import net.minecraft.registry.Registries
import net.minecraft.registry.tag.BlockTags
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerListener
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.slf4j.kotlin.getLogger
import java.util.Optional
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@Environment(EnvType.CLIENT)
class OverhauledBeaconScreen(
    handler: OverhauledBeaconScreenHandler,
    inventory: PlayerInventory?,
    title: Text,
) : HandledScreen<OverhauledBeaconScreenHandler>(handler, inventory, title) {
    private val logger by getLogger()

    private val buttons: MutableList<BeaconButtonWidget> = Lists.newArrayList()
    var primaryEffect: StatusEffect? = null
    var secondaryEffect: StatusEffect? = null
    private val data: OverhauledBeaconData
        get() = handler.beaconData

    init {
        backgroundWidth = 230
        backgroundHeight = 219

        handler.addListener(object : ScreenHandlerListener {
            override fun onSlotUpdate(handler: ScreenHandler, slotId: Int, stack: ItemStack) {}

            override fun onPropertyUpdate(handler: ScreenHandler, property: Int, value: Int) {
                handler as OverhauledBeaconScreenHandler

                primaryEffect = handler.primaryEffect
                secondaryEffect = handler.secondaryEffect
            }
        })
    }

    private fun <T> addButton(button: T) where T : ClickableWidget, T : BeaconButtonWidget {
        addDrawableChild(button)
        buttons.add(button)
    }

    override fun init() {
        super.init()
        buttons.clear()
        addButton(DoneButtonWidget(this, x + 164, y + 107))
        addButton(CancelButtonWidget(this, x + 190, y + 107))

        for (effectTier in 0..2) {
            val tierEffectsCount = BeaconBlockEntity.EFFECTS_BY_LEVEL[effectTier].size
            val xCenterOffset = tierEffectsCount * 22 + (tierEffectsCount - 1) * 2

            for (effectIndex in 0 until tierEffectsCount) {
                val effectButton = EffectButtonWidget(
                    this,
                    x + 76 + (effectIndex * 24) - xCenterOffset / 2,
                    y + 22 + (effectTier * 25),
                    BeaconBlockEntity.EFFECTS_BY_LEVEL[effectTier][effectIndex],
                    true,
                    effectTier
                )
                effectButton.active = false
                addButton(effectButton)
            }
        }

        val secondaryEffectsCount = BeaconBlockEntity.EFFECTS_BY_LEVEL[3].size
        val effectsPerLine = ((secondaryEffectsCount + 1) / 2.0).roundToInt()
        val xCenterOffset = (effectsPerLine) * 22 + (effectsPerLine - 1) * 2

        for (effectIndex in 0 until secondaryEffectsCount) {
            val effectButtonWidget = EffectButtonWidget(
                this,
                x + 167 + ((effectIndex % effectsPerLine) * 24) - xCenterOffset / 2,
                y + 47 + ((effectIndex / effectsPerLine) * 24),
                BeaconBlockEntity.EFFECTS_BY_LEVEL[3][effectIndex],
                false,
                3
            )
            effectButtonWidget.active = false
            addButton(effectButtonWidget)
        }
        val primaryEffectLevelTwoButton = LevelTwoEffectButtonWidget(
            this,
            x + 167 + ((secondaryEffectsCount % effectsPerLine) * 24) - xCenterOffset / 2,
            y + 47 + ((secondaryEffectsCount / effectsPerLine) * 24),
            BeaconBlockEntity.EFFECTS_BY_LEVEL[0][0]
        )
        primaryEffectLevelTwoButton.visible = true
        primaryEffectLevelTwoButton.active = false
        addButton(primaryEffectLevelTwoButton)
    }

    public override fun handledScreenTick() {
        super.handledScreenTick()
        tickButtons()
    }

    fun tickButtons() {
        val i = handler.level
        buttons.forEach { button ->
            button.tick(i)
        }
    }

    override fun drawForeground(context: DrawContext, mouseX: Int, mouseY: Int) = context.drawForeground()

    @JvmName("drawForegroundExtension")
    private fun DrawContext.drawForeground() {
        drawCenteredTextWithShadow(textRenderer, PRIMARY_POWER_TEXT, 62, 10, POWER_TEXT_COLOR)
        drawCenteredTextWithShadow(textRenderer, SECONDARY_POWER_TEXT, 169, 10, POWER_TEXT_COLOR)
    }

    override fun drawBackground(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) =
        context.drawBackground()

    @JvmName("drawBackgroundExtension")
    private fun DrawContext.drawBackground() {
        val initialX = (width - backgroundWidth) / 2
        val initialY = (height - backgroundHeight) / 2

        drawTexture(BEACON_CONTAINER_TEXTURE, initialX, initialY, 0, 0, backgroundWidth, backgroundHeight)

        matrices.push()
        matrices.translate(0.0f, 0.0f, 100.0f)

        val beaconPaymentY = initialY + 109

        // why this formula? It aligns almost (R^2=0.9998) perfectly with mc's GUI
        for ((index, item) in BEACON_PAYMENT_ITEMS.withIndex())
            drawItem(item, initialX + (22.0375 * index + 19.4).roundToInt(), beaconPaymentY)

        drawBeaconInformation(initialX + backgroundWidth + 2, initialY, initialX + backgroundWidth / 2)

        matrices.pop()
    }


    private fun DrawContext.drawBeaconInformation(initialX: Int, initialY: Int, centerX: Int) {
        val fontOffset = (16 /* icon size */ - textRenderer.fontHeight) / 2

        val pointsText = "Points: %.1f".format(data.beaconPoints)
        drawCenteredTextWithShadow(textRenderer, pointsText, centerX, initialY - (textRenderer.fontHeight + PADDING) * 3, WHITE)
        val rangeText = "Range: %d blocks".format(data.range)
        drawCenteredTextWithShadow(textRenderer, rangeText, centerX, initialY - (textRenderer.fontHeight + PADDING) * 2, WHITE)
        val durationText = "Duration: %s".format(data.duration.seconds / 20)
        drawCenteredTextWithShadow(textRenderer, durationText, centerX, initialY - (textRenderer.fontHeight + PADDING) * 1, WHITE)

        matrices.push()
        matrices.translate(initialX.toDouble(), initialY.toDouble(), 0.0)

        var line = 0
        for (blockEntry in Registries.BLOCK.iterateEntries(BlockTags.BEACON_BASE_BLOCKS)) {
            val x = 0
            val y = (ITEM_ICON_SIZE + PADDING) * line
            val block = blockEntry.value()
            val count = data.baseBlocks[block] ?: 0

            if (count == 0)
                continue

            drawItem(block.asItem().defaultStack, x, y)

            drawTextWithShadow(textRenderer, "×$count", x + ITEM_ICON_SIZE + PADDING, y + fontOffset, WHITE)

            line++
        }

        matrices.pop()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        super.render(context, mouseX, mouseY, delta)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }

    @Environment(EnvType.CLIENT)
    interface BeaconButtonWidget {
        fun tick(level: Int)
    }

    @Environment(EnvType.CLIENT)
    abstract class BaseButtonWidget(
        val screen: OverhauledBeaconScreen,
        x: Int,
        y: Int,
        message: Text = ScreenTexts.EMPTY,
    ) : PressableWidget(x, y, 22, 22, message), BeaconButtonWidget {
        var isDisabled: Boolean = false

        public override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
            context.renderButton()
        }

        @JvmName("renderButtonExtension")
        private fun DrawContext.renderButton() {
            val j = when {
                !active -> width * 2
                isDisabled -> width * 1
                isSelected -> width * 3
                else -> 0
            }

            drawTexture(BEACON_CONTAINER_TEXTURE, x, y, j, 219, width, height)
            renderExtra()
        }

        protected abstract fun DrawContext.renderExtra()

        public override fun appendClickableNarrations(builder: NarrationMessageBuilder) {
            appendDefaultNarrations(builder)
        }
    }

    @Environment(EnvType.CLIENT)
    internal class CancelButtonWidget(
        screen: OverhauledBeaconScreen,
        x: Int,
        y: Int,
    ) : IconButtonWidget(screen, x, y, 112, 220, ScreenTexts.CANCEL) {
        override fun onPress() {
            screen.client?.player?.closeHandledScreen()
        }

        override fun tick(level: Int) {}
    }

    @Environment(EnvType.CLIENT)
    internal class DoneButtonWidget(
        screen: OverhauledBeaconScreen,
        x: Int,
        y: Int,
    ) : IconButtonWidget(screen, x, y, 90, 220, ScreenTexts.DONE) {
        override fun onPress() {
            screen.client?.networkHandler?.sendPacket(
                UpdateBeaconC2SPacket(
                    Optional.ofNullable(screen.primaryEffect), Optional.ofNullable(screen.secondaryEffect)
                )
            )
            screen.client?.player?.closeHandledScreen()
        }

        override fun tick(level: Int) {
            active = screen.handler.hasPayment && screen.primaryEffect != null
        }
    }

    @Environment(EnvType.CLIENT)
    internal open class EffectButtonWidget(
        screen: OverhauledBeaconScreen,
        x: Int,
        y: Int,
        private var effect: StatusEffect,
        private val primary: Boolean,
        private val level: Int,
    ) : BaseButtonWidget(screen, x, y) {
        private var sprite: Sprite = MinecraftClient.getInstance().statusEffectSpriteManager.getSprite(effect)

        init {
            init(effect)
        }

        protected fun init(statusEffect: StatusEffect) {
            effect = statusEffect
            sprite = MinecraftClient.getInstance().statusEffectSpriteManager.getSprite(statusEffect)
            tooltip = Tooltip.of(getEffectName(statusEffect), null)
        }

        protected open fun getEffectName(statusEffect: StatusEffect): MutableText {
            val text = Text.translatable(statusEffect.translationKey)
            val amplifier = if (primary) screen.data.primaryAmplifier else screen.data.secondaryAmplifier
            if (statusEffect !in BeaconOverhaulReloaded.config.levelOneStatusEffects && amplifier > 1) {
                text.append(" ")
                    .append(amplifier.asRomanNumeral())
            }
            return text
        }

        override fun onPress() {
            if (!isDisabled) {
                if (primary)
                    screen.primaryEffect = effect
                else
                    screen.secondaryEffect = effect

                screen.tickButtons()
            }
        }

        override fun DrawContext.renderExtra() {
            drawSprite(x + 2, y + 2, 0, 18, 18, sprite)
        }

        override fun tick(level: Int) {
            active = this.level < level
            isDisabled = effect === (if (primary) screen.primaryEffect else screen.secondaryEffect)
        }

        override fun getNarrationMessage(): MutableText {
            return getEffectName(effect)
        }
    }

    @Environment(EnvType.CLIENT)
    internal abstract class IconButtonWidget protected constructor(
        screen: OverhauledBeaconScreen,
        i: Int,
        j: Int,
        private val u: Int,
        private val v: Int,
        text: Text,
    ) : BaseButtonWidget(screen, i, j, text) {
        override fun DrawContext.renderExtra() {
            drawTexture(BEACON_CONTAINER_TEXTURE, x + 2, y + 2, u, v, 18, 18)
        }
    }

    @Environment(EnvType.CLIENT)
    internal class LevelTwoEffectButtonWidget(
        screen: OverhauledBeaconScreen,
        x: Int,
        y: Int,
        statusEffect: StatusEffect,
    ) : EffectButtonWidget(screen, x, y, statusEffect, false, 3) {
        override fun getEffectName(statusEffect: StatusEffect): MutableText {
            val text = Text.translatable(statusEffect.translationKey)
            val amplifier = screen.data.primaryAmplifierPotent

            if (statusEffect !in BeaconOverhaulReloaded.config.levelOneStatusEffects && amplifier > 1)
                text.append(" ")
                    .append(amplifier.asRomanNumeral())

            return text
        }

        override fun tick(level: Int) {
            val primaryEffect = screen.primaryEffect
            if (primaryEffect != null) {
                visible = true
                init(primaryEffect)
                super.tick(level)
            } else {
                visible = true
            }
        }
    }

    companion object {
        private val BEACON_PAYMENT_ITEMS = listOf(
            Items.NETHERITE_INGOT,
            Items.EMERALD,
            Items.DIAMOND,
            Items.GOLD_INGOT,
            Items.IRON_INGOT
        )

        private val BEACON_CONTAINER_TEXTURE = Identifier("textures/gui/container/beacon.png")
        private val PRIMARY_POWER_TEXT = Text.translatable("block.minecraft.beacon.primary")

        private val SECONDARY_POWER_TEXT = Text.translatable("block.minecraft.beacon.secondary")
        private const val ITEM_ICON_SIZE = 16

        private const val PADDING = 2
        private val POWER_TEXT_COLOR = SRGB.from255(224, 224, 224)
        private val WHITE = SRGB(1.0, 1.0, 1.0)
    }
}

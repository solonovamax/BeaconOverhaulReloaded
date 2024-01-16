package gay.solonovamax.beaconsoverhaul.beacon.screen

import com.google.common.collect.Lists
import gay.solonovamax.beaconsoverhaul.BeaconOverhaulReloaded
import gay.solonovamax.beaconsoverhaul.beacon.serializable.OverhauledBeaconData
import gay.solonovamax.beaconsoverhaul.util.asRomanNumeral
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
import net.minecraft.util.Colors
import net.minecraft.util.Identifier
import java.util.Optional

@Environment(EnvType.CLIENT)
class OverhauledBeaconScreen(
    handler: OverhauledBeaconScreenHandler,
    inventory: PlayerInventory?,
    title: Text,
) : HandledScreen<OverhauledBeaconScreenHandler>(handler, inventory, title) {
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

                primaryEffect = handler.getPrimaryEffect()
                secondaryEffect = handler.getSecondaryEffect()
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

        for (i in 0..2) {
            val j = BeaconBlockEntity.EFFECTS_BY_LEVEL[i].size
            val k = j * 22 + (j - 1) * 2

            for (l in 0 until j) {
                val statusEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL[i][l]
                val effectButtonWidget = EffectButtonWidget(
                    this,
                    x + 76 + (l * 24) - k / 2,
                    y + 22 + (i * 25),
                    statusEffect,
                    true,
                    i
                )
                effectButtonWidget.active = false
                addButton(effectButtonWidget)
            }
        }

        val i = 3
        val j = BeaconBlockEntity.EFFECTS_BY_LEVEL[3].size + 1
        val k = j * 22 + (j - 1) * 2

        for (l in 0 until j - 1) {
            val statusEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL[3][l]
            val effectButtonWidget = EffectButtonWidget(
                this,
                x + 167 + (l * 24) - k / 2,
                y + 47,
                statusEffect,
                false,
                3
            )
            effectButtonWidget.active = false
            addButton(effectButtonWidget)
        }

        val effectButtonWidget2: EffectButtonWidget = LevelTwoEffectButtonWidget(
            this,
            x + 167 + ((j - 1) * 24) - k / 2,
            y + 47,
            BeaconBlockEntity.EFFECTS_BY_LEVEL[0][0]
        )
        effectButtonWidget2.visible = false
        addButton(effectButtonWidget2)
    }

    public override fun handledScreenTick() {
        super.handledScreenTick()
        tickButtons()
    }

    fun tickButtons() {
        val i = handler.getProperties()
        buttons.forEach { button ->
            button.tick(i)
        }
    }

    override fun drawForeground(context: DrawContext, mouseX: Int, mouseY: Int) {
        context.drawCenteredTextWithShadow(textRenderer, PRIMARY_POWER_TEXT, 62, 10, 14737632)
        context.drawCenteredTextWithShadow(textRenderer, SECONDARY_POWER_TEXT, 169, 10, 14737632)
    }

    override fun drawBackground(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        val initialX = (width - backgroundWidth) / 2
        val initialY = (height - backgroundHeight) / 2
        context.drawTexture(BEACON_CONTAINER_TEXTURE, initialX, initialY, 0, 0, backgroundWidth, backgroundHeight)
        context.matrices.push()
        context.matrices.translate(0.0f, 0.0f, 100.0f)
        context.drawItem(ItemStack(Items.NETHERITE_INGOT), initialX + 20, initialY + 109)
        context.drawItem(ItemStack(Items.EMERALD), initialX + 41, initialY + 109)
        context.drawItem(ItemStack(Items.DIAMOND), initialX + 41 + 22, initialY + 109)
        context.drawItem(ItemStack(Items.GOLD_INGOT), initialX + 42 + 44, initialY + 109)
        context.drawItem(ItemStack(Items.IRON_INGOT), initialX + 42 + 66, initialY + 109)

        context.drawBeaconSidebar(delta, mouseX, mouseY, initialX + backgroundWidth + 2, initialY)

        context.matrices.pop()
    }

    private fun DrawContext.drawBeaconSidebar(delta: Float, mouseX: Int, mouseY: Int, initialX: Int, initialY: Int) {
        val fontOffset = (16 - textRenderer.fontHeight) / 2

        matrices.push()
        matrices.translate(initialX.toDouble(), initialY.toDouble(), 0.0)
        // if (client!!.window.scaleFactor >= 3.0)
        //     matrices.scale(0.75f, 0.75f, 1.0f) // only scale if the gui scale is at least 2
        var x = 0
        var y = 0
        for (blockEntry in Registries.BLOCK.iterateEntries(BlockTags.BEACON_BASE_BLOCKS)) {

            // matrices.push()
            // matrices.translate(x.toDouble(), y.toDouble(), 0.0)
            // matrices.scale(1.5f, 2.0f, 1.5f)
            // drawText(textRenderer, "(", 0, 0, Colors.WHITE, true)
            // x += (textRenderer.getWidth("(") * 1.5).toInt() + 2
            // matrices.pop()

            val block = blockEntry.value()
            drawItem(block.asItem().defaultStack, x, y)
            x += 18
            val count = data.baseBlocks[block] ?: 0
            val countText = "×$count"
            drawText(textRenderer, countText, x, y + fontOffset, Colors.WHITE, true)
            x += textRenderer.getWidth(countText)


            // matrices.push()
            // matrices.translate(x.toDouble(), y.toDouble(), 0.0)
            // matrices.scale(1.5f, 2.0f, 1.5f)
            // drawText(textRenderer, ")", 0, 0, Colors.WHITE, true)
            // x += (textRenderer.getWidth(")") * 1.5).toInt() + 2
            // matrices.pop()
            //
            // val formula = "× [insert formula here]"
            // drawText(textRenderer, formula, x, y + fontOffset, Colors.WHITE, true)
            // x += textRenderer.getWidth(formula)

            y += 18
            x = 0
        }
        matrices.pop()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        super.render(context, mouseX, mouseY, delta)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }

    @Environment(EnvType.CLIENT)
    abstract class BaseButtonWidget(
        val screen: OverhauledBeaconScreen,
        x: Int,
        y: Int,
        message: Text = ScreenTexts.EMPTY,
    ) :
        PressableWidget(x, y, 22, 22, message), BeaconButtonWidget {
        var isDisabled: Boolean = false

        public override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
            val j = when {
                !active -> width * 2
                isDisabled -> width * 1
                this.isSelected -> width * 3
                else -> 0
            }

            context.drawTexture(BEACON_CONTAINER_TEXTURE, x, y, j, 219, width, height)
            renderExtra(context)
        }

        protected abstract fun renderExtra(context: DrawContext)

        public override fun appendClickableNarrations(builder: NarrationMessageBuilder) {
            appendDefaultNarrations(builder)
        }
    }

    @Environment(EnvType.CLIENT)
    interface BeaconButtonWidget {
        fun tick(level: Int)
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
            screen.client!!.player!!.closeHandledScreen()
        }

        override fun tick(level: Int) {
            active = screen.handler.hasPayment() && screen.primaryEffect != null
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

        override fun renderExtra(context: DrawContext) {
            context.drawSprite(x + 2, y + 2, 0, 18, 18, sprite)
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
        override fun renderExtra(context: DrawContext) {
            context.drawTexture(BEACON_CONTAINER_TEXTURE, x + 2, y + 2, u, v, 18, 18)
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
            if (statusEffect !in BeaconOverhaulReloaded.config.levelOneStatusEffects && amplifier > 1) {
                text.append(" ")
                    .append(amplifier.asRomanNumeral())
            }
            return text
        }

        override fun tick(level: Int) {
            val primaryEffect = screen.primaryEffect
            if (primaryEffect != null) {
                visible = true
                init(primaryEffect)
                super.tick(level)
            } else {
                visible = false
            }
        }
    }

    companion object {
        private val BEACON_CONTAINER_TEXTURE: Identifier = Identifier("textures/gui/container/beacon.png")
        private val PRIMARY_POWER_TEXT: Text = Text.translatable("block.minecraft.beacon.primary")
        private val SECONDARY_POWER_TEXT: Text = Text.translatable("block.minecraft.beacon.secondary")
    }
}

package com.notryken.effecttimerplus.gui.screen;

import com.notryken.effecttimerplus.config.Config;
import com.notryken.effecttimerplus.gui.widget.list.PotencyOptionsList;
import com.notryken.effecttimerplus.gui.widget.list.TimerOptionsList;
import com.notryken.effecttimerplus.gui.widget.slider.DoubleSlider;
import com.notryken.effecttimerplus.mixin.accessor.GuiAccessor;
import com.notryken.effecttimerplus.util.MiscUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;

public class OptionsScreen extends OptionsSubScreen {
    // Dimensional constants
    private final int MIN_X = this.width / 2 - 155;
    private final int MIN_Y = this.height / 6 - 12;
    private final int ITEM_WIDTH = 150;
    private final int ITEM_HEIGHT = 20;

    // Demo set of effects
    // Params: effect, duration, amplifier, ambient, visible
    private final MobEffectInstance[] DEMO_EFFECTS = new MobEffectInstance[] {
            new MobEffectInstance(MobEffects.DIG_SPEED, 111, 1, true, true),
            new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 211, 1, false, true),
            new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 411, 2, false, true),
            new MobEffectInstance(MobEffects.DAMAGE_BOOST, 811, 9, false, true),
            new MobEffectInstance(MobEffects.JUMP, 1251, 4, false, true),
            new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2131, 0, false, true),
            new MobEffectInstance(MobEffects.WEAKNESS, 3500, 1, false, true),
            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 9600, 0, false, true),
            new MobEffectInstance(MobEffects.INVISIBILITY, 144000, 0, false, true),
            new MobEffectInstance(MobEffects.CONDUIT_POWER, -1, 0, false, true),
    };

    // GUI elements
    private DoubleSlider scaleSlider;
    private PotencyOptionsList potencyOptionsList;
    private TimerOptionsList timerOptionsList;
    private Button resetButton;
    private Button doneButton;


    public OptionsScreen(Screen lastScreen) {
        super(lastScreen, Minecraft.getInstance().options,
                Component.translatable("screen.effecttimerplus.title.default"));
    }

    public void reload() {
        Minecraft.getInstance().setScreen(new OptionsScreen(lastScreen));
    }

    @Override
    protected void init() {

        int optionsTopY = MIN_Y + 65;

        scaleSlider = new DoubleSlider(this.width / 2 - 80, optionsTopY + 5, 160, 20, 0,
                2, 1, "Icon Scale: ", null, null, null,
                () -> Config.get().scale, (value) -> Config.get().scale = value);

        int paneTopY = optionsTopY + 30;
        int paneHeight = height - paneTopY - 36;
        int paneWidth = width / 2 - 8;
        int rightPaneX = width - paneWidth;

        potencyOptionsList = new PotencyOptionsList(minecraft, paneWidth, paneHeight, paneTopY, ITEM_HEIGHT, this);
        potencyOptionsList.setX(0);

        timerOptionsList = new TimerOptionsList(minecraft, paneWidth, paneHeight, paneTopY, ITEM_HEIGHT, this);
        timerOptionsList.setX(rightPaneX);

        resetButton = Button.builder(Component.literal("Reset All"), (button) -> {
            Config.resetAndSave();
            reload();
        })
                .pos(this.width / 2 - 154, this.height - 28)
                .size(150, 20)
                .build();

        doneButton = Button.builder(CommonComponents.GUI_DONE, (button) -> onClose())
                .pos(this.width / 2 + 4, this.height - 28)
                .size(150, 20)
                .build();

        addRenderableWidget(scaleSlider);
        addRenderableWidget(potencyOptionsList);
        addRenderableWidget(timerOptionsList);
        addRenderableWidget(resetButton);
        addRenderableWidget(doneButton);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(font, title, width / 2, 15, 16777215);
        scaleSlider.render(graphics, mouseX, mouseY, delta);

        // Render demo status effect icons
        int xSpace = 27; // Icon spacing
        int x = width / 2 - DEMO_EFFECTS.length * xSpace / 2;
        int y = MIN_Y + 40; // Icon placement reference point is bottom left

        for (MobEffectInstance effect : DEMO_EFFECTS) {
            graphics.blitSprite(GuiAccessor.getEffectBackgroundSprite(), x, y, 24, 24);
            graphics.blit(x + 3, y + 3, 0, 18, 18, minecraft.getMobEffectTextures().get(effect.getEffect()));

            // Render potency overlay
            if (Config.get().potencyEnabled && effect.getAmplifier() > 0) {
                String label = MiscUtil.getAmplifierAsString(effect.getAmplifier());
                int labelWidth = minecraft.font.width(label);
                int pX = x + MiscUtil.getTextOffsetX(Config.get().getPotencyLocation(), labelWidth);
                int pY = y + MiscUtil.getTextOffsetY(Config.get().getPotencyLocation());
                graphics.fill(pX, pY, pX + labelWidth, pY + minecraft.font.lineHeight - 1,
                        Config.get().getPotencyBackColor());
                graphics.drawString(minecraft.font, label, pX, pY, Config.get().getPotencyColor(), false);
            }
            // Render timer overlay
            if (Config.get().timerEnabled && (Config.get().timerEnabledAmbient || !effect.isAmbient())) {
                String label = MiscUtil.getDurationAsString(effect.getDuration());
                int labelWidth = minecraft.font.width(label);
                int pX = x + MiscUtil.getTextOffsetX(Config.get().getTimerLocation(), labelWidth);
                int pY = y + MiscUtil.getTextOffsetY(Config.get().getTimerLocation());
                graphics.fill(pX, pY, pX + labelWidth, pY + minecraft.font.lineHeight - 1,
                        Config.get().getTimerBackColor());
                graphics.drawString(minecraft.font, label, pX, pY, MiscUtil.getTimerColor(effect), false);
            }
            x += xSpace;
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderDirtBackground(graphics);
    }

    @Override
    public void onClose() {
        Config.save();
        super.onClose();
    }
}

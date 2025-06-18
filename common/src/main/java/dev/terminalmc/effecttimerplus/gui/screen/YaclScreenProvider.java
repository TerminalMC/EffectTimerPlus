/*
 * EffectTimerPlus
 * Copyright (C) 2025 TerminalMC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.terminalmc.effecttimerplus.gui.screen;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import dev.terminalmc.effecttimerplus.config.Config;
import dev.terminalmc.effecttimerplus.mixin.accessor.GuiAccessor;
import dev.terminalmc.effecttimerplus.util.IndicatorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.awt.*;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

import static dev.terminalmc.effecttimerplus.util.IndicatorUtil.getScaleTranslateX;
import static dev.terminalmc.effecttimerplus.util.IndicatorUtil.getScaleTranslateY;
import static dev.terminalmc.effecttimerplus.util.Localization.localized;

public class YaclScreenProvider {
    /**
     * Builds and returns a YACL options screen.
     * @param parent the current screen.
     * @return a new options {@link Screen}.
     * @throws NoClassDefFoundError if the YACL mod is not available.
     */
    static Screen getConfigScreen(Screen parent) {
        Config options = Config.get();
        Preview preview = new Preview();

        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(localized("name"))
                .save(Config::save);

        ConfigCategory.Builder timerCat = ConfigCategory.createBuilder()
                .name(localized("option", "timer"));

        timerCat.option(Option.<Boolean>createBuilder()
                .name(localized("option", "timer.enabled"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.timerEnabled = option.pendingValue())
                .binding(Config.defaultTimerEnabled,
                        () -> options.timerEnabled,
                        val -> options.timerEnabled = val)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true))
                .build());

        timerCat.option(Option.<Boolean>createBuilder()
                .name(localized("option", "timer.ambient.enabled"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.timerEnabledAmbient = option.pendingValue())
                .binding(Config.defaultTimerEnabledAmbient,
                        () -> options.timerEnabledAmbient,
                        val -> options.timerEnabledAmbient = val)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true))
                .build());

        timerCat.option(Option.<Boolean>createBuilder()
                .name(localized("option", "timer.warn.enabled"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.timerWarnEnabled = option.pendingValue())
                .binding(Config.defaultTimerWarnEnabled,
                        () -> options.timerWarnEnabled,
                        val -> options.timerWarnEnabled = val)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true))
                .build());

        timerCat.option(Option.<Boolean>createBuilder()
                .name(localized("option", "timer.warn.flash.enabled"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.timerFlashEnabled = option.pendingValue())
                .binding(Config.defaultTimerFlashEnabled,
                        () -> options.timerFlashEnabled,
                        val -> options.timerFlashEnabled = val)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true))
                .build());

        timerCat.option(Option.<Color>createBuilder()
                .name(localized("option", "timer.color"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.timerColor = fixAlpha(option.pendingValue().getRGB()))
                .binding(fromArgb(Config.defaultTimerColor),
                        () -> fromArgb(options.timerColor),
                        val -> options.timerColor = fixAlpha(val.getRGB()))
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(true))
                .build());

        timerCat.option(Option.<Boolean>createBuilder()
                .name(localized("option", "timer.shadow.enabled"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.timerShadow = option.pendingValue())
                .binding(Config.defaultTimerShadow,
                        () -> options.timerShadow,
                        val -> options.timerShadow = val)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true))
                .build());

        timerCat.option(Option.<Boolean>createBuilder()
                .name(localized("option", "timer.back.enabled"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.timerBack = option.pendingValue())
                .binding(Config.defaultTimerBack,
                        () -> options.timerBack,
                        val -> options.timerBack = val)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true))
                .build());

        timerCat.option(Option.<Color>createBuilder()
                .name(localized("option", "timer.back.color"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.timerBackColor = fixAlpha(option.pendingValue().getRGB()))
                .binding(fromArgb(Config.defaultTimerBackColor),
                        () -> fromArgb(options.timerBackColor),
                        val -> options.timerBackColor = fixAlpha(val.getRGB()))
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(true))
                .build());

        timerCat.option(Option.<Color>createBuilder()
                .name(localized("option", "timer.warn.color"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.timerWarnColor = fixAlpha(option.pendingValue().getRGB()))
                .binding(fromArgb(Config.defaultTimerWarnColor),
                        () -> fromArgb(options.timerWarnColor),
                        val -> options.timerWarnColor = fixAlpha(val.getRGB()))
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(true))
                .build());

        timerCat.option(Option.<Integer>createBuilder()
                .name(localized("option", "timer.warn.time"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.timerWarnTime = option.pendingValue())
                .binding(Config.defaultTimerWarnTime,
                        () -> options.timerWarnTime,
                        val -> options.timerWarnTime = val)
                .controller(option -> IntegerSliderControllerBuilder.create(option)
                        .range(0, 60)
                        .step(1)
                        .formatValue(val ->
                                localized("option", "timer.warn.time.value", val)))
                .build());

        timerCat.option(Option.<Integer>createBuilder()
                .name(localized("option", "timer.location"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.timerLocation = option.pendingValue())
                .binding(Config.defaultTimerLocation,
                        () -> options.timerLocation,
                        val -> options.timerLocation = val)
                .controller(option -> CyclingListControllerBuilder.create(option)
                        .values(Config.locations)
                        .formatValue(val -> localized("option", "location." + val)))
                .build());

        // Potency indicator category
        ConfigCategory.Builder potencyCat = ConfigCategory.createBuilder()
                .name(localized("option", "potency"));

        potencyCat.option(Option.<Boolean>createBuilder()
                .name(localized("option", "potency.enabled"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.potencyEnabled = option.pendingValue())
                .binding(Config.defaultPotencyEnabled,
                        () -> options.potencyEnabled,
                        val -> options.potencyEnabled = val)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true))
                .build());

        potencyCat.option(Option.<Color>createBuilder()
                .name(localized("option", "potency.color"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.potencyColor = fixAlpha(option.pendingValue().getRGB()))
                .binding(fromArgb(Config.defaultPotencyColor),
                        () -> fromArgb(options.potencyColor),
                        val -> options.potencyColor = fixAlpha(val.getRGB()))
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(true))
                .build());

        potencyCat.option(Option.<Boolean>createBuilder()
                .name(localized("option", "potency.shadow.enabled"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.potencyShadow = option.pendingValue())
                .binding(Config.defaultPotencyShadow,
                        () -> options.potencyShadow,
                        val -> options.potencyShadow = val)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true))
                .build());

        potencyCat.option(Option.<Boolean>createBuilder()
                .name(localized("option", "potency.back.enabled"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.potencyBack = option.pendingValue())
                .binding(Config.defaultPotencyBack,
                        () -> options.potencyBack,
                        val -> options.potencyBack = val)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true))
                .build());

        potencyCat.option(Option.<Color>createBuilder()
                .name(localized("option", "potency.back.color"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.potencyBackColor = fixAlpha(option.pendingValue().getRGB()))
                .binding(fromArgb(Config.defaultPotencyBackColor),
                        () -> fromArgb(options.potencyBackColor),
                        val -> options.potencyBackColor = fixAlpha(val.getRGB()))
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(true))
                .build());

        potencyCat.option(Option.<Integer>createBuilder()
                .name(localized("option", "potency.location"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.potencyLocation = option.pendingValue())
                .binding(Config.defaultPotencyLocation,
                        () -> options.potencyLocation,
                        val -> options.potencyLocation = val)
                .controller(option -> CyclingListControllerBuilder.create(option)
                        .values(Config.locations)
                        .formatValue(val -> localized("option", "location." + val)))
                .build());

        ConfigCategory.Builder scaleCat = ConfigCategory.createBuilder()
                .name(localized("option", "scale"));

        scaleCat.option(Option.<Double>createBuilder()
                .name(localized("option", "scale.icon"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.scale = option.pendingValue())
                .binding(Config.defaultScale,
                        () -> options.scale,
                        val -> options.scale = val)
                .controller(option -> DoubleSliderControllerBuilder.create(option)
                        .range(0.5D, 1.5D)
                        .step(0.1D))
                .build());

        scaleCat.option(Option.<Double>createBuilder()
                .name(localized("option", "scale.timer"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.timerScale = option.pendingValue())
                .binding(Config.defaultTimerScale,
                        () -> options.timerScale,
                        val -> options.timerScale = val)
                .controller(option -> DoubleSliderControllerBuilder.create(option)
                        .range(0.5D, 1.5D)
                        .step(0.1D))
                .build());

        scaleCat.option(Option.<Double>createBuilder()
                .name(localized("option", "scale.potency"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .addListener((option, event) -> preview.potencyScale = option.pendingValue())
                .binding(Config.defaultPotencyScale,
                        () -> options.potencyScale,
                        val -> options.potencyScale = val)
                .controller(option -> DoubleSliderControllerBuilder.create(option)
                        .range(0.5D, 1.5D)
                        .step(0.1D))
                .build());

        // Assemble
        builder.category(timerCat.build());
        builder.category(potencyCat.build());
        builder.category(scaleCat.build());

        YetAnotherConfigLib yacl = builder.build();
        return yacl.generateScreen(parent);
    }

    // Special option utils

    private static Color fromArgb(int i) {
        return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF, (i >> 24) & 0xFF);
    }

    // Workaround for a bug (?) causing alpha to snap to max when set below 4.
    private static int fixAlpha(int color) {
        if (toAlpha.applyAsInt(color) < 4) {
            return withAlpha.applyAsInt(color, fromAlpha.applyAsInt(4));
        }
        return color;
    }

    private static final IntUnaryOperator toAlpha = (value) -> (value >> 24 & 255);
    private static final IntUnaryOperator fromAlpha = (value) -> (value * 16777216);
    private static final IntBinaryOperator withAlpha = (value, alpha) ->
            (value - (fromAlpha.applyAsInt(toAlpha.applyAsInt(value))) + alpha);

    // Preview

    private static class Preview implements ImageRenderer {
        // Maintain a copy of all values for instant update by listeners
        Config options = Config.get();
        public double scale = options.scale;
        public double potencyScale = options.potencyScale;
        public double timerScale = options.potencyScale;
        public boolean potencyEnabled = options.potencyEnabled;
        public boolean timerEnabled = options.timerEnabled;
        public boolean timerEnabledAmbient = options.timerEnabledAmbient;
        public boolean timerWarnEnabled = options.timerWarnEnabled;
        public boolean timerFlashEnabled = options.timerFlashEnabled;
        public int timerWarnTime = options.timerWarnTime;
        public int potencyColor = options.potencyColor;
        public boolean potencyShadow = options.potencyShadow;
        public boolean potencyBack = options.potencyBack;
        public int potencyBackColor = options.potencyBackColor;
        public int timerColor = options.timerColor;
        public int timerWarnColor = options.timerWarnColor;
        public boolean timerShadow = options.timerShadow;
        public boolean timerBack = options.timerBack;
        public int timerBackColor = options.timerBackColor;
        public int potencyLocation = options.potencyLocation;
        public int timerLocation = options.timerLocation;

        // Params: effect, duration, amplifier, ambient, visible
        private final MobEffectInstance[] DEMO_EFFECTS = new MobEffectInstance[] {
                new MobEffectInstance(MobEffects.HASTE, 111, 1, true, true),
                new MobEffectInstance(MobEffects.RESISTANCE, 211, 1, false, true),
                new MobEffectInstance(MobEffects.SPEED, 411, 2, false, true),
                new MobEffectInstance(MobEffects.STRENGTH, 811, 9, false, true),
                new MobEffectInstance(MobEffects.JUMP_BOOST, 1251, 4, false, true),
                new MobEffectInstance(MobEffects.SLOWNESS, 2131, 0, false, true),
                new MobEffectInstance(MobEffects.WEAKNESS, 3500, 1, false, true),
                new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 9600, 0, false, true),
                new MobEffectInstance(MobEffects.INVISIBILITY, 144000, 0, false, true),
                new MobEffectInstance(MobEffects.CONDUIT_POWER, -1, 0, false, true),
        };

        @Override
        public int render(GuiGraphics graphics, int x, int y, int width, float delta) {
            float scale = (float)this.scale;
            graphics.pose().pushMatrix();
            graphics.pose().translate(x * (1 - scale), y * (1 - scale));
            graphics.pose().scale(scale, scale);

            Minecraft mc = Minecraft.getInstance();
            int movingX = x;
            int movingY = y;
            int space = 27;
            int targetHeight = space;
            int maxX = (int)(x + width / scale);

            int spriteSize = 24;
            int iconSize = 18;

            for (MobEffectInstance effect : DEMO_EFFECTS) {
                if (effect.isAmbient()) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                            GuiAccessor.getEffectBackgroundAmbientSprite(), 
                            movingX, movingY, spriteSize, spriteSize);
                } else {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                            GuiAccessor.getEffectBackgroundSprite(), 
                            movingX, movingY, spriteSize, spriteSize);
                }
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                        Gui.getMobEffectSprite(effect.getEffect()),
                        movingX + 3, movingY + 3, iconSize, iconSize, ARGB.white(1.0F));

                // Render potency overlay
                if (potencyEnabled && effect.getAmplifier() > 0) {
                    String label = IndicatorUtil.getAmplifierAsString(effect.getAmplifier());
                    int labelWidth = mc.font.width(label);
                    int pX = movingX + IndicatorUtil.getTextOffsetX(potencyLocation, labelWidth, spriteSize);
                    int pY = movingY + IndicatorUtil.getTextOffsetY(potencyLocation, mc.font.lineHeight, spriteSize);

                    graphics.pose().pushMatrix();
                    graphics.pose().translate((float)(pX * (1 - potencyScale)), (float)(pY * (1 - potencyScale)));
                    graphics.pose().translate(getScaleTranslateX(potencyLocation, labelWidth, (float)potencyScale),
                            getScaleTranslateY(potencyLocation, mc.font.lineHeight, (float)potencyScale));
                    graphics.pose().scale((float)potencyScale, (float)potencyScale);
                    if (potencyBack) {
                        graphics.fill(pX - 1, pY - 1, pX + labelWidth, pY + mc.font.lineHeight - 1, potencyBackColor);
                    }
                    graphics.drawString(mc.font, label, pX, pY, potencyColor, potencyShadow);
                    graphics.pose().popMatrix();
                }
                // Render timer overlay
                if (timerEnabled && (timerEnabledAmbient || !effect.isAmbient())) {
                    String label = IndicatorUtil.getDurationAsString(effect.getDuration());
                    int labelWidth = mc.font.width(label);
                    int pX = movingX + IndicatorUtil.getTextOffsetX(timerLocation, labelWidth, spriteSize);
                    int pY = movingY + IndicatorUtil.getTextOffsetY(timerLocation, mc.font.lineHeight, spriteSize);

                    graphics.pose().pushMatrix();
                    graphics.pose().translate((float)(pX * (1 - timerScale)), (float)(pY * (1 - timerScale)));
                    graphics.pose().translate(getScaleTranslateX(timerLocation, labelWidth, (float)timerScale),
                            getScaleTranslateY(timerLocation, mc.font.lineHeight, (float)timerScale));
                    graphics.pose().scale((float)timerScale, (float)timerScale);
                    if (timerBack) {
                        graphics.fill(pX - 1, pY - 1, pX + labelWidth, pY + mc.font.lineHeight - 1, timerBackColor);
                    }
                    graphics.drawString(mc.font, label, pX, pY, IndicatorUtil.getTimerColor(effect, timerColor,
                            timerWarnEnabled, timerWarnTime, timerWarnColor, timerFlashEnabled), timerShadow);
                    graphics.pose().popMatrix();
                }
                movingX += space;
                if (movingX + space > maxX) {
                    movingX = x;
                    movingY += space;
                    targetHeight += space;
                }
            }

            graphics.pose().popMatrix();
            return (int)(targetHeight * scale);
        }

        @Override
        public void close() {
            // Not currently used
        }
    }
}

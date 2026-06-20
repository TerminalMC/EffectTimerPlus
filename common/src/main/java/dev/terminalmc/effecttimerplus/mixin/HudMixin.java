/*
 * EffectTimerPlus
 * Copyright (C) 2024 magicus
 * Copyright (C) 2026 TerminalMC
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

package dev.terminalmc.effecttimerplus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.terminalmc.effecttimerplus.config.Config;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.terminalmc.effecttimerplus.util.IndicatorUtil.*;

/**
 * This file includes derivative work of code from
 * <a href="https://github.com/magicus/statuseffecttimer">Status Effect Timer</a>
 */
@Mixin(
        value = Hud.class,
        priority = 2000
)
public abstract class HudMixin {

    @Final
    @Shadow
    private Minecraft minecraft;

    @Unique
    @Nullable
    private Runnable effectTimerPlus$runnable;

    @Inject(
            method = "extractEffects",
            at = @At("HEAD")
    )
    private void scale(GuiGraphicsExtractor graphics, DeltaTracker delta, CallbackInfo ci) {
        float scale = (float) Config.get().scale;
        graphics.pose().pushMatrix();
        graphics.pose().translate(graphics.guiWidth() * (1 - scale), 0.0F);
        graphics.pose().scale(scale, scale);
    }

    @Inject(
            method = "extractEffects",
            at = @At("RETURN")
    )
    private void descale(GuiGraphicsExtractor graphics, DeltaTracker delta, CallbackInfo ci) {
        graphics.pose().popMatrix();
    }

    @WrapOperation(
            method = "extractEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"
            )
    )
    private void CreateOverlayRunnable(
            GuiGraphicsExtractor graphics,
            RenderPipeline pipeline,
            Identifier sprite,
            int x,
            int y,
            int width,
            int height,
            Operation<Void> original,
            @Local(name = "instance") MobEffectInstance effectInstance
    ) {
        original.call(graphics, pipeline, sprite, x, y, width, height);

        Config options = Config.get();
        effectTimerPlus$runnable = () -> {
            // Render potency overlay
            if (options.potencyEnabled && effectInstance.getAmplifier() > 0) {
                String label = getAmplifierAsString(effectInstance.getAmplifier());
                int labelWidth = minecraft.font.width(label);
                int posX = x + getTextOffsetX(options.potencyLocation, labelWidth, width);
                int posY = y + getTextOffsetY(
                        options.potencyLocation,
                        minecraft.font.lineHeight,
                        height
                );

                float scale = (float) Config.get().potencyScale;
                graphics.pose().pushMatrix();
                graphics.pose().translate(posX * (1 - scale), posY * (1 - scale));
                graphics.pose().translate(
                        getScaleTranslateX(options.potencyLocation, labelWidth, scale),
                        getScaleTranslateY(
                                options.potencyLocation,
                                minecraft.font.lineHeight,
                                scale
                        )
                );
                graphics.pose().scale(scale, scale);
                if (options.potencyBack) {
                    graphics.fill(
                            posX - 1, posY - 1, posX + labelWidth,
                            posY + minecraft.font.lineHeight - 1, options.potencyBackColor
                    );
                }
                graphics.text(
                        minecraft.font,
                        label,
                        posX,
                        posY,
                        options.potencyColor,
                        options.potencyShadow
                );
                graphics.pose().popMatrix();
            }
            // Render timer overlay
            if (options.timerEnabled && (options.timerEnabledAmbient
                    || !effectInstance.isAmbient())) {
                String label = getDurationAsString(effectInstance.getDuration());
                int labelWidth = minecraft.font.width(label);
                int posX = x + getTextOffsetX(options.timerLocation, labelWidth, width);
                int posY = y + getTextOffsetY(
                        options.timerLocation,
                        minecraft.font.lineHeight,
                        height
                );

                int color = getTimerColor(
                        effectInstance, options.timerColor,
                        options.timerWarnEnabled, options.timerWarnTime,
                        options.timerWarnColor, options.timerFlashEnabled
                );
                float scale = (float) Config.get().timerScale;
                graphics.pose().pushMatrix();
                graphics.pose().translate(posX * (1 - scale), posY * (1 - scale));
                graphics.pose().translate(
                        getScaleTranslateX(options.timerLocation, labelWidth, scale),
                        getScaleTranslateY(options.timerLocation, minecraft.font.lineHeight, scale)
                );
                graphics.pose().scale(scale, scale);
                if (options.timerBack) {
                    graphics.fill(
                            posX - 1, posY - 1, posX + labelWidth,
                            posY + minecraft.font.lineHeight - 1, options.timerBackColor
                    );
                }
                graphics.text(minecraft.font, label, posX, posY, color, options.timerShadow);
                graphics.pose().popMatrix();
            }
        };
    }

    @Inject(
            method = "extractEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIII)V",
                    shift = At.Shift.AFTER
            )
    )
    private void AddOverlayRunnable(
            GuiGraphicsExtractor graphics,
            DeltaTracker deltaTracker,
            CallbackInfo ci
    ) {
        if (effectTimerPlus$runnable != null) {
            effectTimerPlus$runnable.run();
        }
    }
}

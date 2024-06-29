package dev.terminalmc.effecttimerplus.mixin;

import com.google.common.collect.Ordering;
import dev.terminalmc.effecttimerplus.config.Config;
import dev.terminalmc.effecttimerplus.util.MiscUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

/**
 * Includes derivative work of code used by
 * <a href="https://github.com/magicus/statuseffecttimer/">Status Effect Timer</a>
 */
@Mixin(value = Gui.class, priority = 500)
public class MixinGui {

    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "renderEffects", at = @At("HEAD"))
    private void scaleGraphics(GuiGraphics graphics, CallbackInfo ci) {
        float scale = (float) Config.get().scale;
        graphics.pose().pushPose();
        graphics.pose().translate(graphics.guiWidth() * (1 - scale), 0.0F, 0.0F);
        graphics.pose().scale(scale, scale, 0.0F);
    }

    @Inject(method = "renderEffects", at = @At("RETURN"))
    private void descaleGraphicsAndOverlay(GuiGraphics graphics, CallbackInfo ci) {
        // Replicate vanilla placement algorithm to place labels correctly
        Collection<MobEffectInstance> effects = this.minecraft.player.getActiveEffects();
        if (!effects.isEmpty()) {

            int beneficialCount = 0;
            int nonBeneficialCount = 0;

            for (MobEffectInstance effectInstance : Ordering.natural().reverse().sortedCopy(effects)) {
                MobEffect effect = effectInstance.getEffect();
                if (effectInstance.showIcon()) {
                    int x = graphics.guiWidth();
                    int y = 1;
                    if (this.minecraft.isDemo()) {
                        y += 15;
                    }

                    if (effect.isBeneficial()) {
                        ++beneficialCount;
                        x -= 25 * beneficialCount;
                    } else {
                        ++nonBeneficialCount;
                        x -= 25 * nonBeneficialCount;
                        y += 26;
                    }

                    // Render potency overlay
                    if (Config.get().potencyEnabled && effectInstance.getAmplifier() > 0) {
                        String label = MiscUtil.getAmplifierAsString(effectInstance.getAmplifier());
                        int labelWidth = minecraft.font.width(label);
                        int posX = x + MiscUtil.getTextOffsetX(Config.get().getPotencyLocation(), labelWidth);
                        int posY = y + MiscUtil.getTextOffsetY(Config.get().getPotencyLocation());
                        graphics.fill(posX, posY, posX + labelWidth, posY + minecraft.font.lineHeight - 1,
                                Config.get().getPotencyBackColor());
                        graphics.drawString(minecraft.font, label, posX, posY, Config.get().getPotencyColor(), false);
                    }
                    // Render timer overlay
                    if (Config.get().timerEnabled && (Config.get().timerEnabledAmbient || !effectInstance.isAmbient())) {
                        String label = MiscUtil.getDurationAsString(effectInstance.getDuration());
                        int labelWidth = minecraft.font.width(label);
                        int posX = x + MiscUtil.getTextOffsetX(Config.get().getTimerLocation(), labelWidth);
                        int posY = y + MiscUtil.getTextOffsetY(Config.get().getTimerLocation());
                        graphics.fill(posX, posY, posX + labelWidth, posY + minecraft.font.lineHeight - 1,
                                Config.get().getTimerBackColor());
                        int color = MiscUtil.getTimerColor(effectInstance);
                        graphics.drawString(minecraft.font, label, posX, posY, color, false);
                    }
                }
            }
        }
        graphics.pose().popPose();
    }
}

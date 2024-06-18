package dev.terminalmc.effecttimerplus.mixin;

import com.google.common.collect.Ordering;
import dev.terminalmc.effecttimerplus.config.Config;
import dev.terminalmc.effecttimerplus.util.MiscUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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

    @Unique
    private boolean effectTimerPlus$operate;

    @Inject(method = "renderEffects", at = @At("HEAD"))
    private void scaleGraphics(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        effectTimerPlus$operate = false;
        Collection effects;
        label40:
        {
            effects = this.minecraft.player.getActiveEffects();
            if (!effects.isEmpty()) {
                Screen screen = this.minecraft.screen;
                if (!(screen instanceof EffectRenderingInventoryScreen)) {
                    break label40;
                }

                EffectRenderingInventoryScreen invScreen = (EffectRenderingInventoryScreen) screen;
                if (!invScreen.canSeeEffects()) {
                    break label40;
                }
            }
            return;
        }
        float scale = (float) Config.get().scale;
        effectTimerPlus$operate = true;
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 0.0F);
//        this.screenWidth = (int) (graphics.guiWidth() / scale);
    }

    @ModifyVariable(method = "renderEffects", at = @At("STORE"), ordinal = 2)
    private int moveIcons(int original, GuiGraphics graphics) {
        if (effectTimerPlus$operate) {
            return (int) ((double)graphics.guiWidth() / Config.get().scale);
        }
        return original;
    }

    @Inject(method = "renderEffects", at = @At("RETURN"))
    private void descaleGraphicsAndOverlay(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        if (effectTimerPlus$operate) {
            // Replicate vanilla placement algorithm to place labels correctly
            Collection<MobEffectInstance> effects = this.minecraft.player.getActiveEffects();
            if (!effects.isEmpty()) {

                int beneficialCount = 0;
                int nonBeneficialCount = 0;

                for (MobEffectInstance effectInstance : Ordering.natural().reverse().sortedCopy(effects)) {
                    Holder<MobEffect> effect = effectInstance.getEffect();
                    if (effectInstance.showIcon()) {
                        int x = (int) ((double)graphics.guiWidth() / Config.get().scale); // this.screenWidth
                        int y = 1;
                        if (this.minecraft.isDemo()) {
                            y += 15;
                        }

                        if (effect.value().isBeneficial()) {
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
                graphics.pose().popPose();
//                this.screenWidth = graphics.guiWidth();
            }
        }
    }
}

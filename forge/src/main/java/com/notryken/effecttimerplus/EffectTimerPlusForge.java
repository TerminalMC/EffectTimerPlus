package com.notryken.effecttimerplus;

import com.notryken.effecttimerplus.gui.screen.OptionsScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(EffectTimerPlus.MOD_ID)
public class EffectTimerPlusForge {
    public EffectTimerPlusForge() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, parent) -> new OptionsScreen(parent))
                );

        EffectTimerPlus.init();
    }
}

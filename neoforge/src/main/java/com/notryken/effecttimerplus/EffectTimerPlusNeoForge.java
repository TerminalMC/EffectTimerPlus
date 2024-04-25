package com.notryken.effecttimerplus;

import com.notryken.effecttimerplus.gui.screen.OptionsScreen;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.ConfigScreenHandler;

@Mod(EffectTimerPlus.MOD_ID)
public class EffectTimerPlusNeoForge {
    public EffectTimerPlusNeoForge() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, parent) -> new OptionsScreen(parent))
                );

        EffectTimerPlus.init();
    }
}

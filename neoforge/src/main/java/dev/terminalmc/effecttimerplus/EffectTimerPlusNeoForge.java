package dev.terminalmc.effecttimerplus;

import dev.terminalmc.effecttimerplus.gui.screen.OptionsScreen;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(EffectTimerPlus.MOD_ID)
public class EffectTimerPlusNeoForge {
    public EffectTimerPlusNeoForge() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
                () -> (mc, parent) -> new OptionsScreen(parent));

        EffectTimerPlus.init();
    }
}

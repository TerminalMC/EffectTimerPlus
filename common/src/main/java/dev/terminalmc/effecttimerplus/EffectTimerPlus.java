package dev.terminalmc.effecttimerplus;

import dev.terminalmc.effecttimerplus.config.Config;
import dev.terminalmc.effecttimerplus.util.ModLogger;

public class EffectTimerPlus {
    public static final String MOD_ID = "effecttimerplus";
    public static final String MOD_NAME = "EffectTimerPlus";
    public static final ModLogger LOG = new ModLogger(MOD_NAME);

    public static void init() {
        Config.getAndSave();
    }
}
package com.notryken.effecttimerplus;

import com.notryken.effecttimerplus.config.Config;
import com.notryken.effecttimerplus.util.ModLogger;

public class EffectTimerPlus {
    public static final String MOD_ID = "effecttimerplus";
    public static final String MOD_NAME = "EffectTimerPlus";
    public static final ModLogger LOG = new ModLogger(MOD_NAME);

    public static void init() {
        Config.getAndSave();
    }
}
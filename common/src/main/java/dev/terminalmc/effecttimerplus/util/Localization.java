package dev.terminalmc.effecttimerplus.util;

import dev.terminalmc.effecttimerplus.EffectTimerPlus;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class Localization {
    public static String translationKey(String domain, String path) {
        return domain + "." + EffectTimerPlus.MOD_ID + "." + path;
    }

    public static MutableComponent localized(String domain, String path, Object... args) {
        return Component.translatable(translationKey(domain, path), args);
    }
}

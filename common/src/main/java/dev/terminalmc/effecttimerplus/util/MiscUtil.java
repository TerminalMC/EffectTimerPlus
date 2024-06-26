package dev.terminalmc.effecttimerplus.util;

import dev.terminalmc.effecttimerplus.config.Config;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

public class MiscUtil {

    // ARGB color utils
    public static final IntUnaryOperator toAlpha = (value) -> (value >> 24 & 255);
    public static final IntUnaryOperator toRed = (value) -> (value >> 16 & 255);
    public static final IntUnaryOperator toGreen = (value) -> (value >> 8 & 255);
    public static final IntUnaryOperator toBlue = (value) -> (value & 255);
    public static final IntUnaryOperator fromAlpha = (value) -> (value * 16777216);
    public static final IntUnaryOperator fromRed = (value) -> (value * 65536);
    public static final IntUnaryOperator fromGreen = (value) -> (value * 256);
    public static final IntUnaryOperator fromBlue = (value) -> (value);
    public static final IntBinaryOperator withAlpha = (value, alpha) ->
            (value - (fromAlpha.applyAsInt(toAlpha.applyAsInt(value))) + alpha);
    public static final IntBinaryOperator withRed = (value, red) ->
            (value - (fromRed.applyAsInt(toRed.applyAsInt(value))) + red);
    public static final IntBinaryOperator withGreen = (value, green) ->
            (value - (fromGreen.applyAsInt(toGreen.applyAsInt(value))) + green);
    public static final IntBinaryOperator withBlue = (value, blue) ->
            (value - (fromBlue.applyAsInt(toBlue.applyAsInt(value))) + blue);

    /**
     * @param effectInstance the {@code MobEffectInstance} to get color for.
     * @return the color as defined by mod configuration.
     */
    public static int getTimerColor(MobEffectInstance effectInstance) {
        int color = Config.get().getTimerColor();
        if (Config.get().timerWarnEnabled
                && effectInstance.getDuration() != MobEffectInstance.INFINITE_DURATION
                && effectInstance.getDuration() / 20 <= Config.get().getTimerWarnTime()
                && (!Config.get().timerFlashEnabled
                || effectInstance.getDuration() % 20 >= 10)) {
            color = Config.get().getTimerWarnColor();
        }
        return color;
    }

    /**
     * Converts an amplifier number into a Roman numeral {@code String}, if
     * possible.
     * @param amplifier the amplifier number.
     * @return a {@code String} representing the number, or an empty
     * {@code String} if the number is invalid.
     */
    public static String getAmplifierAsString(int amplifier) {
        int value = amplifier + 1;
        if (value > 1) {
            String key = String.format("enchantment.level.%d", value);
            if (I18n.exists(key)) {
                return I18n.get(key);
            } else {
                return String.valueOf(value);
            }
        }
        return "";
    }

    /**
     * Converts a duration in ticks to a readable approximation.
     * @param durationTicks the duration, in ticks.
     * @return a {@code String} representing the duration.
     */
    public static String getDurationAsString(int durationTicks) {
        if(durationTicks == MobEffectInstance.INFINITE_DURATION) {
            return "\u221e";
        }
        int seconds = durationTicks / 20;
        if (seconds >= 360000) { // 100 hours
            return "\u221e";
        }
        else if (seconds >= 3600) {
            return seconds / 3600 + "h";
        }
        else if (seconds >= 600) {
            return seconds / 60 + "m";
        }
        else if (seconds >= 60) {
            int sec = seconds % 60;
            return seconds / 60 + ":" + (sec > 9 ? sec : "0" + sec);
        }
        else {
            return String.valueOf(seconds);
        }
    }

    /**
     * Determines the X offset for a {@code String} of the given width to be
     * drawn over a status effect icon, based on the given positional index.
     * @param locIndex the positional index (0-7).
     * @param labelWidth the width of the {@code String} to be rendered.
     * @return the X-axis offset.
     * @throws IllegalStateException if the given index is invalid.
     */
    public static int getTextOffsetX(int locIndex, int labelWidth) {
        return switch (locIndex) {
            case 0, 6, 7 -> 3; // Left
            case 1, 5 -> 13 - labelWidth / 2; // Center
            case 2, 3, 4 -> 22 - labelWidth; // Right
            default -> throw new IllegalStateException(
                    "Unexpected positional index outside of allowed range (0-7): " + locIndex);
        };
    }

    /**
     * Determines the Y offset for a {@code String} to be drawn over a status
     * effect icon, based on the given positional index.
     * @param locIndex the positional index (0-7).
     * @return the Y-axis offset.
     * @throws IllegalStateException if the given index is invalid.
     */
    public static int getTextOffsetY(int locIndex) {
        return switch (locIndex) {
            case 0, 1, 2 -> 3; // Top
            case 3, 7 -> 9; // Center
            case 4, 5, 6 -> 14; // Bottom
            default -> throw new IllegalStateException(
                    "Unexpected positional index outside of allowed range (0-7): " + locIndex);
        };
    }
}

/*
 * EffectTimerPlus
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

package dev.terminalmc.effecttimerplus.util;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.effect.MobEffectInstance;

public class IndicatorUtil {

    public static final int SPRITE_BORDER = 3;

    /**
     * @param effectInstance the {@code MobEffectInstance} to get color for.
     * @return the color as defined by mod configuration.
     */
    public static int getTimerColor(
            MobEffectInstance effectInstance,
            int color,
            boolean warn,
            int warnTime,
            int warnColor,
            boolean warnFlash
    ) {
        if (warn
                && effectInstance.getDuration() != MobEffectInstance.INFINITE_DURATION
                && effectInstance.getDuration() / 20 <= warnTime
                && (!warnFlash || effectInstance.getDuration() % 20 >= 10)) {
            color = warnColor;
        }
        return color;
    }

    /**
     * Converts an amplifier number into a Roman numeral {@code String}, if possible.
     *
     * @param amplifier the amplifier number.
     * @return a {@code String} representing the number, or an empty {@code String} if the number is
     * invalid.
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
     *
     * @param durationTicks the duration, in ticks.
     * @return a {@code String} representing the duration.
     */
    public static String getDurationAsString(int durationTicks) {
        if (durationTicks == MobEffectInstance.INFINITE_DURATION) {
            return "\u221e";
        }
        int seconds = durationTicks / 20;
        if (seconds >= 360000) { // 100 hours
            return "\u221e";
        } else if (seconds >= 3600) {
            return seconds / 3600 + "h";
        } else if (seconds >= 600) {
            return seconds / 60 + "m";
        } else if (seconds >= 60) {
            int sec = seconds % 60;
            return seconds / 60 + ":" + (sec > 9 ? sec : "0" + sec);
        } else {
            return String.valueOf(seconds);
        }
    }

    /**
     * Determines the X offset for a {@code String} of the given width to be drawn over a status
     * effect icon, based on the given positional index.
     *
     * @param locIndex   the positional index (0-7).
     * @param labelWidth the width of the {@code String} to be rendered.
     * @return the X-axis offset.
     * @throws IllegalStateException if the given index is invalid.
     */
    public static int getTextOffsetX(int locIndex, int labelWidth, int spriteWidth) {
        return switch (locIndex) {
            case 0, 6, 7 -> SPRITE_BORDER; // Left
            case 1, 5 -> (spriteWidth / 2) - (labelWidth / 2); // Center
            case 2, 3, 4 -> spriteWidth - SPRITE_BORDER - (labelWidth - 1); // Right
            default -> throw new IllegalStateException(
                    "Unexpected positional index outside of allowed range (0-7): " + locIndex);
        };
    }

    /**
     * Determines the Y offset for a {@code String} to be drawn over a status effect icon, based on
     * the given positional index.
     *
     * @param locIndex the positional index (0-7).
     * @return the Y-axis offset.
     * @throws IllegalStateException if the given index is invalid.
     */
    public static int getTextOffsetY(int locIndex, int labelHeight, int spriteHeight) {
        return switch (locIndex) {
            case 0, 1, 2 -> SPRITE_BORDER; // Top
            case 3, 7 -> (spriteHeight / 2) - (labelHeight / 2); // Center
            case 4, 5, 6 -> spriteHeight - SPRITE_BORDER - (labelHeight - 2); // Bottom
            default -> throw new IllegalStateException(
                    "Unexpected positional index outside of allowed range (0-7): " + locIndex);
        };
    }

    public static float getScaleTranslateX(int locIndex, int width, float scale) {
        return switch (locIndex) {
            case 0, 6, 7 -> 0.0F; // Left
            case 1, 5 -> (width / 2.0F) * (1 - scale); // Center
            case 2, 3, 4 -> (width - 1) * (1 - scale); // Right
            default -> throw new IllegalStateException(
                    "Unexpected positional index outside of allowed range (0-7): " + locIndex);
        };
    }

    public static float getScaleTranslateY(int locIndex, int height, float scale) {
        return switch (locIndex) {
            case 0, 1, 2 -> 0.0F; // Top
            case 3, 7 -> (height / 2.0F) * (1 - scale); // Center
            case 4, 5, 6 -> (height - 2) * (1 - scale); // Bottom
            default -> throw new IllegalStateException(
                    "Unexpected positional index outside of allowed range (0-7): " + locIndex);
        };
    }
}

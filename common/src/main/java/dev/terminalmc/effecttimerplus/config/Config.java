/*
 * EffectTimerPlus
 * Copyright (C) 2025 TerminalMC
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

package dev.terminalmc.effecttimerplus.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.terminalmc.effecttimerplus.EffectTimerPlus;
import dev.terminalmc.effecttimerplus.platform.Services;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Config {

    public final int version = 1;
    private static final Path DIR_PATH = Services.PLATFORM.getConfigDir();
    private static final String FILE_NAME = EffectTimerPlus.MOD_ID + ".json";
    private static final String BACKUP_FILE_NAME = EffectTimerPlus.MOD_ID + ".unreadable.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Options
    // Not using Options subclass due to legacy config format

    public static final double defaultScale = 1.0F;
    public double scale = defaultScale;

    public static final double defaultPotencyScale = 0.7F;
    public double potencyScale = defaultPotencyScale;

    public static final double defaultTimerScale = 0.7F;
    public double timerScale = defaultTimerScale;

    public static final boolean defaultPotencyEnabled = true;
    public boolean potencyEnabled = defaultPotencyEnabled;

    public static final boolean defaultTimerEnabled = true;
    public boolean timerEnabled = defaultTimerEnabled;

    public static final boolean defaultTimerEnabledAmbient = false;
    public boolean timerEnabledAmbient = defaultTimerEnabledAmbient;

    public static final boolean defaultTimerWarnEnabled = true;
    public boolean timerWarnEnabled = defaultTimerWarnEnabled;

    public static final boolean defaultTimerFlashEnabled = true;
    public boolean timerFlashEnabled = defaultTimerFlashEnabled;

    public static final int defaultTimerWarnTime = 20;
    public int timerWarnTime = defaultTimerWarnTime;

    public static final int defaultPotencyColor = -1140850689;
    public int potencyColor = defaultPotencyColor;

    public static final boolean defaultPotencyShadow = true;
    public boolean potencyShadow = defaultPotencyShadow;

    public static final boolean defaultPotencyBack = false;
    public boolean potencyBack = defaultPotencyBack;

    public static final int defaultPotencyBackColor = -1140850689;
    public int potencyBackColor = defaultPotencyBackColor;

    public static final int defaultTimerColor = -1711276033;
    public int timerColor = defaultTimerColor;

    public static final int defaultTimerWarnColor = -65536;
    public int timerWarnColor = defaultTimerWarnColor;

    public static final boolean defaultTimerShadow = true;
    public boolean timerShadow = defaultTimerShadow;

    public static final boolean defaultTimerBack = false;
    public boolean timerBack = defaultTimerBack;

    public static final int defaultTimerBackColor = -1776213727;
    public int timerBackColor = defaultTimerBackColor;

    public static final Integer[] locations = {0, 1, 2, 3, 4, 5, 6, 7};

    public static final int defaultPotencyLocation = 2;
    public int potencyLocation = defaultPotencyLocation;

    public static final int defaultTimerLocation = 6;
    public int timerLocation = defaultTimerLocation;

    // Instance management

    private static Config instance = null;

    public static Config get() {
        if (instance == null) {
            instance = Config.load();
        }
        return instance;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static Config getAndSave() {
        get();
        save();
        return instance;
    }

    @SuppressWarnings("unused")
    public static Config reloadAndSave() {
        instance = Config.load();
        save();
        return instance;
    }

    @SuppressWarnings("unused")
    public static Config resetAndSave() {
        instance = new Config();
        save();
        return instance;
    }

    // Validation

    /**
     * Cleanup and validation method, called after config is loaded and before it is saved.
     */
    private void validate() {
        timerLocation = Math.clamp(timerLocation, 0, 7);
        potencyLocation = Math.clamp(potencyLocation, 0, 7);
    }

    // Load and save

    public static @NotNull Config load() {
        Path file = DIR_PATH.resolve(FILE_NAME);
        @Nullable Config config = null;
        if (Files.exists(file)) {
            config = load(file, GSON);
            if (config == null) {
                backup();
                EffectTimerPlus.LOG.warn("Resetting config");
            }
        }
        if (config == null)
            config = new Config();
        config.validate();
        return config;
    }

    @SuppressWarnings("SameParameterValue")
    private static @Nullable Config load(Path file, Gson gson) {
        try (
                InputStreamReader reader = new InputStreamReader(
                        new FileInputStream(file.toFile()),
                        StandardCharsets.UTF_8
                )
        ) {
            return gson.fromJson(reader, Config.class);
        } catch (Exception e) {
            // Catch Exception as errors in deserialization may not fall under
            // IOException or JsonParseException, but should not crash the game.
            EffectTimerPlus.LOG.error("Unable to load config", e);
            return null;
        }
    }

    private static void backup() {
        try {
            EffectTimerPlus.LOG.warn("Copying {} to {}", FILE_NAME, BACKUP_FILE_NAME);
            if (!Files.isDirectory(DIR_PATH))
                Files.createDirectories(DIR_PATH);
            Path file = DIR_PATH.resolve(FILE_NAME);
            Path backupFile = file.resolveSibling(BACKUP_FILE_NAME);
            Files.move(
                    file,
                    backupFile,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            EffectTimerPlus.LOG.error("Unable to copy config file", e);
        }
    }

    public static void save() {
        if (instance == null)
            return;
        instance.validate();
        try {
            if (!Files.isDirectory(DIR_PATH))
                Files.createDirectories(DIR_PATH);
            Path file = DIR_PATH.resolve(FILE_NAME);
            Path tempFile = file.resolveSibling(file.getFileName() + ".tmp");
            try (
                    OutputStreamWriter writer = new OutputStreamWriter(
                            new FileOutputStream(tempFile.toFile()),
                            StandardCharsets.UTF_8
                    )
            ) {
                writer.write(GSON.toJson(instance));
            } catch (IOException e) {
                throw new IOException(e);
            }
            Files.move(
                    tempFile,
                    file,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );
            EffectTimerPlus.onConfigSaved(instance);
        } catch (IOException e) {
            EffectTimerPlus.LOG.error("Unable to save config", e);
        }
    }
}

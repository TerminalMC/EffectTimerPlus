package dev.terminalmc.effecttimerplus.config;

import com.google.gson.*;
import dev.terminalmc.effecttimerplus.EffectTimerPlus;
import dev.terminalmc.effecttimerplus.util.MiscUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Config {
    public final int version = 1;
    private static final Path DIR_PATH = Path.of("config");
    private static final String FILE_NAME = EffectTimerPlus.MOD_ID + ".json";
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Config.class, new Config.Deserializer())
            .setPrettyPrinting()
            .create();

    // Options

    public static final float DEFAULT_SCALE = 1.0F;
    public static final int DEFAULT_COLOR = -1711276033; // 0x99FFFFFF
    public static final int DEFAULT_BACK_COLOR = -1776213727;
    public static final int DEFAULT_WARN_COLOR = -65536; // 0xFFFF0000
    public static final int DEFAULT_WARN_TIME = 20;
    public static final int DEFAULT_POTENCY_LOCATION = 2;
    public static final int DEFAULT_COUNTDOWN_LOCATION = 6;

    public double scale;
    public boolean potencyEnabled;
    public boolean timerEnabled;
    public boolean timerEnabledAmbient;
    public boolean timerWarnEnabled;
    public boolean timerFlashEnabled;
    private int timerWarnTime;
    private int potencyColor;
    private int potencyBackColor;
    private int timerColor;
    private int timerWarnColor;
    private int timerBackColor;
    private int potencyLocation;
    private int timerLocation;

    public Config(double scale, boolean potencyEnabled, boolean timerEnabled, boolean timerEnabledAmbient,
                  boolean timerWarnEnabled, boolean timerFlashEnabled, int timerWarnTime, int potencyColor,
                  int potencyBackColor, int timerColor, int timerWarnColor, int timerBackColor,
                  int potencyLocation, int timerLocation) {
        this.scale = scale;
        this.potencyEnabled = potencyEnabled;
        this.timerEnabled = timerEnabled;
        this.timerEnabledAmbient = timerEnabledAmbient;
        this.timerWarnEnabled = timerWarnEnabled;
        this.timerFlashEnabled = timerFlashEnabled;
        this.timerWarnTime = timerWarnTime;
        this.potencyColor = potencyColor;
        this.potencyBackColor = potencyBackColor;
        this.timerColor = timerColor;
        this.timerWarnColor = timerWarnColor;
        this.timerBackColor = timerBackColor;
        this.potencyLocation = potencyLocation;
        this.timerLocation = timerLocation;
    }

    public Config() {
        scale = DEFAULT_SCALE;
        potencyEnabled = true;
        timerEnabled = true;
        timerEnabledAmbient = false;
        timerWarnEnabled = true;
        timerFlashEnabled = true;
        timerWarnTime = DEFAULT_WARN_TIME;
        potencyColor = DEFAULT_COLOR;
        potencyBackColor = DEFAULT_BACK_COLOR;
        timerColor = DEFAULT_COLOR;
        timerBackColor = DEFAULT_BACK_COLOR;
        timerWarnColor = DEFAULT_WARN_COLOR;
        potencyLocation = DEFAULT_POTENCY_LOCATION;
        timerLocation = DEFAULT_COUNTDOWN_LOCATION;
    }

    public int getTimerWarnTime() {
        return timerWarnTime;
    }

    public void setTimerWarnTime(int time) {
        this.timerWarnTime = time < 0 ? DEFAULT_WARN_TIME : time;
    }

    public int getPotencyColor() {
        return potencyColor;
    }

    public void setPotencyColor(int color) {
        this.potencyColor = adjustColor(color);
    }

    public int getPotencyBackColor() {
        return potencyBackColor;
    }

    public void setPotencyBackColor(int color) {
        this.potencyBackColor = adjustColor(color);
    }

    public int getTimerColor() {
        return timerColor;
    }

    public void setTimerColor(int color) {
        this.timerColor = adjustColor(color);
    }

    public int getTimerBackColor() {
        return timerBackColor;
    }

    public void setTimerBackColor(int color) {
        this.timerBackColor = adjustColor(color);
    }

    public int getTimerWarnColor() {
        return timerWarnColor;
    }

    public void setTimerWarnColor(int color) {
        this.timerWarnColor = adjustColor(color);
    }

    public int getPotencyLocation() {
        return potencyLocation;
    }

    public void setPotencyLocation(int locIndex) {
        this.potencyLocation = locIndex;
    }

    public int getTimerLocation() {
        return timerLocation;
    }

    public void setTimerLocation(int locIndex) {
        this.timerLocation = locIndex;
    }

    // Workaround for alpha slider bug
    private int adjustColor(int color) {
        if (MiscUtil.toAlpha.applyAsInt(color) < 4) {
            return MiscUtil.withAlpha.applyAsInt(color, MiscUtil.fromAlpha.applyAsInt(4));
        }
        return color;
    }

    public void resetPotencyConfig() {
        potencyEnabled = true;
        potencyColor = DEFAULT_COLOR;
        potencyBackColor = DEFAULT_BACK_COLOR;
        potencyLocation = DEFAULT_POTENCY_LOCATION;
    }

    public void resetTimerConfig() {
        timerEnabled = true;
        timerEnabledAmbient = false;
        timerWarnEnabled = true;
        timerFlashEnabled = true;
        timerWarnTime = DEFAULT_WARN_TIME;
        timerColor = DEFAULT_COLOR;
        timerWarnColor = DEFAULT_WARN_COLOR;
        timerBackColor = DEFAULT_BACK_COLOR;
        timerLocation = DEFAULT_POTENCY_LOCATION;
    }

    // Instance management

    private static Config instance = null;

    public static Config get() {
        if (instance == null) {
            instance = Config.load();
        }
        return instance;
    }

    public static Config getAndSave() {
        get();
        save();
        return instance;
    }

    public static Config resetAndSave() {
        instance = new Config();
        save();
        return instance;
    }


    // Load and save

    public static @NotNull Config load() {
        Path file = DIR_PATH.resolve(FILE_NAME);
        Config config = null;
        if (Files.exists(file)) {
            config = load(file, GSON);
        }
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    private static @Nullable Config load(Path file, Gson gson) {
        try (FileReader reader = new FileReader(file.toFile())) {
            return gson.fromJson(reader, Config.class);
        } catch (Exception e) {
            EffectTimerPlus.LOG.error("Unable to load config.", e);
            return null;
        }
    }

    public static void save() {
        if (instance == null) return;
        try {
            if (!Files.isDirectory(DIR_PATH)) {
                Files.createDirectories(DIR_PATH);
            }
            Path file = DIR_PATH.resolve(FILE_NAME);
            Path tempFile = file.resolveSibling(file.getFileName() + ".tmp");

            try (FileWriter writer = new FileWriter(tempFile.toFile())) {
                writer.write(GSON.toJson(instance));
            }
            catch (IOException e) {
                throw new IOException(e);
            }
            Files.move(tempFile, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            EffectTimerPlus.LOG.error("Unable to save config.", e);
        }
    }

    // Deserialization

    public static class Deserializer implements JsonDeserializer<Config> {
        @Override
        public Config deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            double scale = obj.get("scale").getAsDouble();
            boolean potencyEnabled = obj.get("potencyEnabled").getAsBoolean();
            boolean timerEnabled = obj.get("timerEnabled").getAsBoolean();
            boolean timerEnabledAmbient = obj.get("timerEnabledAmbient").getAsBoolean();
            boolean timerWarnEnabled = obj.get("timerWarnEnabled").getAsBoolean();
            boolean timerFlashEnabled = obj.get("timerFlashEnabled").getAsBoolean();
            int timerWarnTime = obj.get("timerWarnTime").getAsInt();
            int potencyColor = obj.get("potencyColor").getAsInt();
            int potencyBackColor = obj.get("potencyBackColor").getAsInt();
            int timerColor = obj.get("timerColor").getAsInt();
            int timerWarnColor = obj.get("timerWarnColor").getAsInt();
            int timerBackColor = obj.get("timerBackColor").getAsInt();
            int potencyLocation = obj.get("potencyLocation").getAsInt();
            int timerLocation = obj.get("timerLocation").getAsInt();

            return new Config(scale, potencyEnabled, timerEnabled, timerEnabledAmbient,
                    timerWarnEnabled, timerFlashEnabled, timerWarnTime, potencyColor,
                    potencyBackColor, timerColor, timerWarnColor, timerBackColor,
                    potencyLocation, timerLocation);
        }
    }
}

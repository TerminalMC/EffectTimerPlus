package dev.terminalmc.effecttimerplus.gui.widget.list;

import dev.terminalmc.effecttimerplus.config.Config;
import dev.terminalmc.effecttimerplus.gui.screen.OptionsScreen;
import dev.terminalmc.effecttimerplus.util.MiscUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PotencyOptionsList extends OptionsList {

    protected OptionsList.Entry header;
    protected OptionsList.Entry toggleButton;
    protected OptionsList.Entry cornerButton;
    protected OptionsList.Entry colorSelectionSet;
    protected OptionsList.Entry alphaSlider;
    protected OptionsList.Entry redSlider;
    protected OptionsList.Entry greenSlider;
    protected OptionsList.Entry blueSlider;
    protected OptionsList.Entry backAlphaSlider;
    protected OptionsList.Entry resetButton;


    public PotencyOptionsList(Minecraft minecraft, int width, int height, int x, int y,
                              int itemHeight, OptionsScreen parent) {
        super(minecraft, width, height, x, y, itemHeight, parent);

        int unitWidth = 200;
        int unitHeight = 18;
        int unitX = width - unitWidth - 10;

        Supplier<Integer> colorSource = Config.get()::getPotencyColor;
        Consumer<Integer> colorDest = Config.get()::setPotencyColor;

        header = new OptionsList.Entry.TextEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Potency Text Options"));
        toggleButton = new OptionsList.Entry.OnOffButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Display"), Config.get().potencyEnabled,
                (value) -> Config.get().potencyEnabled = value);
        cornerButton = new OptionsList.Entry.IntCycleButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Location"), Config.get().getPotencyLocation(), (value) ->
                switch(value) {
                    case 0:
                        yield Component.literal("Top Left");
                    case 1:
                        yield Component.literal("Top Center");
                    case 2:
                        yield Component.literal("Top Right");
                    case 3:
                        yield Component.literal("Center Right");
                    case 4:
                        yield Component.literal("Bottom Right");
                    case 5:
                        yield Component.literal("Bottom Center");
                    case 6:
                        yield Component.literal("Bottom Left");
                    case 7:
                        yield Component.literal("Center Left");
                    default:
                        throw new IllegalStateException(
                                "Unexpected positional index outside of allowed range (0-7): " + value);
                },
                new Integer[]{0, 1, 2, 3, 4, 5, 6, 7},
                (value) -> Config.get().setPotencyLocation(value));
        colorSelectionSet = new OptionsList.Entry.ColorSelectionSet(this, unitX, 0, unitWidth,
                (color) -> colorDest.accept(MiscUtil.withAlpha.applyAsInt(color,
                        MiscUtil.fromAlpha.applyAsInt(MiscUtil.toAlpha.applyAsInt(colorSource.get())))));

        alphaSlider = new Entry.ArgbSliderEntry(this, unitX, unitWidth, unitHeight, "Opacity: ", colorSource,
                (color) -> colorDest.accept(MiscUtil.withAlpha.applyAsInt(colorSource.get(), color)),
                MiscUtil.toAlpha, MiscUtil.fromAlpha);
        redSlider = new Entry.ArgbSliderEntry(this, unitX, unitWidth, unitHeight, "Red: ", colorSource,
                (color) -> colorDest.accept(MiscUtil.withRed.applyAsInt(colorSource.get(), color)),
                MiscUtil.toRed, MiscUtil.fromRed);
        greenSlider = new Entry.ArgbSliderEntry(this, unitX, unitWidth, unitHeight, "Green: ", colorSource,
                (color) -> colorDest.accept(MiscUtil.withGreen.applyAsInt(colorSource.get(), color)),
                MiscUtil.toGreen, MiscUtil.fromGreen);
        blueSlider = new Entry.ArgbSliderEntry(this, unitX, unitWidth, unitHeight, "Blue: ", colorSource,
                (color) -> colorDest.accept(MiscUtil.withBlue.applyAsInt(colorSource.get(), color)),
                MiscUtil.toBlue, MiscUtil.fromBlue);

        Supplier<Integer> backColorSource = Config.get()::getPotencyBackColor;
        Consumer<Integer> backColorDest = Config.get()::setPotencyBackColor;
        backAlphaSlider = new Entry.ArgbSliderEntry(this, unitX, unitWidth, unitHeight, "Background Opacity: ", backColorSource,
                (color) -> backColorDest.accept(MiscUtil.withAlpha.applyAsInt(backColorSource.get(), color)),
                MiscUtil.toAlpha, MiscUtil.fromAlpha);

        resetButton = new OptionsList.Entry.ActionButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Reset"),
                (button) -> {
                    Config.get().resetPotencyConfig();
                    reload();
                });

        addEntry(header);
        addEntry(toggleButton);
        addEntry(cornerButton);
        addEntry(colorSelectionSet);
        addEntry(alphaSlider);
        addEntry(redSlider);
        addEntry(greenSlider);
        addEntry(blueSlider);
        addEntry(backAlphaSlider);
        addEntry(resetButton);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 6 + x0;
    }
}

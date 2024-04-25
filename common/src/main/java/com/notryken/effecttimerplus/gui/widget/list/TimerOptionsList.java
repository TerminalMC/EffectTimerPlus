package com.notryken.effecttimerplus.gui.widget.list;

import com.notryken.effecttimerplus.config.Config;
import com.notryken.effecttimerplus.gui.screen.OptionsScreen;
import com.notryken.effecttimerplus.gui.widget.slider.DoubleSlider;
import com.notryken.effecttimerplus.util.MiscUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TimerOptionsList extends OptionsList {

    protected OptionsList.Entry mainHeader;
    protected OptionsList.Entry mainToggleButton;
    protected OptionsList.Entry cornerButton;
    protected OptionsList.Entry colorSelectionSet;
    protected OptionsList.Entry alphaSlider;
    protected OptionsList.Entry redSlider;
    protected OptionsList.Entry greenSlider;
    protected OptionsList.Entry blueSlider;
    protected OptionsList.Entry backAlphaSlider;
    protected OptionsList.Entry warnHeader;
    protected OptionsList.Entry warnToggleButton;
    protected OptionsList.Entry warnTimeSlider;
    protected OptionsList.Entry warnColorSelectionSet;
    protected OptionsList.Entry warnAlphaSlider;
    protected OptionsList.Entry warnRedSlider;
    protected OptionsList.Entry warnGreenSlider;
    protected OptionsList.Entry warnBlueSlider;
    protected OptionsList.Entry resetButton;


    public TimerOptionsList(Minecraft minecraft, int width, int height, int top, int bottom,
                            int itemHeight, OptionsScreen parent) {
        super(minecraft, width, height, top, bottom, itemHeight, parent);

        int unitWidth = 200;
        int unitHeight = 18;
        int unitX = parent.width - width + 10;

        Supplier<Integer> colorSource = Config.get()::getTimerColor;
        Consumer<Integer> colorDest = Config.get()::setTimerColor;

        mainHeader = new OptionsList.Entry.TextEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Timer Text Options"));
        mainToggleButton = new OptionsList.Entry.DualOnOffButtonEntry(this, unitX, 0,
                unitWidth, unitHeight,
                Component.literal("Display"), Config.get().timerEnabled,
                (value) -> Config.get().timerEnabled = value,
                Component.literal("Beacon"), Config.get().timerEnabledAmbient,
                (value) -> Config.get().timerEnabledAmbient = value);
        cornerButton = new OptionsList.Entry.IntCycleButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Location"), Config.get().getTimerLocation(), (value) ->
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
                new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8},
                (value) -> Config.get().setTimerLocation(value));
        colorSelectionSet = new OptionsList.Entry.ColorSelectionSet(this, unitX, 0, unitWidth,
                (color) -> colorDest.accept(MiscUtil.withAlpha.applyAsInt(color,
                        MiscUtil.fromAlpha.applyAsInt(MiscUtil.toAlpha.applyAsInt(colorSource.get())))));

        alphaSlider = new OptionsList.Entry.ArgbSliderEntry(this, unitX, unitWidth, unitHeight, "Opacity: ", colorSource,
                (color) -> colorDest.accept(MiscUtil.withAlpha.applyAsInt(colorSource.get(), color)),
                MiscUtil.toAlpha, MiscUtil.fromAlpha);
        redSlider = new OptionsList.Entry.ArgbSliderEntry(this, unitX, unitWidth, unitHeight, "Red: ", colorSource,
                (color) -> colorDest.accept(MiscUtil.withRed.applyAsInt(colorSource.get(), color)),
                MiscUtil.toRed, MiscUtil.fromRed);
        greenSlider = new OptionsList.Entry.ArgbSliderEntry(this, unitX, unitWidth, unitHeight, "Green: ", colorSource,
                (color) -> colorDest.accept(MiscUtil.withGreen.applyAsInt(colorSource.get(), color)),
                MiscUtil.toGreen, MiscUtil.fromGreen);
        blueSlider = new OptionsList.Entry.ArgbSliderEntry(this, unitX, unitWidth, unitHeight, "Blue: ", colorSource,
                (color) -> colorDest.accept(MiscUtil.withBlue.applyAsInt(colorSource.get(), color)),
                MiscUtil.toBlue, MiscUtil.fromBlue);

        Supplier<Integer> backColorSource = Config.get()::getTimerBackColor;
        Consumer<Integer> backColorDest = Config.get()::setTimerBackColor;
        backAlphaSlider = new OptionsList.Entry.ArgbSliderEntry(this, unitX, unitWidth, unitHeight, "Background Opacity: ", backColorSource,
                (color) -> backColorDest.accept(MiscUtil.withAlpha.applyAsInt(backColorSource.get(), color)),
                MiscUtil.toAlpha, MiscUtil.fromAlpha);

        Supplier<Integer> warnColorSource = Config.get()::getTimerWarnColor;
        Consumer<Integer> warnColorDest = Config.get()::setTimerWarnColor;
        warnHeader = new OptionsList.Entry.TextEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Low Time Warning Options"));
        warnToggleButton = new OptionsList.Entry.DualOnOffButtonEntry(this, unitX, 0,
                unitWidth, unitHeight,
                Component.literal("Warn Color"), Config.get().timerWarnEnabled,
                (value) -> Config.get().timerWarnEnabled = value,
                Component.literal("Warn Flash"), Config.get().timerFlashEnabled,
                (value) -> Config.get().timerFlashEnabled = value);
        warnTimeSlider = new Entry.WarnTimeSlider(this, unitX, 0, unitWidth, unitHeight);
        warnColorSelectionSet = new OptionsList.Entry.ColorSelectionSet(this, unitX, 0, unitWidth,
                (color) -> warnColorDest.accept(MiscUtil.withAlpha.applyAsInt(color,
                        MiscUtil.fromAlpha.applyAsInt(MiscUtil.toAlpha.applyAsInt(warnColorSource.get())))));

        warnAlphaSlider = new OptionsList.Entry.ArgbSliderEntry(this, unitX, unitWidth, unitHeight, "Opacity: ", warnColorSource,
                (color) -> warnColorDest.accept(MiscUtil.withAlpha.applyAsInt(warnColorSource.get(), color)),
                MiscUtil.toAlpha, MiscUtil.fromAlpha);
        warnRedSlider = new OptionsList.Entry.ArgbSliderEntry(this, unitX, unitWidth, unitHeight, "Red: ", warnColorSource,
                (color) -> warnColorDest.accept(MiscUtil.withRed.applyAsInt(warnColorSource.get(), color)),
                MiscUtil.toRed, MiscUtil.fromRed);
        warnGreenSlider = new OptionsList.Entry.ArgbSliderEntry(this, unitX, unitWidth, unitHeight, "Green: ", warnColorSource,
                (color) -> warnColorDest.accept(MiscUtil.withGreen.applyAsInt(warnColorSource.get(), color)),
                MiscUtil.toGreen, MiscUtil.fromGreen);
        warnBlueSlider = new OptionsList.Entry.ArgbSliderEntry(this, unitX, unitWidth, unitHeight, "Blue: ", warnColorSource,
                (color) -> warnColorDest.accept(MiscUtil.withBlue.applyAsInt(warnColorSource.get(), color)),
                MiscUtil.toBlue, MiscUtil.fromBlue);

        resetButton = new OptionsList.Entry.ActionButtonEntry(this, unitX, 0, unitWidth, unitHeight,
                Component.literal("Reset"),
                (button) -> {
                    Config.get().resetTimerConfig();
                    reload();
                });

        addEntry(mainHeader);
        addEntry(mainToggleButton);
        addEntry(cornerButton);
        addEntry(colorSelectionSet);
        addEntry(alphaSlider);
        addEntry(redSlider);
        addEntry(greenSlider);
        addEntry(blueSlider);
        addEntry(backAlphaSlider);
        addEntry(warnHeader);
        addEntry(warnToggleButton);
        addEntry(warnTimeSlider);
        addEntry(warnColorSelectionSet);
        addEntry(warnAlphaSlider);
        addEntry(warnRedSlider);
        addEntry(warnGreenSlider);
        addEntry(warnBlueSlider);
        addEntry(resetButton);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 6 + x0;
    }

    protected abstract static class Entry extends OptionsList.Entry {
        public Entry(TimerOptionsList list) {
            super(list);
        }

        protected static class WarnTimeSlider extends Entry {

            public WarnTimeSlider(TimerOptionsList list, int x, int y, int width, int height) {
                super(list);

                DoubleSlider warningTimeSlider = new DoubleSlider(x, y, width, height,
                        0, 120, 0, "Warning Time: ", " sec",
                        null, null,
                        () -> (double)Config.get().getTimerWarnTime(),
                        (value) -> Config.get().setTimerWarnTime(value.intValue()));
                elements.add(warningTimeSlider);
            }
        }
    }
}

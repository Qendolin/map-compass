package com.qendolin.mapcompass.config;

import com.qendolin.mapcompass.MapCompassInit;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ConfigGUI {
    public static final String LANG_KEY_PREFIX = MapCompassInit.MODID + ".config";

    protected final Config config;
    protected final Config defaults;

    public final Option<Boolean> enabled;
    public final Option<Config.CompassSide> side;
    public final Option<Boolean> reverseEW;
    public final Option<Config.CompassSize> size;
    public final Option<Config.CompassOffset> offsetDirection;
    public final Option<Integer> offsetX;
    public final Option<Integer> offsetY;

    protected final List<Tuple<ConfigCategory.Builder, List<Tuple<OptionGroup.Builder, List<Option<?>>>>>> categories = new ArrayList<>();

    protected final List<Tuple<OptionGroup.Builder, List<Option<?>>>> generalCategory = new ArrayList<>();

    protected final List<Option<?>> appearanceGroup = new ArrayList<>();

    public ConfigGUI(Config defaults, Config config) {
        this.defaults = defaults;
        this.config = config;

        this.enabled = createOption(boolean.class, "enabled")
            .binding(defaults.enabled, () -> config.enabled, val -> config.enabled = val)
            .controller(TickBoxControllerBuilder::create)
            .description(OptionDescription.createBuilder().text(simpleDescription("enabled")).build())
            .build();

        this.side = createOption(Config.CompassSide.class, "side")
            .binding(defaults.side, () -> config.side, val -> config.side = val)
            .controller(opt -> EnumControllerBuilder.create(opt)
                .enumClass(Config.CompassSide.class)
                .valueFormatter(translateEnumValue("side")))
            .description(val -> OptionDescription.createBuilder().text(enumDescription("side", val)).build())
            .build();
        this.size = createOption(Config.CompassSize.class, "size")
            .binding(defaults.size, () -> config.size, val -> config.size = val)
            .controller(opt -> EnumControllerBuilder.create(opt)
                .enumClass(Config.CompassSize.class)
                .valueFormatter(translateEnumValue("size")))
            .description(val -> OptionDescription.createBuilder().text(enumDescription("size", val)).build())
            .build();
        this.offsetDirection = createOption(Config.CompassOffset.class, "offset")
            .binding(defaults.offsetDirection, () -> config.offsetDirection, val -> config.offsetDirection = val)
            .controller(opt -> EnumControllerBuilder.create(opt)
                .enumClass(Config.CompassOffset.class)
                .valueFormatter(translateEnumValue("offset")))
            .description(val -> OptionDescription.createBuilder().text(enumDescription("offset", val)).build())
            .build();
        this.reverseEW = createOption(boolean.class, "reverseEW")
            .binding(defaults.reverseEW, () -> config.reverseEW, val -> config.reverseEW = val)
            .controller(TickBoxControllerBuilder::create)
            .description(OptionDescription.createBuilder().text(simpleDescription("reverseEW")).build())
            .build();
        this.offsetX = createOption(int.class, "offsetX")
            .binding(defaults.offsetX, () -> config.offsetX, val -> config.offsetX = val)
            .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                .range(-22, 84)
                .step(1))
            .description(OptionDescription.createBuilder().text(simpleDescription("offsetX")).build())
            .build();
        this.offsetY = createOption(int.class, "offsetY")
            .binding(defaults.offsetY, () -> config.offsetY, val -> config.offsetY = val)
            .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                .range(-36, 148)
                .step(1))
            .description(OptionDescription.createBuilder().text(simpleDescription("offsetY")).build())
            .build();

        categories.add(new Tuple<>(ConfigCategory.createBuilder()
            .name(categoryLabel("general")), generalCategory));
        generalCategory.add(new Tuple<>(OptionGroup.createBuilder()
            .name(groupLabel("general.appearance")), appearanceGroup));

        appearanceGroup.addAll(List.of(enabled, side, reverseEW, size, offsetDirection, offsetX, offsetY));
    }

    public static Screen create(Screen parent) {
        YetAnotherConfigLib yacl = YetAnotherConfigLib.create(ConfigManager.handler(),
            (defaults, config, builder) -> new ConfigGUI(defaults, config).assemble(builder));
        return yacl.generateScreen(parent);
    }

    public YetAnotherConfigLib.Builder assemble(YetAnotherConfigLib.Builder builder) {
        builder = builder
            .save(() -> {
                ConfigManager.handler().save();
            })
            .title(Component.translatable(LANG_KEY_PREFIX + ".title"));

        for (Tuple<ConfigCategory.Builder, List<Tuple<OptionGroup.Builder, List<Option<?>>>>> categoryPair : categories) {
            ConfigCategory.Builder categoryBuilder = categoryPair.getA();
            for (Tuple<OptionGroup.Builder, List<Option<?>>> groupPair : categoryPair.getB()) {
                if (groupPair.getB().isEmpty()) continue;
                OptionGroup.Builder groupBuilder = groupPair.getA();
                groupBuilder.options(groupPair.getB());
                categoryBuilder.group(groupBuilder.build());
            }
            builder.category(categoryBuilder.build());
        }

        return builder;
    }

    private static <T> Option.Builder<T> createOption(Class<T> typeClass, String key) {
        return Option.<T>createBuilder()
            .name(optionLabel(key))
            .description(OptionDescription.of(optionDescription(key)));
    }

    private static <T extends Enum<?>> Function<T, Component> translateEnumValue(String name) {
        final String prefix = LANG_KEY_PREFIX+".entry."+name+".";
        return value -> Component.translatable(prefix+value.name().toLowerCase());
    }

    private static Component categoryLabel(String key) {
        return Component.translatable(LANG_KEY_PREFIX + ".category." + key);
    }

    private static Component groupLabel(String key) {
        return Component.translatable(LANG_KEY_PREFIX + ".group." + key);
    }

    private static Component optionLabel(String key) {
        return Component.translatable(LANG_KEY_PREFIX + ".entry." + key);
    }

    private static Component simpleDescription(String key) {
        return Component.translatable(LANG_KEY_PREFIX + ".entry." + key + ".description");
    }
    private static Component enumDescription(String key, Enum<?> value) {
        return Component.translatable(LANG_KEY_PREFIX + ".entry." + key + "." + value.name().toLowerCase() + ".description");
    }

    private static Component optionDescription(String key) {
        return Component.translatable(LANG_KEY_PREFIX + ".entry." + key + ".description");
    }

}

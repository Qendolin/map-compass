package com.qendolin.mapcompass.config;

import net.minecraft.text.Text;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

public abstract class Entry {
    static void init() {
        ConfigScreen.registerWidgetFactory(IntRange.class, (a, x, y, w, h, f, o, s) -> Widgets.intRange(x, y, w, a.min(), a.max(), a.step(), f.getInt(o), (Function<Integer, Text>) s));
        ConfigScreen.registerWidgetFactory(FloatRange.class, (a, x, y, w, h, f, o, s) -> Widgets.floatRange(x, y, w, a.min(), a.max(), a.step(), f.getFloat(o), (Function<Float, Text>) s));
        ConfigScreen.registerWidgetFactory(ToggleButton.class, (a, x, y, w, h, f, o, s) -> Widgets.toggleButton(x, y, w, f.getBoolean(o), (Function<Boolean, Text>) s));
        ConfigScreen.registerWidgetFactory(EnumButton.class, (a, x, y, w, h, f, o, s) -> Widgets.enumButton(x, y, w, (Enum<?>) f.get(o), (Function<Enum<?>, Text>) s));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface IntRange {
        int min() default 0;
        int max();
        int step() default 1;
        String stringer() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface FloatRange {
        float min() default 0f;
        float max() default 1f;
        float step() default 0.05f;
        String stringer() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ToggleButton {
        String stringer() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface EnumButton {
        String stringer() default "";
    }
}

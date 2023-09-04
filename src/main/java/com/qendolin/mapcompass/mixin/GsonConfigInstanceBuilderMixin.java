package com.qendolin.mapcompass.mixin;

import com.google.gson.GsonBuilder;
import com.qendolin.mapcompass.compat.GsonConfigInstanceBuilderDuck;
import dev.isxander.yacl3.config.GsonConfigInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.UnaryOperator;

@Mixin(value = GsonConfigInstance.Builder.class, remap = false)
public abstract class GsonConfigInstanceBuilderMixin<T> implements GsonConfigInstanceBuilderDuck<T> {

    @Shadow
    private UnaryOperator<GsonBuilder> gsonBuilder;

    // GsonConfigInstance.Builder#appendGsonBuilder causes infinite recursion, see issue 64
    public GsonConfigInstance.Builder<T> mapcompass$appendGsonBuilder(UnaryOperator<GsonBuilder> operator) {
        final UnaryOperator<GsonBuilder> prev = gsonBuilder;
        this.gsonBuilder = builder -> operator.apply(prev.apply(builder));
        //noinspection unchecked
        return (GsonConfigInstance.Builder<T>) (Object) this;
    }
}

package com.qendolin.mapcompass.compat;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.GsonConfigInstance;

import java.util.function.UnaryOperator;

public interface GsonConfigInstanceBuilderDuck<T> {
    GsonConfigInstance.Builder<T> mapcompass$appendGsonBuilder(UnaryOperator<GsonBuilder> operator);
}

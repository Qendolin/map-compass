package com.qendolin.mapcompass;

import com.qendolin.mapcompass.config.ConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ConfigScreen<>(parent, Main.CONFIG);
    }
}

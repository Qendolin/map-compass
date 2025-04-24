//? if forge {
/*package com.qendolin.mapcompass.platforms.forge;

import com.qendolin.mapcompass.MapCompassInit;
import com.qendolin.mapcompass.config.ConfigGUI;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("mapcompass")
public class MapCompassForge {

    @Deprecated
    @SuppressWarnings("removal")
    public MapCompassForge() {
        this(FMLJavaModLoadingContext.get());
    }

    public MapCompassForge(FMLJavaModLoadingContext context) {
        MapCompassInit.initialize();

        context.getModEventBus().<FMLClientSetupEvent>addListener(event -> {
            Minecraft.getInstance().execute(MapCompassInit::initialize);

            context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                    (client, parent) -> ConfigGUI.create(parent)));
        });
    }
}
*///?}
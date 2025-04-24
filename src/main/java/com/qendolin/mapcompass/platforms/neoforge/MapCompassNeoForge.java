//? if neoforge {
/*package com.qendolin.mapcompass.platforms.neoforge;

import com.qendolin.mapcompass.MapCompassInit;
import com.qendolin.mapcompass.config.ConfigGUI;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod("mapcompass")
public class MapCompassNeoForge {
    public MapCompassNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(FMLClientSetupEvent.class, event -> {
            Minecraft.getInstance().execute(MapCompassInit::initialize);

            //? if <1.20.6 {
            /^ModLoadingContext.get().registerExtensionPoint(net.neoforged.neoforge.client.ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new net.neoforged.neoforge.client.ConfigScreenHandler.ConfigScreenFactory(
                    (client, parent) -> ConfigGUI.create(parent)));
            ^///?} else {
            ModLoadingContext.get().registerExtensionPoint(net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
                () -> (modContainer, parent) -> ConfigGUI.create(parent));
            //?}
        });
    }
}
*///?}
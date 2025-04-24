package com.qendolin.mapcompass;

import com.qendolin.mapcompass.config.ConfigManager;
import com.qendolin.mapcompass.platforms.ModLoader;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;

public class MapCompassInit {
	public static final boolean IS_DEV = ModLoader.isDevelopmentEnvironment();
	public static final boolean IS_CLIENT = ModLoader.isClientEnvironment();

	public static final String MODID = "mapcompass";
	public static final NamedLogger LOGGER = new NamedLogger(LogManager.getLogger("Map Compass"), !IS_DEV);

	public static void initialize() {
		ConfigManager.initialize();
		ConfigManager.loadWithFailureBackup();
	}

	public static ResourceLocation getResourceLocation(String path) {
		//? if >=1.21 {
		/*return ResourceLocation.fromNamespaceAndPath(MapCompassInit.MODID, path);
		*///?} else {
        //noinspection removal
        return new ResourceLocation(MapCompassInit.MODID, path);
		//?}
	}
}

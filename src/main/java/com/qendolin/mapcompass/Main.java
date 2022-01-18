package com.qendolin.mapcompass;

import com.qendolin.mapcompass.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main implements ClientModInitializer {
	public static final String MODID = "mapcompass";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final Config CONFIG = ConfigManager.createOrLoad(new Config());

	@Override
	public void onInitializeClient() {}
}

package com.qendolin.mapcompass;

import com.qendolin.mapcompass.compat.GsonConfigInstanceBuilderDuck;
import dev.isxander.yacl3.config.GsonConfigInstance;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main implements ClientModInitializer {
	private static final boolean IS_DEV = FabricLoader.getInstance().isDevelopmentEnvironment();
	private static final boolean IS_CLIENT = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	public static final String MODID = "mapcompass";
	public static final NamedLogger LOGGER = new NamedLogger(LogManager.getLogger(MODID), !IS_DEV);
	private static final GsonConfigInstance<Config> CONFIG;
	private static final Path CONFIG_PATH = Path.of("config/mapcompass-v1.json");

	static {
		if (IS_CLIENT) {
			GsonConfigInstance.Builder<Config> builder = GsonConfigInstance
				.createBuilder(Config.class)
				.setPath(CONFIG_PATH);

			if (builder instanceof GsonConfigInstanceBuilderDuck) {
				//noinspection unchecked
				GsonConfigInstanceBuilderDuck<Config> duck = (GsonConfigInstanceBuilderDuck<Config>) builder;
				builder = duck.mapcompass$appendGsonBuilder(b -> b
					.setLenient().setPrettyPrinting());
			}
			CONFIG = builder.build();
		} else {
			CONFIG = null;
		}

	}

	@Override
	public void onInitializeClient() {
		if (!IS_CLIENT)
			throw new IllegalStateException("Fabric environment is " + FabricLoader.getInstance().getEnvironmentType().name() + " but onInitializeClient was called");

		loadConfig();
	}

	private void loadConfig() {
		assert CONFIG != null;

		try {
			CONFIG.load();
			return;
		} catch (Exception loadException) {
			LOGGER.error("Failed to load config: ", loadException);
		}

		File file = CONFIG.getPath().toFile();
		if (file.exists() && file.isFile()) {
			String backupName = FilenameUtils.getBaseName(file.getName()) +
				"-backup-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) +
				"." + FilenameUtils.getExtension(file.getName());
			Path backup = Path.of(CONFIG.getPath().toAbsolutePath().getParent().toString(), backupName);
			try {
				Files.copy(file.toPath(), backup, StandardCopyOption.REPLACE_EXISTING);
				LOGGER.info("Created config backup at: {}", backup);
			} catch (Exception backupException) {
				LOGGER.error("Failed to create config backup: ", backupException);
			}
		} else if (file.exists()) {
			//noinspection ResultOfMethodCallIgnored
			file.delete();
			LOGGER.info("Deleted old config");
		}

		try {
			CONFIG.save();
			LOGGER.info("Created new config");
			CONFIG.load();
		} catch (Exception loadException) {
			LOGGER.error("Failed to load config again, please report this issue: ", loadException);
		}
	}

	public static GsonConfigInstance<Config> getConfigInstance() {
		return CONFIG;
	}

	public static Config getConfig() {
		return CONFIG.getConfig();
	}


}

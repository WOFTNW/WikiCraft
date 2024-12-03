package io.github.iherongh.wikicraft.file.config;

import io.github.iherongh.wikicraft.WikiCraft;
import org.bukkit.configuration.file.FileConfiguration;


public class ConfigUtils {

	private static FileConfiguration configFile;


	public ConfigUtils() {
		configFile = WikiCraft.getInstance().getConfig();

	}

	public FileConfiguration getConfigFile() {
		return configFile;

	}

}

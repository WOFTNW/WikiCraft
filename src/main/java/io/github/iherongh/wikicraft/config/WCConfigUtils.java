package io.github.iherongh.wikicraft.config;

import io.github.iherongh.wikicraft.WikiCraft;
import io.github.iherongh.wikicraft.messages.WCMessages;
import org.bukkit.configuration.file.FileConfiguration;


public class WCConfigUtils {

	public static FileConfiguration configFile = WikiCraft.getInstance().getConfig();

	public static String getWikiURL() {
		if ( configFile.get( "wiki-url" ) instanceof String returnVal ) {
			return returnVal;

		}

		return WCMessages.message( "error", "Configured URL unrecognised." ).toString();

	}

}

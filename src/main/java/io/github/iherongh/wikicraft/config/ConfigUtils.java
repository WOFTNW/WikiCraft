package io.github.iherongh.wikicraft.config;

import io.github.iherongh.wikicraft.WikiCraft;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;
import java.util.Set;


public class ConfigUtils {

	public static FileConfiguration configFile;


	public ConfigUtils() {
		configFile = WikiCraft.getInstance().getConfig();

	}

	public static String getWikiURL() {
		if ( configFile.get( "wiki-url" ) instanceof String returnVal ) {
			return returnVal;

		}

		return "Configured URL unrecognised.";

	}

	public String getConfigContent( String findKey, MemorySection search ) {
		WikiCraft.getInstance().getLogger().info( findKey );

		Set<String> keys = search.getKeys( true );

		for ( int i = 0; i < keys.size(); i++ ) {
			Object key = keys.toArray()[ i ];

			if ( Objects.equals( key, findKey ) ) {
				if ( key instanceof String returnVal ) {
					WikiCraft.getInstance().getLogger().info( returnVal );
					return returnVal;

				} else if ( key instanceof MemorySection returnVal ) {
					getConfigContent( returnVal.getCurrentPath(), returnVal );

				} else {
					return "Key " + key + " not valid. Check the configuration for invalid values.";

				}

			}
			return "Key not found";

		}

		return "";

//		for ( Object value : search) {
//			// WikiCraft.getInstance().getLogger().info( value.toString() );
//			if ( value instanceof String ) {
//				WikiCraft.getInstance().getLogger().info( value.toString() );
//
//			} else if ( value instanceof MemorySection ) {
//				getConfigContent(search, value );
//
//			}
//
//		}

//		if ( findKey == null || findKey.isEmpty() ) {
//			return configFile.saveToString();
//
//		}
//
//		for ( String key : keys ) {
//			if ( key.equals( findKey ) ) {
//				return Component.text( "Key " + key + " has value " +  "value" ).content();
//
//			}
//
//			WikiCraft.getInstance().getLogger().info( key );
//
//		}
//
//		return "Key " + findKey + " not found.";

	}

}

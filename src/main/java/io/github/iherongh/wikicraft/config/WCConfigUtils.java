package io.github.iherongh.wikicraft.config;

import com.google.gson.GsonBuilder;
import io.github.iherongh.wikicraft.WikiCraft;
import io.github.iherongh.wikicraft.messages.WCMessages;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * A class containing utility methods for the config.yml file.
 */
public class WCConfigUtils {

    /**
     * The config.yml file.
     */
    private static FileConfiguration configFile = WikiCraft.getInstance().getConfig();

    /**
     * Gets the config file.
     *
     * @return The config file.
     *
     * @since 0.1.0
     */
    public static FileConfiguration getConfig() {
        return configFile;

    }

    /**
     * Reloads the config file.
     */
    public static void reloadConfig() {
        try {
            configFile = WikiCraft.getInstance().getConfig();
            WikiCraft.getInstance().reloadConfig();
            WikiCraft.getInstance().saveConfig();
            WCMessages.debug( "info", "Contents of the config has been refreshed." );

        } catch ( Exception e ) {
            WCMessages.debug( "error", "Failed to reload config: " + e.getMessage() );

        }

    }

    /**
     * Gets the configured wiki URL.
     *
     * @return The configured wiki URL.
     *
     * @since 0.1.0
     */
    public static String getWikiURL() {
        if ( !( configFile.get( "wiki-url" ) instanceof String ) ) {
            // Set default wiki-url if value is not a string
            WCMessages.debug( "error", "Invalid wiki-url in config.yml; setting to default (example.com)." );

            configFile.set( "wiki-url", "example.com" );
            WikiCraft.getInstance().saveConfig();

        }

        return configFile.getString( "wiki-url" );

    }

    /**
     * Gets the configured wiki bot username.
     *
     * @return The configured wiki bot username.
     *
     * @since 0.1.0
     */
    public static @Nullable String getWikiBotUsername() {
        if ( !( configFile.get( "wiki-bot-username" ) instanceof String ) || configFile.get( "wiki-bot-username" ) == null || configFile.getString( "wiki-bot-username" ).isEmpty() ) {
            WCMessages.debug( "error", "WikiCraft bot username not set; setting to default (WikiCraftBot)." );
            configFile.set( "wiki-bot-username", "WikiCraftBot" );
            WikiCraft.getInstance().saveConfig();

        }
        return configFile.getString( "wiki-bot-username" );

    }

    /**
     * Gets the configured wiki bot password.
     *
     * @return The configured wiki bot password.
     *
     * @since 0.1.0
     */
    public static @Nullable String getWikiBotPassword() {
        if ( !( configFile.get( "wiki-bot-password" ) instanceof String ) || configFile.get( "wiki-bot-password" ) == null || configFile.getString( "wiki-bot-password" ).isEmpty() ) {
            WCMessages.debug( "error", "WikiCraft bot password not set." );
            return null;

        }

        return configFile.getString( "wiki-bot-password" );

    }

    /**
     * Get the maximum number of results to return from the wiki.
     *
     * @return The maximum number of results to return from the wiki.
     *
     * @since 0.1.0
     */
    public static int getMaxResults() {
        if ( !( configFile.get( "max-results" ) instanceof Integer ) ) {
            configFile.set( "max-results", 10 );
            WikiCraft.getInstance().saveConfig();

        } else {
            // Cap results to 5 or 100
            if ( configFile.getInt( "max-results" ) < 5 ) {
                WCMessages.debug( "error", "Invalid max-results in config.yml; setting to nearest limit (5)." );
                configFile.set( "max-results", 5 );
                WikiCraft.getInstance().saveConfig();

            } else if ( configFile.getInt( "max-results" ) > 100 ) {
                WCMessages.debug( "error", "Invalid max-results in config.yml; setting to nearest limit (100)." );
                configFile.set( "max-results", 100 );
                WikiCraft.getInstance().saveConfig();

            }

        }

        // Return maxResults
        WCMessages.debug( "info", "Max results currently set to " + configFile.getInt( "max-results" ) + "." );
        return configFile.getInt( "max-results" );

    }

    /**
     * Get minimum duration between requests.
     *
     * @return Minimum duration between requests.
     *
     * @since 0.1.0
     */
    public static int getMinRequestTime() {
        // Set default if not set
        if ( !( configFile.get( "time-between-actions" ) instanceof Integer ) ) {
            WCMessages.debug( "error", "Invalid time-between-actions in config.yml; setting to default (20)." );
            configFile.set( "time-between-actions", 20 );
            WikiCraft.getInstance().reloadConfig();
            WikiCraft.getInstance().saveConfig();
            
        } else {
            if ( configFile.getInt( "time-between-actions" ) < 20 ) {
                // Cap result to 20
                WCMessages.debug( "error", "Invalid time-between-actions in config.yml; setting to nearest limit (20)." );
                configFile.set( "time-between-actions", 20 );
                WikiCraft.getInstance().reloadConfig();
                WikiCraft.getInstance().saveConfig();
                
            }
            
        }

        return configFile.getInt( "time-between-actions" );

    }
    
    /**
     * Get the locale to be used for player messages.
     * @return THe locale to be used for player messages.
     *
     * @since 0.1.0
     */
    public static String getLocale() {
        if ( !( configFile.get( "locale" ) instanceof String ) ) {
            WCMessages.debug("error", "Invalid locale in config.yml; setting to default (en-us).");
            configFile.set( "locale", "en-us" );
            WikiCraft.getInstance().saveConfig();
            
        } else {
            String locale = configFile.getString("locale", "en-us").toLowerCase();
            String pattern = "^[a-z]{2,4}([-_][a-z]{2,4})?$\n";

            // Default value if it is invalid (en-us)
            if ( !locale.matches( pattern ) ) {
                WCMessages.debug("error", "Invalid locale in config.yml; setting to default (en-us).");
                configFile.set( "locale", "en-us" );
                WikiCraft.getInstance().saveConfig();
                
            }
            
        }

        WCMessages.debug( "info", "Locale currently set to \"" + configFile.getString( "locale" ) + "\"." );
        return configFile.getString( "locale" );

    }

    public static String formatConfigValue( Object value ) {
        if ( value instanceof Map || value instanceof List ) {
            return new GsonBuilder().setPrettyPrinting().create().toJson( value );

        } else if ( value instanceof Number || value instanceof Boolean ) {
            return value.toString();

        } else if ( value == null ) {
            return "null";

        } else {
            return "\"" + value + "\"";

        }

    }
}

package io.github.iherongh.wikicraft;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import io.github.iherongh.wikicraft.account.WCAccountBridge;
import io.github.iherongh.wikicraft.commands.WCCommandWiki;
import io.github.iherongh.wikicraft.messages.WCMessages;
import io.github.iherongh.wikicraft.wiki.WCWiki;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The main class for WikiCraft.
 */
public class WikiCraft extends JavaPlugin {

    /**
     * Defines the primary colour constants for WikiCraft.
     */
    public static final RGBLike PRIMARY = TextColor.color( 85, 191, 173 );

    /**
     * Defines the secondary colour constants for WikiCraft.
     */
    public static final RGBLike SECONDARY = TextColor.color( 51, 141, 143 );

    /**
     * Defines the tertiary colour constants for WikiCraft.
     */
    public static final RGBLike TERTIARY = TextColor.color( 27, 68, 66 );

    /**
     * Defines the info text colour constants for WikiCraft.
     */
    public static final RGBLike TEXT_INFO = TextColor.color( 199, 227, 216 );

    /**
     * Defines the error text colour constants for WikiCraft.
     */
    public static final RGBLike TEXT_ERROR = TextColor.color( 82, 53, 234 );

    /**
     * Defines the full prefix component for WikiCraft messages.
     */
    public static TextComponent PREFIX = Component.text( "[" ).color( TextColor.color( TERTIARY ) )
        .append( Component.text( "Wiki" ).color( TextColor.color( PRIMARY ) ) )
        .append( Component.text( "Craft" ).color( TextColor.color( SECONDARY ) ) )
        .append( Component.text( "] " ).color( TextColor.color( TERTIARY ) ) );

    /**
     * Defines the short prefix component for WikiCraft messages.
     */
    public static TextComponent PREFIX_SHORT = Component.text( "[" ).color( TextColor.color( TERTIARY ) )
        .append( Component.text( "W" ).color( TextColor.color( PRIMARY ) ) )
        .append( Component.text( "C" ).color( TextColor.color( SECONDARY ) ) )
        .append( Component.text( "] " ).color( TextColor.color( TERTIARY ) ) );

    /**
     * The singleton instance of the WikiCraft plugin.
     */
    private static WikiCraft instance;

    /**
     * The constructor for the WikiCraft plugin.
     */
    public WikiCraft() {
        instance = this;

    }
    
    /**
     * Retrieves the singleton instance of the WikiCraft plugin.
     * This method provides global access to the plugin instance throughout the application.
     * 
     * @return The single instance of the WikiCraft plugin.
     *
     * @since 0.1.0
     */
    public static WikiCraft getInstance() {
        return instance;
        
    }

    /**
     * Retrieves the version of the WikiCraft plugin.
     *
     * @return The version of the WikiCraft plugin.
     */
    public static @NotNull String getVersion() {
        return getInstance().getPluginMeta().getVersion();

    }

    /**
     * Disables the WikiCraft plugin and performs necessary cleanup operations.
     * <br><br>
     * Attempts to unregister the 'wiki' command, disable the CommandAPI,
     * and then disable the WikiCraft plugin itself.
     */
    public static void disableWikiCraft() {
        Bukkit.getPluginManager().disablePlugin( getInstance() );

    }

    /**
     * Handles the disabling of WikiCraft.
     * This method is called when the plugin is being disabled, typically during server shutdown
     * or when the plugin is manually disabled, logging a message indicating a successful disable.
     */
    @Override
    public void onDisable() {
        try {
            WCMessages.debug( "info", "Disabling WikiCraft..." );

            // Unregister the 'wiki' command
            CommandAPI.unregister( "wiki", true );

            // Disable CommandAPI
            CommandAPI.onDisable();

        } catch ( Exception e ) {
            WCMessages.debug( "severe", "Unable to disable WikiCraft: " + e.getMessage() );

        }

        // Declare successful disable
        WCMessages.debug( "info", "WikiCraft successfully disabled." );

    }

    /**
     * Handles the loading of WikiCraft.
     */
    @Override
    public void onLoad() {
        try {
            // Initialize CommandAPI
            CommandAPI.onLoad( new CommandAPIPaperConfig( this ).silentLogs( true ) );

        } catch ( Exception e ) {
            WCMessages.throwError( e );
            throw new RuntimeException( e );

        }

    }

    @Override
    public void onEnable() {
        try {
            instance = this;

            // Initialize CommandAPI
            CommandAPI.onEnable();

            // Load config.yml file
            loadConfig();

            // Build wiki on initialisation
            WCWiki.buildWiki();

            // Register commands
            registerCommand();

            // Load account_bridge.json file
            loadAccountBridge();

            // Declare successful enable
            WCMessages.debug( "info", "WikiCraft successfully enabled." );

        } catch ( Exception e ) {
            WCMessages.debug( "severe", "A fatal error has occurred and WikiCraft was unable to start: " + e.getMessage() );
            disableWikiCraft();

        }

    }

    private void loadConfig() {
        WCMessages.debug( "info", "Attempting to load configurations..." );

        try {
            // Save default config if it doesn't exist
            saveDefaultConfig();

            // Reload the config
            reloadConfig();

        } catch ( Exception e ) {
            throw new RuntimeException( e );

        }

        WCMessages.debug( "info", "Configurations successfully loaded." );

    }

    private void loadAccountBridge() {
        WCMessages.debug( "info", "Attempting to load account bridge..." );

        try {
            WCAccountBridge.instantiateAccountFile();
            WCMessages.debug( "info", "Account bridge file successfully loaded." );

            WCAccountBridge.loadAccountLinks();
            WCMessages.debug( "info", WCAccountBridge.getUUIDToWikiUserMap().size() + " link(s) from account bridge successfully loaded." );

        } catch ( Exception e ) {
            WCMessages.debug( "warning", "Unable to generate account bridge file: " + e.getMessage() );

        }

    }

    private void registerCommand() {
        WCMessages.debug( "info", "Registering commands..." );

        try {
            new WCCommandWiki().getCommand().register();

        } catch ( Exception e ) {
            WCMessages.debug( "warning", "/wiki was unable to register: " + e.getMessage() );

        }
        WCMessages.debug( "info", "Command registration process completed." );

    }

}

package io.github.iherongh.wikicraft;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.github.iherongh.wikicraft.account.WCAccountBridge;
import io.github.iherongh.wikicraft.commands.WCCommandWiki;
import io.github.iherongh.wikicraft.config.WCConfigDefault;
import io.github.iherongh.wikicraft.messages.WCMessages;
import io.github.iherongh.wikicraft.wiki.WCWiki;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class WikiCraft extends JavaPlugin {

    /**
     * Defines the color scheme constants for WikiCraft.
     * These colors are used throughout the plugin for consistent visual styling.
     */
    public static final RGBLike PRIMARY = TextColor.color( 85, 191, 173 );
    public static final RGBLike SECONDARY = TextColor.color( 51, 141, 143 );
    public static final RGBLike TERTIARY = TextColor.color( 27, 68, 66 );
    public static final RGBLike TEXT_INFO = TextColor.color( 199, 227, 216 );
    public static final RGBLike TEXT_ERROR = TextColor.color( 82, 53, 234 );

    public static TextComponent PREFIX = Component.text( "[" ).color( TextColor.color( TERTIARY ) )
        .append( Component.text( "Wiki" ).color( TextColor.color( PRIMARY ) ) )
        .append( Component.text( "Craft" ).color( TextColor.color( SECONDARY ) ) )
        .append( Component.text( "] " ).color( TextColor.color( TERTIARY ) ) );
    public static TextComponent PREFIX_SHORT = Component.text( "[" ).color( TextColor.color( TERTIARY ) )
        .append( Component.text( "W" ).color( TextColor.color( PRIMARY ) ) )
        .append( Component.text( "C" ).color( TextColor.color( SECONDARY ) ) )
        .append( Component.text( "] " ).color( TextColor.color( TERTIARY ) ) );

    private static WikiCraft instance;

    public WikiCraft() {
        instance = this;

    }
    
    /**
     * Retrieves the singleton instance of the WikiCraft plugin.
     * This method provides global access to the plugin instance throughout the application.
     * <br><br>
     * @return The single instance of the WikiCraft plugin.
     */
    public static WikiCraft getInstance() {
        return instance;
        
    }

    /**
     * Disables the WikiCraft plugin and performs necessary cleanup operations.
     * <br><br>
     * Attempts to unregister the 'wiki' command, disable the CommandAPI,
     * and then disable the WikiCraft plugin itself.
     */
    public static void disableWikiCraft() {
        try {
            WCMessages.debug( "info", "Disabling WikiCraft..." );
            CommandAPI.unregister( "wiki" );
            CommandAPI.onDisable();
            Bukkit.getPluginManager().disablePlugin( getInstance() );

        } catch ( Exception e ) {
            WCMessages.debug( "severe", "Unable to disable WikiCraft: " + e.getMessage() );

        }

    }

    /**
     * Handles the disabling of WikiCraft.
     * This method is called when the plugin is being disabled, typically during server shutdown
     * or when the plugin is manually disabled, logging a message indicating a successful disable.
     */
    @Override
    public void onDisable() {
        // Declare successful disable
        WCMessages.debug( "info", "WikiCraft successfully disabled." );

    }

    @Override
    public void onEnable() {
        try {
            instance = this;

            // Load config.yml file
            loadConfig();

            // Load account_bridge.txt file
            loadAccountBridge();

            // Initialize CommandAPI
            CommandAPI.onLoad( new CommandAPIBukkitConfig( this ) );
            CommandAPI.onEnable();

            // Register commands
            registerCommand();

            // Build wiki on initialisation
            WCWiki.buildWiki();

            // Declare successful enable
            WCMessages.debug( "info", "WikiCraft successfully enabled." );

        } catch ( Exception e ) {
            WCMessages.debug( "severe", "A fatal error has occurred and WikiCraft was unable to start: " + e.getMessage() );
            disableWikiCraft();
            throw new RuntimeException( e );

        }

    }

    private void loadConfig() {
        WCMessages.debug( "info", "Attempting to load configurations..." );

        try {
            // Populate and instantiate default values
            WCConfigDefault.instantiateWikiCraftConfig();

            // Reload the getKeys
            this.reloadConfig();

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
        WCMessages.debug( "info", "Commands registered successfully." );

    }

}

package io.github.iherongh.wikicraft.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.TextArgument;
import io.github.iherongh.wikicraft.WikiCraft;
import io.github.iherongh.wikicraft.arguments.WCArguments;
import io.github.iherongh.wikicraft.config.WCConfigUtils;
import io.github.iherongh.wikicraft.messages.WCMessages;
import io.github.iherongh.wikicraft.utils.WCUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

/**
 * The {@code /wiki config} command.
 *
 * @author iHeronGH
 * @version 0.1.0
 * @since 0.1.0
 *
 * @see WCCommandWikiConfig#getCommand()
 * @see WCCommandWikiConfig#getSubcommand()
 * @see WCCommandWikiConfig#reloadSubcommand()
 * @see WCCommandWikiConfig#setSubcommand()
 */
public class WCCommandWikiConfig {

    /**
     * Creates the {@code /wiki config <get|reload|set>} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.command.config}
     * <p><b>Usage:</b> {@code /wiki config <get|reload|set>}
     * <ul>
     *     <li>{@code /wiki config get [key]}: Gets a config value
     *     <li>{@code /wiki config reload}: Reloads the config.yml file
     *     <li>{@code /wiki config set <key> [value]}: Sets a config value
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki config} subcommand
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiConfig#getSubcommand()
     * @see WCCommandWikiConfig#reloadSubcommand()
     * @see WCCommandWikiConfig#setSubcommand()
     */
    public static CommandAPICommand getCommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki config <get|reload|set>" );

        // /wiki config <get|reload|set>
        return new CommandAPICommand( "config" ).withSubcommands(

            // /wiki config get
            getSubcommand(),

            // /wiki config reload
            reloadSubcommand(),

            // /wiki config set
            setSubcommand()

        );

    }

    /**
     * Creates the {@code /wiki config get [key]} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.command.config.get}
     * <p><b>Usage:</b> {@code /wiki config get [key]}
     * <ul>
     *     <li>{@code /wiki config get}: Shows all config entries
     *     <li>{@code /wiki config get <key>}: Gets a config value
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki config get} subcommand
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiConfig#getCommand()
     */
    public static CommandAPICommand getSubcommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki config get [key]" );

        // /wiki config get [key]
        return new CommandAPICommand( "get" )
            .withPermission( "wikicraft.command.config.get" )
            .withOptionalArguments( WCArguments.configKeyArgument() )
            .executes( ( sender, args ) -> {
                FileConfiguration config = WCConfigUtils.getConfig();

                String userKey = (String) args.get( "key" );

                if ( userKey == null ) {
                    // Show all config entries
                    for ( String configKey : config.getKeys( true ) ) {
                        sender.sendMessage( WCMessages.message( "info", configKey + ": " + config.get( configKey ) ) );

                    }
                } else {
                    if ( config.contains( userKey ) ) {
                        Object value = config.get( userKey );
                        sender.sendMessage( WCMessages.message( "info", userKey + ": " + WCConfigUtils.formatConfigValue( value ) ) );

                    } else {
                        boolean found = false;

                        for ( String configKey : config.getKeys( true ) ) {
                            if ( configKey.contains( userKey ) ) {
                                found = true;
                                sender.sendMessage( WCMessages.message( "info", configKey + ": " + WCConfigUtils.formatConfigValue( config.get( configKey ) ) ) );

                            }

                        }
                        if ( !found ) {
                            sender.sendMessage( WCMessages.message( "error", "No matching config keys found for: " + userKey ) );

                        }

                    }
                }
            });

    }

    /**
     * Creates the {@code /wiki config reload} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.command.config.reload}
     * <p><b>Usage:</b> {@code /wiki config reload}
     * <ul>
     *     <li>{@code /wiki config reload}: Reloads the config.yml file
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki config reload} subcommand
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiConfig#getCommand()
     */
    private static CommandAPICommand reloadSubcommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki config reload" );

        // /wiki config reload
        return new CommandAPICommand( "reload" )
            .withPermission( "wikicraft.command.config.reload" )
            .executes( ( sender, args ) -> {
                WikiCraft.getInstance().reloadConfig();
                WikiCraft.getInstance().saveConfig();
                sender.sendMessage( WCMessages.message( "info", "Configuration reloaded successfully." ) );

            } );
    }

    /**
     * Creates the {@code /wiki config set <key> [value]} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.command.config.set}
     * <p><b>Usage:</b> {@code /wiki config set <key> [value]}
     * <ul>
     *     <li>{@code /wiki config set <key>}: Deletes a config value
     *     <li>{@code /wiki config set <key> <value>}: Sets a config value
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki config set} subcommand
     *
     * @since 0.1.0
     * @see WCCommandWikiConfig#getCommand()
     */
    private static CommandAPICommand setSubcommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki config set <key> [value]" );

        // /wiki config set <key> [value]
        return new CommandAPICommand( "set" )
            .withPermission( "wikicraft.command.config.set" )
            .withArguments( WCArguments.configKeyArgument() )
            .withOptionalArguments( new TextArgument("value" ) )
            .executes( ( sender, args ) -> {
                String key = (String) args.get( "key" );
                String value = (String) args.get( "value" );

                try {
                    if ( key == null ) {
                        sender.sendMessage( WCMessages.message( "error", "Key cannot be null." ) );

                    } else {
                        if ( key.equals( "wiki-url" ) ) {
                            if ( WCConfigUtils.getConfig().getString( "wiki-url" ) != null ) {
                                // Get current URL
                                String currentUrl = WCConfigUtils.getConfig().getString( "wiki-url" );

                                // Set URL to new value
                                WCConfigUtils.getConfig().set( key, value );

                                // Get config file
                                File configFile = new File( WikiCraft.getInstance().getDataFolder(), "config.yml" );

                                // Update URL instances
                                String content = new String( Files.readAllBytes( configFile.toPath() ) ).replace( currentUrl, value );

                                // Write the updated content back to the file
                                Files.write( configFile.toPath(), content.getBytes() );

                                // Reload config
                                WikiCraft.getInstance().reloadConfig();

                                sender.sendMessage( WCMessages.message( "info", "Wiki URL updated successfully." ) );
                            } else {
                                sender.sendMessage( WCMessages.message( "error", "Wiki URL not found in config.yml." ) );
                                WCMessages.debug( "warning", "Wiki URL not found in config.yml." );

                            }

                        }
                        // Check if value is JSON
                        if ( value != null && value.startsWith( "{" ) ) {
                            // Handle as JSON object
                            JsonObject jsonObject = JsonParser.parseString( value ).getAsJsonObject();
                            Map<String, Object> configMap = WCUtils.convertJsonToMap(jsonObject);
                            WCConfigUtils.getConfig().set( key, configMap );

                        } else {
                            // Handle as regular string value
                            WCConfigUtils.getConfig().set( key, value );

                        }

                        // Save the config
                        WikiCraft.getInstance().saveConfig();
                        sender.sendMessage( WCMessages.message( "info", "Config value updated successfully!" ) );

                    }

                } catch ( Exception e ) {
                    WCMessages.throwError( e, sender );

                }
            });
    }

}

package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPICommand;
import io.github.iherongh.wikicraft.config.WCConfigUtils;
import io.github.iherongh.wikicraft.messages.WCMessages;
import io.github.iherongh.wikicraft.wiki.WCWiki;

/**
 * The {@code /wiki reload} command.
 *
 * @author iHeronGH
 *
 * @version 0.1.0
 *
 * @since 0.1.0
 *
 * @see WCCommandWikiPages#getCommand()
 */
public class WCCommandWikiReload {

    /**
     * Constructs a new {@code WCCommandWikiReload} object.
     */
    public WCCommandWikiReload() {}

    /**
     * The last time the wiki was reloaded.
     */
    private static long lastReload = 0;

    /**
     * The cooldown for regenerating the wiki.
     */
    private static final long RELOAD_COOLDOWN = 20000;

    /**
     * Creates the {@code /wiki reload} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.command.reload}
     * <p><b>Usage:</b> {@code /wiki reload}
     * <ul>
     *     <li>{@code /wiki reload}: Reloads the WikiCraft plugin configuration
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki reload} subcommand
     *
     * @since 0.1.0
     *
     * @see WCCommandWiki#getCommand()
     */
    public static CommandAPICommand getCommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki reload" );

        // /wiki reload
        return new CommandAPICommand( "reload" )
            .withPermission( "wikicraft.command.reload" )
            .executes( ( sender, args ) -> {
                try {
                    // Get the current time
                    long now = System.currentTimeMillis();

                    if ( now - lastReload < RELOAD_COOLDOWN ) {
                        sender.sendMessage( WCMessages.message( "error", "Please wait " + ( ( RELOAD_COOLDOWN - ( now - lastReload ) ) / 1000 ) + " seconds before reloading." ) );
                        return;

                    }

                    sender.sendMessage( WCMessages.message( "info", "Reloading WikiCraft..." ) );
                    WCConfigUtils.reloadConfig();
                    WCWiki.buildWiki();

                    // Reset the last reload time
                    lastReload = now;

                } catch ( Exception e ) {
                    WCMessages.throwError( e, sender );

                }


            } );

    }

}

package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPICommand;
import io.github.iherongh.wikicraft.WikiCraft;
import io.github.iherongh.wikicraft.messages.WCMessages;
import org.bukkit.entity.Player;

/**
 * The {@code /wiki disable} subcommand.
 *
 * @author iHeronGH
 * @version 0.1.0
 * @since 0.1.0
 *
 * @see WCCommandWikiDisable#getCommand()
 */
public class WCCommandWikiDisable {

    /**
     * Constructs a new {@code WCCommandWikiDisable} object.
     */
    public WCCommandWikiDisable() {}

    /**
     * Creates the {@code /wiki disable} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.command.disable}
     * <p><b>Usage:</b> {@code /wiki disable}
     * <ul>
     *     <li>{@code /wiki disable}: Safely shuts down the WikiCraft plugin
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki disable} subcommand
     *
     * @since 0.1.0
     *
     * @see WCCommandWiki#getCommand()
     */
    public static CommandAPICommand getCommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki disable" );

        // /wiki disable
        return new CommandAPICommand( "disable" )
            .withPermission( "wikicraft.command.disable" )
            .executes( ( sender, args ) -> {
                try {
                    if ( sender != null ) {
                        sender.sendMessage( WCMessages.message( "info", "Disabling WikiCraft..." ) );
                    }

                    WikiCraft.disableWikiCraft();

                    if ( sender != null ) {
                        sender.sendMessage( WCMessages.message( "info", "WikiCraft has been disabled." ) );

                    }
                } catch ( Exception e ) {
                    if ( sender instanceof Player player ) {
                        WCMessages.throwError( e, player );

                    } else {
                        WCMessages.throwError( e );

                    }

                }

            } );

    }

}

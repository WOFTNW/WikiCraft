package io.github.iherongh.wikicraft.commands;

import org.jetbrains.annotations.NotNull;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.ExecutableCommand;
import io.github.iherongh.wikicraft.messages.WCMessages;
import io.github.iherongh.wikicraft.wiki.WCWiki;

public class WCCommandWiki {

    /**
     * Constructs and returns the main 'wiki' command with its subcommands.
     *
     * @return An {@link ExecutableCommand} object representing the 'wiki' command
     *
     * @since 0.1.0
     */
    public @NotNull ExecutableCommand<?, ?> getCommand() {
        if ( WCWiki.getWiki() == null ) {
            WCMessages.debug( "warning", "No wiki exists; /wiki will be loaded in a severely limited state." );
            return limitedCommand();

        }

        return fullCommand();

    }
    
    /**
     * This method creates the primary 'wiki' command structure, including various
     * subcommands for account management, configuration, disabling, reloading,
     * searching, and page operations.
     * <p>The command structure is as follows:
     * <ul>
     *     <li>{@code /wiki account <get|list|login|refresh>} (Requires {@code wikicraft.command.account})
     *     <li>{@code /wiki config <get|reload|set>} (Requires {@code wikicraft.command.config})
     *     <li>{@code /wiki disable} (Requires {@code wikicraft.command.disable})
     *     <li>{@code /wiki help} (Requires {@code wikicraft.command.help})
     *     <li>{@code /wiki reload} (Requires {@code wikicraft.command.reload})
     *     <li>{@code /wiki pages <add|delete|edit|info|read|search>} (Requires {@code wikicraft.command.pages})
     * </ul>
     * @return An {@link ExecutableCommand} representing the complete /wiki command
     *
     * @since 0.1.0
     */
    private @NotNull ExecutableCommand<?,?> fullCommand() {
        // Log command load
        WCMessages.debug( "info", "Creating command: /wiki <account|config|disable|help|reload|pages>" );

        // /wiki <account|config|disable|help|reload|pages>
        return new CommandAPICommand( "wiki" )
            .withSubcommands(

                // /wiki account <get|list|login|refresh>
                WCCommandWikiAccount.getCommand(),

                // /wiki config <get|reload|set>
                WCCommandWikiConfig.getCommand(),

                // /wiki disable
                WCCommandWikiDisable.getCommand(),
                
                // /wiki help
                WCCommandWikiHelp.getCommand(),

                // /wiki reload
                WCCommandWikiReload.getCommand(),

                // /wiki pages <add|delete|edit|info|search>
                WCCommandWikiPages.getCommand()

            )
            .withUsage( "/wiki <account|config|disable|reload|pages>" )
            .withShortDescription( "Edit your WikiCraft experience." )
            .withFullDescription(
                """
                    Access and manage your MediaWiki through Minecraft.
                    Available commands:
                    - /wiki account: Manage wiki accounts, login, and view linked users.
                    - /wiki config: View and modify WikiCraft configuration.
                    - /wiki disable: Safely shut down the WikiCraft plugin.
                    - /wiki help: Show this message.
                    - /wiki reload: Reload the WikiCraft plugin configuration.
                    - /wiki pages: Create, edit, delete, search and view wiki pages.
                """
            )
            .executesPlayer( (player, args) -> {
                try {
                    player.sendMessage(WCCommandWikiHelp.createFullHelpMessage());

                } catch ( Exception e ) {
                    WCMessages.throwError( e );

                }
            } );

    }
    
    /**
     * This method creates the secondary 'wiki' command structure, only including
     * subcommands for plugin configuration, disabling, and reloading.
     * <p>The command structure is as follows:
     * <ul>
     *     <li>{@code /wiki config <get|reload|set>}
     *     <li>{@code /wiki disable}
     *     <li>{@code /wiki reload}
     * </ul>
     * @return An {@link ExecutableCommand} representing the limited /wiki command
     *
     * @since 0.1.0
     */
    private @NotNull ExecutableCommand<?,?> limitedCommand() {
        // Log command load
        WCMessages.debug( "info", "Creating command: /wiki <config|disable|help|reload>" );

        // /wiki <config|disable|reload>
        return new CommandAPICommand( "wiki" )
            .withSubcommands(

                // /wiki config <get|reload|set>
                WCCommandWikiConfig.getCommand(),

                // /wiki disable
                WCCommandWikiDisable.getCommand(),

                // /wiki help
                WCCommandWikiHelp.getCommand(),

                // /wiki reload
                WCCommandWikiReload.getCommand()

            )
            .withUsage( "/wiki <config|disable|help|reload>" )
            .withShortDescription( "Edit your WikiCraft experience." )
            .withFullDescription(
                """
                    Access and manage your MediaWiki through Minecraft.
                    Available commands:
                    - /wiki config: View and modify WikiCraft configuration.
                    - /wiki disable: Safely shut down the WikiCraft plugin.
                    - /wiki help: Show this message.
                    - /wiki reload: Reload the WikiCraft plugin configuration.
                """
            )
            .executesPlayer( (player, args) -> {
                try {
                    player.sendMessage(WCCommandWikiHelp.createLimitedHelpMessage());

                } catch ( Exception e ) {
                    WCMessages.throwError( e );

                }
            } );

    }

}

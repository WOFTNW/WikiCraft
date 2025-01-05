package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.fastily.jwiki.core.WParser;
import io.github.fastily.jwiki.core.Wiki;
import io.github.iherongh.wikicraft.account.WCAccountBridge;
import io.github.iherongh.wikicraft.arguments.WCArguments;
import io.github.iherongh.wikicraft.config.WCConfigUtils;
import io.github.iherongh.wikicraft.messages.WCMessages;
import io.github.iherongh.wikicraft.utils.WCUtils;
import io.github.iherongh.wikicraft.wiki.WCWiki;
import io.github.iherongh.wikicraft.wiki.WCWikiUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * The {@code /wiki pages} command.
 *
 * @author iHeronGH
 * @version 0.1.0
 * @since 0.1.0
 *
 * @see WCCommandWikiPages#getCommand()
 * @see WCCommandWikiPages#addSubcommand()
 * @see WCCommandWikiPages#deleteSubcommand()
 * @see WCCommandWikiPages#editSubcommand()
 * @see WCCommandWikiPages#infoSubcommand()
 * @see WCCommandWikiPages#readSubcommand()
 * @see WCCommandWikiPages#searchSubcommand()
 *
 * @TODO <ul>
 *          <li>Make /wiki pages edit work... somehow
 *       </ul>
 */
public class WCCommandWikiPages {

    /**
     * Creates the {@code /wiki pages <add|delete|edit|info|read|search>} command.
     * <p><b>Permission:</b> {@code wikicraft.command.pages}
     * <p><b>Usage:</b> {@code /wiki pages <add|delete|edit|info|read|search>}
     * <ul>
     *     <li>{@code /wiki pages add <page> <content>}: Adds a page with the given content
     *     <li>{@code /wiki pages delete <page>}: Deletes a page
     *     <li>{@code /wiki pages edit <page> <content>}: Edits a page with the given content
     *     <li>{@code /wiki pages info <page>}: Gets information about a page
     *     <li>{@code /wiki pages read <page>}: Displays text on a page
     *     <li>{@code /wiki pages search <query>}: Searches for pages
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki pages} command
     *
     * @since 0.1.0
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiPages#addSubcommand()
     * @see WCCommandWikiPages#deleteSubcommand()
     * @see WCCommandWikiPages#editSubcommand()
     * @see WCCommandWikiPages#infoSubcommand()
     * @see WCCommandWikiPages#readSubcommand()
     * @see WCCommandWikiPages#searchSubcommand()
     */
    public static @Nullable CommandAPICommand getCommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki pages <add|delete|edit|info|search>" );

        // Check if a wiki exists
        if ( WCWiki.getWiki() == null ) {
            WCMessages.debug( "warning", "No wiki exists; cancelling /wiki pages <add|delete|edit|info|search>." );
            return null;

        }

        // /wiki pages <add|delete|edit|info|search>
        return new CommandAPICommand( "pages" ).withSubcommands(
            
            // /wiki pages add
            addSubcommand(),

            // /wiki pages delete
            deleteSubcommand(),

            // /wiki pages edit
//            editSubcommand(),

            // /wiki pages info
            infoSubcommand(),

            // /wiki pages info
            readSubcommand(),

            // /wiki pages search
            searchSubcommand()

        );
    
    }

    /**
     * Creates the {@code /wiki pages add <page> <content>} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.pages.manage}
     * <p><b>Usage:</b> {@code /wiki pages add <page> <content>}
     * <ul>
     *     <li>{@code /wiki pages add <page> <content>}: Adds a new wiki page with the specified content
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki pages add} subcommand
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiPages#getCommand()
     * @see WCCommandWikiPages#handleAddRequest(Player, String, String, long) 
     * @see WCCommandWikiPages#createNewPage(Player, String, String) 
     */
    private static CommandAPICommand addSubcommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki pages add <page> <content>" );

        // /wiki pages add <page> <content>
        return new CommandAPICommand( "add" )
            .withPermission( "wikicraft.pages.manage" )
            .withArguments( new StringArgument( "page" ), new GreedyStringArgument( "content" ) )
            .executesPlayer( ( player, args ) -> {
                try {
                    long now = System.currentTimeMillis();
                    long lastRequestTime = WCUtils.getLastRequestTimeMap().getOrDefault( player.getUniqueId(), 0L );
                    long cooldown = ( WCConfigUtils.getMinRequestTime() - ( now - lastRequestTime ) ) / 1000;

                    // Get arguments
                    String page = args.getRaw( "page" );
                    String content = args.getRaw( "content" );

                    // Handle add request
                    handleAddRequest( player, page, content, cooldown );

                } catch ( Exception e ) {
                    WCMessages.throwError( e, player );
                    
                }

            } );
    }

    /**
     * Handles the {@code /wiki pages add <page> <content>} subcommand.
     * 
     * @param player The player who executed the command
     * @param page The page to add
     * @param content The content of the page
     * @param cooldown The cooldown time in seconds
     * 
     * @since 0.1.0
     *
     * @see WCCommandWikiPages#getCommand()
     * @see WCCommandWikiPages#addSubcommand()
     * @see WCCommandWikiPages#createNewPage(Player, String, String) 
     */
    private static void handleAddRequest( @NotNull Player player, String page, String content, long cooldown ) {
        // Announce player's attempt to add page
        WCMessages.debug( "info", player.getName() + " is attempting to add page \"" + page + "\"..." );
        WCMessages.debug( "info", "Page content: \n" + content );

        // Check if player is on cooldown
        if ( WCUtils.isOnRequestCooldown( player ) ) {
            WCMessages.debug( "warning", player.getName() + " attempted to add page " + page + ", but is on cooldown for " + cooldown + " seconds." );
            player.sendMessage( WCMessages.message( "error", "Please wait " + cooldown + " seconds before reloading." ) );
            return;

        }

        // Check if page already exists
        if ( WCWiki.getWiki().exists( page ) ) {
            WCMessages.debug( "warning", "Page " + page + " already exists, aborting add request." );
            player.sendMessage( WCMessages.message( "error", "Page " + page + " already exists. Use '/wiki edit' to apply these changes." ) );
            return;

        }

        // Create new page
        createNewPage( player, page, content );
        
    }

    /**
     * Creates a new page with the specified content.
     * @param player The player who is creating the page
     * @param page The name of the page
     * @param content The content of the page
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiPages#getCommand()
     * @see WCCommandWikiPages#addSubcommand()
     * @see WCCommandWikiPages#handleAddRequest(Player, String, String, long)
     */
    private static void createNewPage( @NotNull Player player, String page, String content ) {
        // Announce page creation
        WCMessages.debug( "info", "Creating page via WikiCraft bot account " + WCConfigUtils.getWikiBotUsername() + "..." );

        // Generate page content
        boolean success = WCWiki.getWiki().edit( page, content, "Content generated from WikiCraft by " + WCAccountBridge.getWikiUser( player.getUniqueId() ) + " (" + player.getName() + ")." );
        
        // Handle success/failure
        if ( success ) {
            WCMessages.debug( "info", player.getName() + " added \"" + page + "\" successfully." );
            WCMessages.debug( "info", "Content: \n" + content );
            player.sendMessage( WCMessages.message( "info", "Page \"" + page + "\" created successfully." ) );

        } else {
            WCMessages.debug( "warning", player.getName() + " failed to add page \"" + page + "\"." );
            player.sendMessage( WCMessages.message( "error", "Failed to create page \"" + page + "\"." ) );

        }

        // Update last request time
        WCUtils.setLastRequestTime( player );
        
    }

    /**
     * Creates the {@code /wiki pages delete <page>} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.pages.delete}
     * <p><b>Usage:</b> {@code /wiki pages delete <page>}
     * <ul>
     *     <li>{@code /wiki pages delete <page>}: Deletes the specified wiki page
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki pages delete} subcommand
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiPages#getCommand()
     * @see WCCommandWikiPages#handleDeleteRequest(Player, long, Long, String)
     * @see WCCommandWikiPages#deletePage(Player, String)
     */
    private static CommandAPICommand deleteSubcommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki pages delete <page>" );

        // /wiki pages delete <page>
        return new CommandAPICommand( "delete" )
            .withPermission( "wikicraft.pages.delete" )
            .withArguments( WCArguments.wikiPagesArgument() )
            .executesPlayer( ( player, args ) -> {
                try {
                    long now = System.currentTimeMillis();
                    Long lastRequestTime = WCUtils.getLastRequestTimeMap().getOrDefault( player.getUniqueId(), 0L );
                    String page = args.getRaw( "page" );

                    handleDeleteRequest( player, now, lastRequestTime, page );

                } catch ( Exception e ) {
                    WCMessages.throwError( e, player );

                }
            } );
    }

    /**
     * Handles a request to delete a wiki page.
     *
     * @param player The player who is requesting to delete the page
     * @param now The current time
     * @param lastRequestTime The time of the player's last request
     * @param page The name of the page to delete
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiPages#getCommand()
     * @see WCCommandWikiPages#deleteSubcommand()
     * @see WCCommandWikiPages#deletePage(Player, String)
     */
    private static void handleDeleteRequest( @NotNull Player player, long now, Long lastRequestTime, String page ) {
        WCMessages.debug( "info", player.getName() + " is requesting to delete a page..." );

        if ( WCUtils.isOnRequestCooldown( player ) ) {
            player.sendMessage( WCMessages.message( "error", "Please wait " + ( ( WCConfigUtils.getMinRequestTime() - ( now - lastRequestTime ) ) / 1000 ) + " seconds before reloading." ) );
            return;

        }

        // Check if page exists before attempting delete
        if ( !WCWiki.getWiki().exists( page ) ) {
            player.sendMessage( WCMessages.message( "error", "Page " + page + " does not exist." ) );
            return;

        }

        deletePage( player, page );
    }

    /**
     * Deletes a wiki page.
     *
     * @param player The player who is requesting to delete the page
     * @param page The name of the page to delete
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiPages#getCommand()
     * @see WCCommandWikiPages#deleteSubcommand()
     * @see WCCommandWikiPages#handleDeleteRequest(Player, long, Long, String)
     */
    private static void deletePage( @NotNull Player player, String page ) {
        // Announce page deletion
        WCMessages.debug( "info", player.getName() + " (" + WCAccountBridge.getWikiUser( player.getUniqueId() ) + ") is requesting to delete page \"" + page + "\"..." );
        
        // Delete page
        WCWiki.getWiki().delete( page, "Content deleted from WikiCraft by " + WCAccountBridge.getWikiUser( player.getUniqueId() ) + " (" + player.getName() + ")." );

        if ( WCWiki.getWiki().exists( page ) ) {
            WCMessages.debug( "info", "Failed to delete page \"" + page + "\"." );
            player.sendMessage( WCMessages.message( "error", "Failed to delete page \"" + page + "\"." ) );

        } else {
            WCMessages.debug( "info", player.getName() + " deleted page \"" + page + "\"." );
            player.sendMessage( WCMessages.message( "info", "Page \"" + page + "\" deleted successfully." ) );

        }

        WCUtils.setLastRequestTime( player );
        
    }

    /**
     * Creates the {@code /wiki pages edit <page> <content>} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.pages.manage}
     * <p><b>Usage:</b> {@code /wiki pages edit <page> <content>}
     * <ul>
     *     <li>{@code /wiki pages edit <page> <content>}: Edits the specified wiki page with new content
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki pages edit} subcommand
     *
     * @since 0.1.0 (unused)
     *
     * @see WCCommandWikiPages#getCommand()
     * @see WCCommandWikiPages#handleEditRequest(Player, String, String)
     * @see WCCommandWikiPages#editPage(Player, String, String)
     */
    private static CommandAPICommand editSubcommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki pages edit <page> <content>" );

        // /wiki pages edit <page> <content>
        return new CommandAPICommand( "edit" )
            .withPermission( "wikicraft.pages.manage" )
            .withArguments( WCArguments.wikiPagesArgument(), new GreedyStringArgument( "content" ) )
            .executesPlayer( ( player, args ) -> {
                try {
                    String page = args.getRaw( "page" );
                    String content = args.getRaw( "content" );

                    handleEditRequest( player, page, content );

                } catch ( Exception e ) {
                    WCMessages.throwError( e, player );

                }
            } );
    }

    /**
     * Handles a request to edit a wiki page.
     * @param player The player who is requesting to edit the page
     * @param page The name of the page to edit
     * @param content The new content of the page
     *
     * @since 0.1.0 (unused)
     *
     * @see WCCommandWikiPages#getCommand()
     * @see WCCommandWikiPages#editSubcommand()
     * @see WCCommandWikiPages#editPage(Player, String, String)
     */
    private static void handleEditRequest( @NotNull Player player, String page, String content ) {
        WCMessages.debug( "info", player.getName() + " is requesting to edit a page..." );

        if ( WCUtils.isOnRequestCooldown( player ) ) {
            long cooldown = ( System.currentTimeMillis() - WCUtils.getLastRequestTimeMap().getOrDefault( player.getUniqueId(), 0L ) ) / 1000;
            WCMessages.debug( "info", player.getName() + " is on cooldown for " + cooldown + " seconds." );
            player.sendMessage( WCMessages.message( "error", "Please wait " + cooldown + " seconds before reloading." ) );
            return;

        }

        if ( !WCWiki.getWiki().exists( page ) ) {
            player.sendMessage( WCMessages.message( "error", "Page " + page + " does not exist. Use '/wiki add' to apply these changes." ) );
            return;

        }

        editPage( player, page, content );
    }

    /**
     * Edits a wiki page.
     * @param player The player who is requesting to edit the page
     * @param page The name of the page to edit
     * @param content The new content of the page
     *
     * @since 0.1.0 (unused)
     *
     * @see WCCommandWikiPages#getCommand()
     * @see WCCommandWikiPages#editSubcommand()
     * @see WCCommandWikiPages#handleEditRequest(Player, String, String)
     */
    private static void editPage( @NotNull Player player, String page, String content ) {
        WCWiki.getWiki().edit( page, content, "Content generated from WikiCraft by " + WCAccountBridge.getWikiUser( player.getUniqueId() ) + " (" + player.getName() + ")." );
        player.sendMessage( WCMessages.message( "info", "Page " + page + " edited successfully." ) );
        WCUtils.setLastRequestTime( player );

    }

    /**
     * Creates the {@code /wiki pages info <page>} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.pages.view}
     * <p><b>Usage:</b> {@code /wiki pages info <page>}
     * <ul>
     *     <li>{@code /wiki pages info <page>}: Displays information about the specified wiki page
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki pages info} subcommand
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiPages#getCommand()
     * @see WCCommandWikiPages#displayPageInfo(Player, HashMap)
     */
    private static CommandAPICommand infoSubcommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki pages info <page>" );

        // /wiki pages info <page>
        return new CommandAPICommand( "info" )
            .withPermission( "wikicraft.pages.view" )
            .withArguments( WCArguments.wikiPagesArgument() )
            .executesPlayer( ( player, args ) -> {
                try {
                    String page = args.getRaw( "page" );
                    HashMap<String, String> pageInfo = WCWikiUtils.getPageInfo( page );

                    displayPageInfo( player, pageInfo );

                } catch ( Exception e ) {
                    WCMessages.throwError( e, player );

                }
                
            });
    }

    /**
     * Displays information about a wiki page.
     * @param player The player who is requesting the page information
     * @param pageInfo The information about the page
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiPages#getCommand()
     * @see WCCommandWikiPages#infoSubcommand()
     */
    private static void displayPageInfo( @NotNull Player player, @NotNull HashMap<String, String> pageInfo ) {
        player.sendMessage( WCMessages.message( "info", "Page info: " ) );
        player.sendMessage( WCMessages.message( "info", "Title: " + pageInfo.get( "title" ), false ) );
        player.sendMessage( WCMessages.message( "info", "URL: ", false ).append( WCMessages.message( "info", pageInfo.get( "url" ), false ) ) );
        player.sendMessage( WCMessages.message( "info", "Categories: " + pageInfo.get( "categories" ), false ) );
        player.sendMessage( WCMessages.message( "info", "Last edit by: ", false ).append( WCMessages.message( "info", WCUtils.hyperlink( pageInfo.get( "last_editor" ), WCUtils.userURL( pageInfo.get( "last_editor" ) ) ), false ) ) );

    }

    /**
     * Creates the {@code /wiki pages read <page>} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.pages.view}
     * <p><b>Usage:</b> {@code /wiki pages read <page>}
     * <ul>
     *     <li>{@code /wiki pages read <page>}: Displays the specified wiki page
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki pages read} subcommand
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiPages#getCommand()
     * @see WParser#parsePage(Wiki, String)
     */
    private static CommandAPICommand readSubcommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki pages read <page>" );

        // /wiki pages read <page>
        return new CommandAPICommand( "read" )
            .withPermission( "wikicraft.pages.view" )
            .withArguments( WCArguments.wikiPagesArgument() )
            .executesPlayer( ( player, args ) -> {
                try {
                    String page = args.getRaw( "page" );
                    player.sendMessage( WCMessages.message( "info", String.valueOf( WParser.parsePage( WCWiki.getWiki(), page ) ) ) );

                } catch ( Exception e ) {
                    WCMessages.throwError( e, player );

                }
            } );
    }

    /**
     * Creates the {@code /wiki pages search [query]} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.pages.view}
     * <p><b>Usage:</b> {@code /wiki pages search [query]}
     * <ul>
     *     <li>{@code /wiki pages search}: Lists all wiki pages
     *     <li>{@code /wiki pages search <query>}: Searches for wiki pages matching the query
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki pages search} subcommand
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiPages#getCommand()
     */
    private static CommandAPICommand searchSubcommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki pages search [query]" );

        // /wiki pages search [query]
        return new CommandAPICommand( "search" )
            .withPermission( "wikicraft.pages.view" )
            .withOptionalArguments( new GreedyStringArgument( "query" ) )
            .executesPlayer( ( player, args ) -> {
                try {
                    String query = args.getRaw( "query" );
                    
                    if ( query == null ) {
                        displayAllPages( player );
                        return;

                    }

                    performSearch( player, query );

                } catch ( Exception e ) {
                    WCMessages.throwError( e, player );
                    
                }
                
            } );

    }

    /**
     * Displays all wiki pages to the player.
     * 
     * @param player The player to display the pages to.
     * 
     * @since 0.1.0
     * 
     * @see WCCommandWikiPages#getCommand()
     * @see WCCommandWikiPages#searchSubcommand()
     * @see WCCommandWikiPages#performSearch(Player, String)
     */
    private static void displayAllPages( Player player ) {
        for ( String result : WCWikiUtils.getAllPages( "main" ) ) {
            player.sendMessage( WCMessages.message( "info", result ) );

        }
        
    }

    /**
     * Searches for wiki pages matching the query and displays the results to the player.
     * 
     * @param player The player to display the results to.
     * @param query The query to search for.
     * 
     * @since 0.1.0
     * 
     * @see WCCommandWikiPages#getCommand()
     * @see WCCommandWikiPages#searchSubcommand()
     * @see WCCommandWikiPages#displayAllPages(Player)
     */
    private static void performSearch( Player player, String query ) {
        if ( WCWikiUtils.getResultCount( query ) > 1 ) {
            for ( String result : WCWikiUtils.searchWiki( query ) ) {
                player.sendMessage( WCMessages.message( "info", result ) );
                WCMessages.debug( "info", player.getName() + " found page " + result + " with query " + query );

            }

        } else {
            player.sendMessage( WCMessages.message( "error", "No results found for query \"" + query + "\"." ) );

        }

    }

}

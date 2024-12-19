package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.fastily.jwiki.core.NS;
import io.github.iherongh.wikicraft.messages.WCMessages;
import io.github.iherongh.wikicraft.wiki.WCWiki;

import java.util.ArrayList;

public class WCCommandWikiPages {
    
    /**
     * Creates and returns the main command for wiki page operations.
     * This command includes subcommands for deleting, editing, getting info, and searching wiki pages.
     *
     * @return A {@link CommandAPICommand} object representing the "pages" command with its subcommands.
     */
    public static CommandAPICommand getCommand() {
        return new CommandAPICommand( "pages" ).withSubcommands(
            
            // /wiki pages add
            // @todo add subcommand "/wiki pages add"
            addSubcommand(),

            // /wiki pages delete
            // @todo add subcommand "/wiki pages delete"
            deleteSubcommand(),

            // /wiki pages edit
            // @todo add subcommand "/wiki pages edit"
            editSubcommand(),

            // /wiki pages info
            // @todo add subcommand "/wiki pages info"
            infoSubcommand(),

            // /wiki pages search
            // @todo add subcommand "/wiki pages search"
            searchSubcommand()

        );
    
    }
    
    private static CommandAPICommand addSubcommand() {
        return new CommandAPICommand( "add" )
            .withArguments( new StringArgument( "page" ), new GreedyStringArgument( "content" ) )
            .executesPlayer( ( player, args ) -> {
                try {
                    String page = args.get( "page" ).toString();
                    
                    if ( WCWiki.getWiki().exists( page ) ) {
                        // @todo save content to player metadata in order to have /wiki edit <page> apply changes to provided page
                        player.sendMessage( WCMessages.message( "error", "Page " + page + " already exists. Use '/wiki edit' to apply these changes." ) );
                        
                    } else {
                        String content = args.get( "content" ).toString();
                        WCWiki.getWiki().addText( page, content, "Content generated from WikiCraft.", false );
                        player.sendMessage( WCMessages.message( "info", "Page " + page + " created successfully." ) );
                        
                    }
                    
                } catch ( Exception e ) {
                    WCMessages.throwError( e, player );
                    
                }
            } );
    }
    
    private static CommandAPICommand deleteSubcommand() {
        return new CommandAPICommand( "delete" )
            .withArguments( new StringArgument( "page" ) )
            .executesPlayer( ( player, args ) -> {
                try {
                    String page = args.get( "page" ).toString();
                    
                    WCWiki.getWiki().delete( page, "Content deleted from WikiCraft." );
                    player.sendMessage( WCMessages.message( "info", "Page " + page + " deleted successfully." ) );
                    
                } catch ( Exception e ) {
                    WCMessages.throwError( e, player );
                    
                }
            } );
    }
    
    private static CommandAPICommand editSubcommand() {
        return new CommandAPICommand( "edit" )
            .withArguments( new StringArgument( "page" ), new GreedyStringArgument( "content" ) )
            .executesPlayer( ( player, args ) -> {
                try {
                    String page = args.get( "page" ).toString();
                    String content = args.get( "content" ).toString();
                    
                    WCWiki.getWiki().edit( page, content, "Content edited from WikiCraft." );
                    player.sendMessage( WCMessages.message( "info", "Page " + page + " edited successfully." ) );
                    
                } catch ( Exception e ) {
                    WCMessages.throwError( e, player );
                    
                }
            } );
    }
    
    private static CommandAPICommand infoSubcommand() {
        return new CommandAPICommand( "info" )
            .withArguments( new GreedyStringArgument( "page" ) )
            .executesPlayer( ( player, args ) -> {
                try {
                    String page = args.get( "page" ).toString();
                    
                    if ( page == null ) {
                        player.sendMessage( WCMessages.message( "info", "No page provided." ) );
                        
                    } else {
                        player.sendMessage( WCMessages.message( "info", "Page info: " + WCWiki.getPageInfo( page ) ) );
                        
                    }
                    
                } catch ( Exception e ) {
                    WCMessages.throwError( e, player );
                }
                
            });
    }
    
    private static CommandAPICommand searchSubcommand() {
        // /wiki pages search <query>
        return new CommandAPICommand( "search" )
            // /wiki search <query>
            .withOptionalArguments( new GreedyStringArgument( "query" ) )
            .executesPlayer( ( player, args ) -> {
                try {
                    WCMessages.debug( "info", String.valueOf( args.count() ) );
                    
                    if ( args.get( "query" ) == null ) {
                        for ( String result : WCCommandWikiPages.pullAllPages() ) {
                            player.sendMessage( WCMessages.message( "info", result ) );
                            
                        }
                        
                    } else {
                        String query = args.rawArgs()[ 0 ];
                        
                        if ( WCCommandWikiPages.getResultCount( query ) > 1 ) {
                            for ( String result : WCCommandWikiPages.searchWiki( query ) ) {
                                player.sendMessage( WCMessages.message( "info", result ) );
                                WCMessages.debug( "info", player.getName() + " found page " + result + " with query " + query );
                                
                            }
                            
                        } else {
                            player.sendMessage( WCMessages.message( "error", "No results found for query \"" + query + "\"." ) );
                        }
                        
                    }
                    
                } catch ( Exception e ) {
                    WCMessages.throwError( e, player );
                    
                }
            } );
    }
    
    public static ArrayList<String> searchWiki( String query ) {
        return WCWiki.getWiki().search( query, 10 );

    }
    
    public static int getResultCount( String query ) {
        WCWiki.buildWiki();
        return searchWiki( query ).size();

    }
    
    public static ArrayList<String> pullAllPages() {
        return WCWiki.buildWiki().allPages( "", false, false, 50, NS.MAIN );

    }
}

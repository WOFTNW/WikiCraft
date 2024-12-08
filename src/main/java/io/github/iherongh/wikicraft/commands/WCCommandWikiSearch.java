package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import io.github.fastily.jwiki.core.NS;
import io.github.iherongh.wikicraft.messages.WCMessages;
import io.github.iherongh.wikicraft.wiki.WCWiki;

import java.util.ArrayList;

public class WCCommandWikiSearch {

    public static CommandAPICommand getCommand() {
        return new CommandAPICommand( "search" )
            // /wiki search <query>
            .withOptionalArguments( new GreedyStringArgument( "query" ) ).executes( ( sender, args ) -> {
                try {
                    WCMessages.debug( "info", String.valueOf( args.count() ) );
                    if ( args.count() == 0 ) {
                        for ( String result : WCCommandWikiSearch.pullAllPages() ) {
                            sender.sendMessage( WCMessages.message( "info", result ) );

                        }

                    } else {
                        String query = args.rawArgs()[ 0 ];

                        if ( WCCommandWikiSearch.getResultCount( query ) > 1 ) {
                            for ( String result : WCCommandWikiSearch.searchWiki( query ) ) {
                                sender.sendMessage( WCMessages.message( "info", result ) );
                                WCMessages.debug( "info", sender.getName() + " found page " + result + " with " + "query " + query );

                            }

                        } else
                            sender.sendMessage( WCMessages.message( "error", "No results found for query \"" + query + "\"." ) );

                    }

                } catch ( Exception e ) {
                    WCMessages.debug( "warning", "Failed to make search: " + e.getMessage() );
                }
            } );
    }

    public static ArrayList<String> searchWiki( String query ) {
        WCWiki.buildWiki();
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

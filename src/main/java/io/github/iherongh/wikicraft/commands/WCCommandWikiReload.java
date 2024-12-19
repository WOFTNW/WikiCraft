package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPICommand;

public class WCCommandWikiReload {

    public static CommandAPICommand getCommand() {
        return new CommandAPICommand( "reload" )
            .executes( ( sender, args ) -> {
                sender.sendMessage( "/wiki reload" );
                sender.sendMessage( args.rawArgs() );

            } );

    }

}

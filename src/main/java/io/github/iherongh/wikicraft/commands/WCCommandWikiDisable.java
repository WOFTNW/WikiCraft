package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import io.github.iherongh.wikicraft.WikiCraft;

public class WCCommandWikiDisable {

    public static CommandAPICommand getCommand() {
        return new CommandAPICommand( "disable" ).withPermission( CommandPermission.OP ).executes( ( sender, args ) -> {
            if ( args.count() > 0 ) {
                sender.sendMessage( String.valueOf( args.count() ) );
                throw CommandAPI.failWithString( "Too many arguments provided!" );

            }

            if ( sender != null )
                sender.sendMessage( "Disabling WikiCraft..." );
            WikiCraft.disableWikiCraft();
            if ( sender != null )
                sender.sendMessage( "WikiCraft disabled." );

        } );

    }

}

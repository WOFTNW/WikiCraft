package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;

public class WCCommandWikiConfig {

    public static CommandAPICommand getCommand() {
        return new CommandAPICommand( "config" ).withSubcommands(
            // /wiki config get
            new CommandAPICommand( "get" ).withOptionalArguments( new StringArgument( "key" ) ).executes( ( sender, args ) -> {
                sender.sendMessage( "/wiki config get" );
                sender.sendMessage( args.rawArgs() );
            } ),
            // /wiki config reload
            new CommandAPICommand( "reload" ).executes( ( sender, args ) -> {
                sender.sendMessage( "/wiki config reload" );
                sender.sendMessage( args.rawArgs() );
            } ),
            // /wiki config set
            new CommandAPICommand( "set" ).executes( ( sender, args ) -> {
                sender.sendMessage( "/wiki config set" );
                sender.sendMessage( args.rawArgs() );
            } ) );

    }

}

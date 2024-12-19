package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;

public class WCCommandWikiConfig {

    public static CommandAPICommand getCommand() {
        return new CommandAPICommand( "config" ).withSubcommands(

            // /wiki config get
            getSubcommand(),

            // /wiki config reload
            reloadSubcommand(),

            // /wiki config set
            setSubcommand()

        );

    }

    public static CommandAPICommand getSubcommand() {
        return new CommandAPICommand( "get" ).withOptionalArguments( new StringArgument( "key" ) )
            .executes( ( sender, args ) -> {
                sender.sendMessage( args.fullInput() );
                sender.sendMessage( args.rawArgs() );

            } );

    }

    private static CommandAPICommand reloadSubcommand() {
        return new CommandAPICommand( "reload" )
            .executes( ( sender, args ) -> {
                sender.sendMessage( args.fullInput() );
                sender.sendMessage( args.rawArgs() );

            } );
    }

    private static CommandAPICommand setSubcommand() {
        return new CommandAPICommand( "set" )
            .executes( ( sender, args ) -> {
                sender.sendMessage( "/wiki config set" );
                sender.sendMessage( args.rawArgs() );

            } );
    }

}

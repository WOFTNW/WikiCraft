package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.ExecutableCommand;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.iherongh.wikicraft.WikiCraft;
import io.github.iherongh.wikicraft.config.ConfigUtils;

import org.jetbrains.annotations.NotNull;

public class CommandWiki {
	ConfigUtils configUtils = new ConfigUtils();

	public @NotNull ExecutableCommand<?, ?> getCommand() {
		// /wiki <config|disable|reload|search>
		return new CommandAPICommand( "wiki" ).withSubcommands(
				// /wiki config <get|reload|set>
				new CommandAPICommand( "config" ).withSubcommands(
						// /wiki config get
						new CommandAPICommand( "get" )
								.withOptionalArguments( new StringArgument( "key" ))
								.executes( ( sender, args ) -> {
//									if ( args.count() == 1 ) {
//										sender.sendMessage( configUtils.getConfigContent( args.rawArgs()[ 0 ], ConfigUtils.configFile ) );
//									} else {
//										sender.sendMessage( configUtils.getConfigContent( null, ConfigUtils.configFile ) );
//									}
									sender.sendMessage( "/wiki config get" );
									sender.sendMessage( args.rawArgs() );
								} ),
						// /wiki config reload
						new CommandAPICommand( "reload" )
								.executes( ( sender, args ) -> {
									sender.sendMessage( "/wiki config reload" );
									sender.sendMessage( args.rawArgs() );
								} ),
						// /wiki config set
						new CommandAPICommand( "set" )
								.executes( ( sender, args ) -> {
									sender.sendMessage( "/wiki config set" );
									sender.sendMessage( args.rawArgs() );
								} )
				),
				// /wiki disable
				new CommandAPICommand( "disable" )
						.withPermission( CommandPermission.OP )
						.executes( ( sender, args ) -> {
							if ( args.count() > 0 ) {
								sender.sendMessage( String.valueOf( args.count() ) );
								throw CommandAPI.failWithString( "Too many arguments provided!" );

							}

							sender.sendMessage( "Disabling WikiCraft..." );
							WikiCraft.disableWikiCraft();
							sender.sendMessage( "WikiCraft disabled." );
						} ),
				// /wiki reload
				new CommandAPICommand( "reload" )
						.executes( ( sender, args ) -> {
							sender.sendMessage( "/wiki reload" );
							sender.sendMessage( args.rawArgs() );
						} ),
				// /wiki search
				new CommandAPICommand( "search" )
						.executes( ( sender, args ) -> {
							sender.sendMessage( "/wiki search" );
							sender.sendMessage( args.rawArgs() );
						} )
		);

	}

}

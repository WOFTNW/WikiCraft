package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.ExecutableCommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import io.github.iherongh.wikicraft.WikiCraft;
import io.github.iherongh.wikicraft.file.WCFileAccountBridge;
import io.github.iherongh.wikicraft.messages.WCMessages;
import org.jetbrains.annotations.NotNull;
import org.shanerx.mojang.Mojang;

import java.util.UUID;

public class WCCommandWiki {

	public @NotNull ExecutableCommand<?, ?> getCommand() {
		// /wiki <account|config|disable|login|reload|search>
		return new CommandAPICommand( "wiki" ).withSubcommands(
				// /wiki account <getPlayer|getWikiUser>
				new CommandAPICommand( "account" ).withSubcommands(
						// /wiki account getPlayer
						new CommandAPICommand( "getPlayer" )
								// /wiki account getWikiUser <wiki_user>
								.withArguments( new GreedyStringArgument( "wiki_user" ))
								.executes( ( sender, args ) -> {
									sender.sendMessage( new Mojang().connect().getPlayerProfile( WCFileAccountBridge.getUUID( args.rawArgs()[ 0 ] ) ).getUsername() );
								}),
						// /wiki account getWikiUser
						new CommandAPICommand( "getWikiUser" )
								// /wiki account getWikiUser <username>
								.withArguments( new StringArgument( "username" ))
								.executes( ( sender, args ) -> {
									String uuid = new Mojang().connect().getUUIDOfUsername( args.rawArgs()[ 0 ] );
									sender.sendMessage( WCFileAccountBridge.getWikiUser( WCFileAccountBridge.formatStringUUIDToUUID( uuid ) ) );
								})
				),
				// /wiki config <get|reload|set>
				new CommandAPICommand( "config" ).withSubcommands(
						// /wiki config get
						new CommandAPICommand( "get" )
								.withOptionalArguments( new StringArgument( "key" ))
								.executes( ( sender, args ) -> {
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

							if ( sender != null ) { sender.sendMessage( "Disabling WikiCraft..." ); }
							WikiCraft.disableWikiCraft();
                            if ( sender != null ) { sender.sendMessage( "WikiCraft disabled." ); }
                        } ),
				// /wiki login <refresh|with>
				new CommandAPICommand( "login" ).withSubcommands(
						// /wiki login refresh
						new CommandAPICommand( "refresh" )
								.withPermission( CommandPermission.fromString( "wikicraft.user" ) )
								.executes( ( sender, args ) -> {
									sender.sendMessage( WCCommandWikiLogin.refreshLogin() );
								}),
						// /wiki login with <username> <password>
						new CommandAPICommand( "with" )
								.withPermission( CommandPermission.fromString( "wikicraft.user" ) )
								.withArguments( new TextArgument( "username" ), new TextArgument( "password" ) )
								.executes( ( sender, args ) -> {
									String wikiUsername = args.rawArgs()[ 0 ];
									String wikiPassword = args.rawArgs()[ 1 ];
									if ( WCCommandWikiLogin.requestLogin( sender, wikiUsername, wikiPassword ) ) {
										sender.sendMessage( WCMessages.message( "info", "Login successful!" ) );

									} else {
										sender.sendMessage( WCMessages.message( "error", "Login unsuccessful. Make sure your username and password are correct!" ) );

									}

								} )
								.withOptionalArguments( new StringArgument( "mcUser" )
										.executes( ( sender, args ) -> {
											try {
												String wikiUsername = args.rawArgs()[ 0 ];
												String wikiPassword = args.rawArgs()[ 1 ];
												String mcUsername = args.rawArgs()[ 2 ];

												if ( WCCommandWikiLogin.requestLogin( sender, mcUsername, wikiUsername, wikiPassword ) ) {
													sender.sendMessage( WCMessages.message( "info", "Login successful! Welcome, " + wikiUsername + "!" ) );

												} else {
													sender.sendMessage( WCMessages.message( "error", "Login unsuccessful. Make sure your username and password are correct!" ) );

												}
											} catch ( Exception e ) {
												WCMessages.debug( "error", e.getMessage());

											}
										}))
						),
				// /wiki reload
				new CommandAPICommand( "reload" )
						.executes( ( sender, args ) -> {
							sender.sendMessage( "/wiki reload" );
							sender.sendMessage( args.rawArgs() );
						} ),
				// /wiki search
				new CommandAPICommand( "search" )
						// /wiki search <query>
						.withOptionalArguments( new GreedyStringArgument( "query" ) )
						.executes( ( sender, args ) -> {
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
				                            WCMessages.debug( "info", sender.getName() + " found page " + result + " with query " + query );

			                            }

		                            } else {
			                            sender.sendMessage( WCMessages.message( "error", "No results found for your query \"" + query + "\"." ) );

		                            }

								}

							} catch ( Exception e ) {
								WCMessages.debug( "warning",  "Failed to make search: " + e.getMessage() );

							}
                        } )
		);

	}

}

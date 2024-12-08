package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import io.github.iherongh.wikicraft.file.WCFileAccountBridge;
import io.github.iherongh.wikicraft.messages.WCMessages;
import io.github.iherongh.wikicraft.wiki.WCWiki;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shanerx.mojang.Mojang;

public class WCCommandWikiAccount {

    public static CommandAPICommand getCommand() {
        // /wiki account <getPlayer|getWikiUser|login|refresh>
        return new CommandAPICommand( "account" ).withSubcommands(
            // /wiki account getToken
            getTokenSubcommand(),

            // /wiki account getPlayer
            getPlayerSubcommand(),

            // /wiki account getWikiUser
            getWikiUserSubcommand(),

            // /wiki account login
            loginSubcommand(),

            // /wiki account refresh
            refreshSubcommand()
        );
    }

    public static CommandAPICommand getTokenSubcommand() {
        return new CommandAPICommand( "getToken" )
            // /wiki account getToken
            .executes( ( sender, args ) -> {
                try {
                    sender.sendMessage( WCWiki.getLoginToken() );

                } catch ( Exception e ) {
                    sender.sendMessage( WCMessages.message( "error", "Error: " + e.getMessage() ) );
                }
            } );
    }

    public static CommandAPICommand getPlayerSubcommand() {
        return new CommandAPICommand( "getPlayer" )
            // /wiki account getPlayer <wiki_username>
            .withArguments( new GreedyStringArgument( "wiki_user" ) )
            .executes( ( sender, args ) -> {
                String username = args.rawArgs()[ 0 ];
                sender.sendMessage( new Mojang().connect().getPlayerProfile( WCFileAccountBridge.getUUID( username ) ).getUsername() );
            } );

    }

    public static CommandAPICommand getWikiUserSubcommand() {
        return new CommandAPICommand( "getWikiUser" )
            // /wiki account getWikiUser <mc_username>
            .withArguments( new StringArgument( "username" ) )
            .executes( ( sender, args ) -> {
                String username = args.rawArgs()[ 0 ];
                String uuid = new Mojang().connect().getUUIDOfUsername( username );
                if ( WCFileAccountBridge.getWikiUser( WCFileAccountBridge.formatStringUUIDToUUID( uuid ) ).equals( username ) ) {
                    sender.sendMessage( WCMessages.message( "error", "" ) );

                } else {
                    sender.sendMessage( WCMessages.message( "error", "No user matches UUID " + uuid + "." ) );

                }
            } );

    }

    private static CommandAPICommand loginSubcommand() {
        // /wiki account login <username> <password>
        return new CommandAPICommand( "login" )
            .withArguments( new TextArgument( "wikiUsername" ), new TextArgument( "wikiPassword" ) )
            .executes( ( sender, args ) -> {
                String wikiUsername = args.rawArgs()[ 0 ];
                String wikiPassword = args.rawArgs()[ 1 ];

                if ( requestLogin( sender, wikiUsername, wikiPassword ) ) {
                    sender.sendMessage( WCMessages.message( "info", "Login successful! Welcome, " + wikiUsername + "!" ) );

                } else {
                    sender.sendMessage( WCMessages.message( "error", "Unable to login! Double check login details." ) );

                }

            } )
            // /wiki account login <username> <password> [mcUser]
            .withOptionalArguments( new StringArgument( "mcUsername" )
                .executes( ( sender, args ) -> {
                    try {
                        String wikiUsername = args.rawArgs()[ 0 ];
                        String wikiPassword = args.rawArgs()[ 1 ];
                        String mcUsername = args.rawArgs()[ 2 ];

                        if ( requestLogin( sender, mcUsername, wikiUsername, wikiPassword ) ) {
                            sender.sendMessage( WCMessages.message( "info", "Login successful!" ) );

                        } else {
                            sender.sendMessage( WCMessages.message( "error", "Unable to login! Double check login details." ) );

                        }

                    } catch ( Exception e ) {
                        WCMessages.debug( "error", e.getMessage() );

                    }

                } ) );
    }

    private static CommandAPICommand refreshSubcommand() {
        // /wiki account refresh <password>
        return new CommandAPICommand( "refresh" )
            .withArguments( new StringArgument( "password" ) )
            .executes( ( sender, args ) -> {
                if ( refreshLogin() ) {
                    sender.sendMessage( WCMessages.message( "info", "Login successful!" ) );

                } else {
                    sender.sendMessage( WCMessages.message( "info", "Unable to login! Double check login details." ) );

                }

            } );
    }

    public static boolean requestLogin( CommandSender sender, String wikiUsername, String wikiPassword ) {
        if ( sender instanceof Player player ) {
            return requestLogin( sender, player.getName(), wikiUsername, wikiPassword );

        } else {
            return requestLogin( sender, null, wikiUsername, wikiPassword );

        }

    }

    public static boolean requestLogin( CommandSender sender, String mcUsername, String wikiUsername, String wikiPassword ) {
        wikiUsername = wikiUsername.replace( "\"", "" );
        wikiPassword = wikiPassword.replace( "\"", "" );

        if ( !WCWiki.getWiki().login( wikiUsername, wikiPassword ) ) {
            WCMessages.debug( "info", "Login unsuccessful under username " + wikiUsername );
            return false;

        }

        WCMessages.debug( "info", "Login successful under username " + wikiUsername );

        return true;

    }

    public static boolean refreshLogin() {
        return ( WCWiki.getWiki().whoami() == null || WCWiki.getWiki().whoami().isEmpty() );

    }

}

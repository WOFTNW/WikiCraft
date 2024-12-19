package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import io.github.iherongh.wikicraft.account.WCAccountBridge;
import io.github.iherongh.wikicraft.messages.WCMessages;
import io.github.iherongh.wikicraft.wiki.WCWiki;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class WCCommandWikiAccount {

    public static CommandAPICommand getCommand() {
        // /wiki account <get|list|login|refresh>
        return new CommandAPICommand( "account" ).withSubcommands(
            // /wiki account get
            getSubcommand(),

            // /wiki account list
            listSubcommand(),

            // /wiki account login
            loginSubcommand(),

            // /wiki account refresh
            refreshSubcommand()

        );
    }

    public static CommandAPICommand getSubcommand() {
        return new CommandAPICommand( "get" )
            .withOptionalArguments( new MultiLiteralArgument( "fetch", "player", "token", "wikiUser" )
                .combineWith( WCCommandWiki.playerNameArgument( false ) )
            )
            .executesPlayer( ( player, args ) -> {
                try {
                    switch ( (String) args.get( "fetch" ) ) {
                        case null:
                            player.sendMessage( WCMessages.message( "info", getWikiUsers().toString() ) );
                            break;
                        case "player":
                            player.sendMessage( WCMessages.message( "info", WCAccountBridge.getUUID( (String) args.get( "name" ) ) ) );
                            break;
                        case "token":
                            player.sendMessage( WCMessages.message( "info", "Requested token." ) );
                            break;
                        case "wikiUser":
                            player.sendMessage( WCMessages.message( "info", WCAccountBridge.getWikiUser( (Player) args.get( "name" ) ) ) );
                            break;
                        default:
                            player.sendMessage( WCMessages.message( "error", "Unexpected value: " + args.get( "fetch" ) ) );

                    }
                    player.sendMessage( WCMessages.message( "info", args.fullInput() ) );

                } catch ( Exception e ) {
                    WCMessages.throwError( e, player );

                }

            } );

    }

    private static CommandAPICommand listSubcommand() {
        return new CommandAPICommand( "list" )
            .executesPlayer( ( player, args ) -> {
                try {
                    player.sendMessage( WCMessages.message( "info", args.fullInput() ) );

                } catch ( Exception e ) {
                    WCMessages.throwError( e );

                }

            } );
    }

    private static CommandAPICommand loginSubcommand() {
        // /wiki account login <username> <password>
        return new CommandAPICommand( "login" )
            .withArguments( new TextArgument( "wikiUsername" ), new TextArgument( "wikiPassword" ) )
            .executesPlayer( ( player, args ) -> {
                try {
                    UUID uuid = player.getUniqueId();
                    String mcUsername = player.getName();
                    String wikiUsername = (String) args.get( "wikiUsername" );
                    String wikiPassword = (String) args.get( "wikiPassword" );

                    if ( WCWiki.getWiki().login( wikiUsername, wikiPassword ) ) {
                        player.sendMessage( WCMessages.message( "info", "Successfully logged in as " + wikiUsername + "!" ) );

                        if ( WCAccountBridge.addLink( uuid, wikiUsername ) ) {
                            WCMessages.debug( "info", "Account " + mcUsername + " linked to " + wikiUsername + "." );
                            player.sendMessage( WCMessages.message( "info", "Account " + mcUsername + " linked to " + wikiUsername + "!" ) );

                        } else {
                            WCMessages.debug( "error", "Unable to link account!" );
                            player.sendMessage( WCMessages.message( "error", "Unable to link accounts!" ) );

                        }

                    } else {
                        WCMessages.debug( "info", mcUsername + " attempted login to account " + wikiUsername );
                        player.sendMessage( WCMessages.message( "error", "Unable to log in! Check you account details." ) );

                    }

                } catch ( Exception e ) {
                    WCMessages.throwError( e, player );

                }
            } )
            .executesConsole( ( console, args ) -> {
                try {
                    console.sendMessage( WCMessages.message( "info", "Unable to log in to the wiki from the console." ) );

                } catch ( Exception e ) {
                    WCMessages.throwError( e, console );

                }

            } );
    }

    private static CommandAPICommand refreshSubcommand() {
        // /wiki account refresh <password>
        return new CommandAPICommand( "refresh" )
            .executesPlayer( ( player, args ) -> {
                try {
                    player.sendMessage( WCMessages.message( "info", args.fullInput() ) );

                } catch ( Exception e ) {
                    WCMessages.throwError( e, player );

                }

            } );
    }

    private static Collection<String> getWikiUsers() {
        return WCAccountBridge.getUUIDToWikiUserMap().values();

    }

}

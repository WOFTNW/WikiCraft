package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPICommand;
import io.github.iherongh.wikicraft.account.WCAccountBridge;
import io.github.iherongh.wikicraft.arguments.WCArguments;
import io.github.iherongh.wikicraft.config.WCConfigUtils;
import io.github.iherongh.wikicraft.messages.WCMessages;
import io.github.iherongh.wikicraft.permissions.WCPermissions;
import io.github.iherongh.wikicraft.utils.WCUtils;
import io.github.iherongh.wikicraft.wiki.WCWiki;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * The {@code /wiki account} command.
 *
 * @author iHeronGH
 * @version 0.1.0
 * @since 0.1.0
 *
 * @see WCCommandWikiAccount#getCommand()
 * @see WCCommandWikiAccount#getSubcommand()
 * @see WCCommandWikiAccount#getPlayerSubcommand()
 * @see WCCommandWikiAccount#getWikiUserSubcommand()
 * @see WCCommandWikiAccount#linkSubcommand()
 * @see WCCommandWikiAccount#listSubcommand()
 * @see WCCommandWikiAccount#unlinkSubcommand()
 */
public class WCCommandWikiAccount {

    /**
     * Constructs a new {@code WCCommandWikiAccount} object.
     */
    public WCCommandWikiAccount() {}

    /**
     * Creates the {@code /wiki account <get|link|list|unlink>} command.
     * <p><b>Permission:</b> {@code wikicraft.command.account}
     * <p><b>Usage:</b> {@code /wiki account <get|link|list|unlink>}
     * <ul>
     *     <li>{@code /wiki account get <player|wikiUser>}: Gets the account information
     *     <li>{@code /wiki account link [wikiUser]}: Links the account
     *     <li>{@code /wiki account list}: Lists the accounts
     *     <li>{@code /wiki account unlink [wikiUser]}: Unlinks the account
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki account} command
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiAccount#getSubcommand()
     * @see WCCommandWikiAccount#getPlayerSubcommand()
     * @see WCCommandWikiAccount#getWikiUserSubcommand()
     * @see WCCommandWikiAccount#linkSubcommand()
     * @see WCCommandWikiAccount#listSubcommand()
     * @see WCCommandWikiAccount#unlinkSubcommand()
     */
    public static @Nullable CommandAPICommand getCommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki account <get|link|list|unlink>" );

        // Check if a wiki exists
        if ( WCWiki.getWiki() == null ) {
            WCMessages.debug( "warning", "No wiki exists; cancelling /wiki account <get|link|list|unlink>." );
            return null;

        }

        // /wiki account <get|link|list|unlink>
        return new CommandAPICommand( "account" ).withSubcommands(

            // /wiki account get
            getSubcommand(),

            // /wiki account link
            linkSubcommand(),

            // /wiki account list
            listSubcommand(),

            // /wiki account unlink
            unlinkSubcommand()

        );
    }

    /**
     * Creates the {@code /wiki account get <player|wikiUser>} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.command.account.get}
     * <p><b>Usage:</b> {@code /wiki account get <player|wikiUser>}
     * <ul>
     *     <li>{@code /wiki account get <player>}: Gets the MediaWiki account associated with the player's UUID
     *     <li>{@code /wiki account get <wikiUser>}: Gets the player associated with the MediaWiki user
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki account get} subcommand
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiAccount#getPlayerSubcommand()
     * @see WCCommandWikiAccount#getWikiUserSubcommand()
     */
    private static CommandAPICommand getSubcommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki account get <player|wikiUser>" );

        // /wiki account get <player|wikiUser>
        return new CommandAPICommand( "get" ).withSubcommands(

            // /wiki account get <player>
            getPlayerSubcommand(),

            // /wiki account get <wikiUser>
            getWikiUserSubcommand()

        ).withPermission( "wikicraft.command.account.get" );

    }

    /**
     * Creates the {@code /wiki account get player <wikiUser>} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.command.account.get}
     * <p><b>Usage:</b> {@code /wiki account get player <wikiUser>}
     * <ul>
     *     <li>{@code /wiki account get player <wikiUser>}: Gets the player associated with the MediaWiki user
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki account get player} subcommand
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiAccount#getSubcommand()
     * @see WCCommandWikiAccount#getWikiUserSubcommand()
     */
    private static CommandAPICommand getPlayerSubcommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki account get player <wikiUser>" );

        // /wiki account get player <wikiUser>
        return new CommandAPICommand( "player" )
            .withPermission( "wikicraft.command.account.get" )
            .withArguments( WCArguments.wikiUserArgument() )
            .executesPlayer( ( player, args ) -> {
                try {
                    String wikiUser = args.getRaw( "name" );
                    Map<String, UUID> accounts = WCAccountBridge.getWikiUserToUUIDMap();

                    if ( !accounts.containsKey( wikiUser ) ) {
                        WCMessages.debug( "warning", "No account found for " + wikiUser );
                        return;

                    }

                    UUID uuid = accounts.get( wikiUser );
                    String playerName = WCUtils.getPlayer( uuid ).getName();

                    player.sendMessage( WCMessages.message( "info", "Player name for " + wikiUser + " is " + playerName ) );

                } catch ( Exception e ) {
                    player.sendMessage( Component.text( "Error: " + e.getMessage() ) );

                }
            } );
    }

    /**
     * Creates the {@code /wiki account get wikiUser <player>} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.command.account.get}
     * <p><b>Usage:</b> {@code /wiki account get wikiUser <player>}
     * <ul>
     *     <li>{@code /wiki account get wikiUser <player>}: Gets the MediaWiki user associated with the specified player's UUID
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki account get wikiUser} subcommand
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiAccount#getSubcommand()
     * @see WCCommandWikiAccount#getPlayerSubcommand()
     */
    private static CommandAPICommand getWikiUserSubcommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki account get wikiUser <player>" );

        // /wiki account get wikiUser <player>
        return new CommandAPICommand( "wikiUser" )
            .withPermission( "wikicraft.command.account.get" )
            .withArguments( WCArguments.playerNameArgument( false ) )
                .executesPlayer( ( player, args ) -> {
                    try {
                        String playerName = args.getRaw( "name" );
                        UUID uuid = WCUtils.getPlayer( playerName ).getUniqueId();
                        Map<UUID, String> accounts = WCAccountBridge.getUUIDToWikiUserMap();

                        if ( !accounts.containsKey( uuid ) ) {
                            WCMessages.debug( "warning", "No account found for " + playerName );
                            player.sendMessage( WCMessages.message( "error", playerName + " has no associated wiki user." ) );
                            return;

                        }

                        String wikiUser = accounts.get( uuid );
                        player.sendMessage( WCMessages.message( "info", playerName + " is linked to " + wikiUser ) );

                    } catch ( Exception e ) {
                        player.sendMessage( Component.text( "Failed to get wiki user info: " + e.getMessage() ) );

                    }

                } );
    }

    /**
     * Creates the {@code /wiki account link [wikiUser]} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.account.bridge}
     * <p><b>Usage:</b>{@code /wiki account link [wikiUser]}
     * <ul>
     * <li>{@code /wiki account link}: Re-links the player's UUID to their registered MediaWiki account.
     * <li>{@code /wiki account link <wikiUser>}: Links the player's UUID to the specified MediaWiki user.
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki account link [wikiUser]} subcommand.
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiAccount#getCommand()
     * @see WCCommandWikiAccount#unlinkSubcommand()
     */
    public static CommandAPICommand linkSubcommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki account link [wikiUser]" );

        // /wiki account link [wikiUser]
        return new CommandAPICommand( "link" )
            .withPermission( "wikicraft.account.bridge" )
            .withOptionalArguments( WCArguments.unlinkedUsersArgument() )
            .executesPlayer( ( player, args ) -> {
                String wikiUser = args.getRaw( "wikiAccount" );

                try {
                    if ( wikiUser == null ) {
                        String relinkResult = WCAccountBridge.relinkAccount( player.getUniqueId(), wikiUser );
                        handleRelinkRequest( player, relinkResult );
                        return;

                    }

                    // Create new account bridge
                    long now = System.currentTimeMillis();
                    long lastRequestTime = WCUtils.getLastRequestTimeMap().getOrDefault( player.getUniqueId(), 0L );

                    // Check request cooldown
                    if ( WCUtils.isOnRequestCooldown( player ) ) {
                        long cooldown = ( ( ( now - lastRequestTime ) - WCConfigUtils.getMinRequestTime() ) / 1000 );
                        WCMessages.debug( "info", "Request cooldown for " + player.getName() + " is active for " + cooldown + " seconds." );
                        player.sendMessage( WCMessages.message( "error", "Please wait " + cooldown + " seconds before trying again." ) );
                        return;

                    }

                    handleLinkRequest( player, wikiUser );

                    WCUtils.setLastRequestTime( player );

                } catch ( Exception e ) {
                    WCMessages.debug( "error", "Unable to link " + player.getName() + " with account " + wikiUser + ": " + e.getMessage() );
                    WCMessages.throwError( e );

                }

            });

    }

    /**
     * Handles the result of a relink request.
     *
     * @param player        The player who initiated the relink request.
     * @param requestResult The result of the relink request.
     *
     * @since 0.1.0
     */
    private static void handleRelinkRequest( @NotNull Player player, @NotNull String requestResult ) {
        WCMessages.debug( "info", "Requesting relink for " + player.getName() + "..." );

        switch ( requestResult ) {
            case "bridge_already_linked" -> player.sendMessage( WCMessages.message( "error", "Your account is already linked to " + WCAccountBridge.getUUIDToWikiUserMap().get( player.getUniqueId() ) + "!" ) );
            case "no_account" -> player.sendMessage( WCMessages.message( "error", "You don't have an account to relink! Use /wiki account link <wikiUser> to create a bridge." ) );
            case "success" -> {
                player.sendMessage( WCMessages.message( "info", "Your account has been relinked to " + WCAccountBridge.getUUIDToWikiUserMap().get( player.getUniqueId() ) + "!" ) );
                WCPermissions.grantPermission( player.getUniqueId(), "wikicraft.pages.manage" );

            }
            default -> player.sendMessage( WCMessages.message( "error", "An unknown error occurred while trying to relink your account." ) );

        }

    }

    /**
     * Handles the result of a link request.
     *
     * @param player   The player who initiated the link request.
     * @param wikiUser The MediaWiki user to link the player's UUID to.
     *
     * @since 0.1.0
     */
    private static void handleLinkRequest( @NotNull Player player, String wikiUser ) {
        // Request link
        WCMessages.debug( "info", "Requesting link for " + player.getName() + " to " + wikiUser + "..." );

        // Handle request
        switch ( WCAccountBridge.requestLink( player.getUniqueId(), wikiUser ) ) {
            case "mediawiki_account_already_linked" -> {
                WCMessages.debug( "warning", "Link request from " + player.getName() + " to " + wikiUser + " failed: MediaWiki account already linked." );
                player.sendMessage( WCMessages.message( "error", "Link failed! " + wikiUser + " is already linked to " + WCUtils.getPlayer( WCAccountBridge.UUIDOfWikiUser( wikiUser ) ).getName() + "." ) );

            }
            case "uuid_already_linked" -> {
                WCMessages.debug( "warning", "Link request from " + player.getName() + " to " + wikiUser + " failed: UUID already linked." );
                player.sendMessage( WCMessages.message( "error", "Link failed! " + WCUtils.getPlayer( WCAccountBridge.UUIDOfWikiUser( wikiUser ) ).getName() + " is already linked to another account." ) );

            }
            case "user_subpage_not_found" -> {
                WCMessages.debug( "warning", "Link request from " + player.getName() + " to " + wikiUser + " failed: User subpage not found." );
                player.sendMessage( WCMessages.message( "error", "Link failed! The user subpage for " + wikiUser + " does not exist. Create it " + Component.text( "here" ).clickEvent( ClickEvent.openUrl( WCUtils.userURL( wikiUser, "WikiCraft" ) ) ) + "." ) );

            }
            case "user_subpage_creator_mismatch" -> {
                WCMessages.debug( "warning", "Link request from " + player.getName() + " to " + wikiUser + " failed: User subpage creator mismatch." );
                player.sendMessage( WCMessages.message( "error", "Link failed! The user subpage for " + wikiUser + " was not created by " + wikiUser + "." ) );

            }
            case "uuid_not_found" -> {
                WCMessages.debug( "warning", "Link request from " + player.getName() + " to " + wikiUser + " failed: UUID not found in subpage." );
                player.sendMessage( WCMessages.message( "error", "Link failed! The UUID for " + player.getName() + " could not be found in the subpage for " + wikiUser + "." ) );

            }
            case "success" -> {
                WCMessages.debug( "info", "Link request from " + player.getName() + " to " + wikiUser + " successful." );
                player.sendMessage( WCMessages.message( "info", "Link successful! Welcome to WikiCraft, " + wikiUser + "!" ) );

            }
            case "add_link_failed" -> {
                WCMessages.debug( "warning", "Link request from " + player.getName() + " to " + wikiUser + " failed: Failed to add link." );
                player.sendMessage( WCMessages.message( "error", "Link failed! An unknown error occurred." ) );

            }
            default -> {
                WCMessages.debug( "warning", "Link request from " + player.getName() + " to " + wikiUser + " failed: Unknown error." );
                player.sendMessage( WCMessages.message( "error", "Link failed! An unknown error occurred." ) );

            }

        }

    }

    /**
     * Creates the {@code /wiki account list} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.command.account.list}
     * <p><b>Usage:</b> {@code /wiki account list}
     * <ul>
     *     <li>{@code /wiki account list}: Lists all linked accounts
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki account list} subcommand
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiAccount#getCommand()
     */
    private static CommandAPICommand listSubcommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki account list" );

        // /wiki account list
        return new CommandAPICommand( "list" )
            .withPermission( "wikicraft.command.account.list" )
            .executesPlayer( ( player, args ) -> {
                try {
                    Map<UUID, String> links = WCAccountBridge.getUUIDToWikiUserMap();

                    player.sendMessage( WCMessages.message( "info", "Minecraft <-> Wiki links:" ) );

                    for ( Map.Entry<UUID, String> entry : links.entrySet() ) {
                        String mcUsername = WCUtils.getPlayer( entry.getKey() ).getName();
                        Component message = WCAccountBridge.getAccountPair( entry, mcUsername );

                        player.sendMessage( WCMessages.message( "info", message, false ) );
                    }

                } catch ( Exception e ) {
                    WCMessages.throwError( e );

                }

            } );
    }

    /**
     * Creates the {@code /wiki account unlink [wikiUser]} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.account.bridge}
     * <p><b>Usage:</b> {@code /wiki account unlink [wikiUser]}
     * <ul>
     *     <li>{@code /wiki account unlink}: Unlinks the executing player's UUID from their currently linked MediaWiki user
     *     <li>{@code /wiki account unlink <wikiUser>}: Unlinks the player's UUID from the specified MediaWiki user
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki account unlink} subcommand
     *
     * @since 0.1.0
     *
     * @see WCCommandWikiAccount#getCommand()
     * @see WCCommandWikiAccount#linkSubcommand()
     */
    private static CommandAPICommand unlinkSubcommand() {
        // Log subcommand load
        WCMessages.debug( "info", "Creating command: /wiki account unlink [wikiUser]" );

        // /wiki account unlink [wikiUser]
        return new CommandAPICommand( "unlink" )
            .withPermission( "wikicraft.account.bridge" )
            .withOptionalArguments( WCArguments.linkedUsersArgument() )
            .executesPlayer( ( player, args ) -> {
                try {
                    String wikiUser = args.getRaw( "wikiUser" );
                    
                    handleUnlinkRequest( player, wikiUser );

                } catch ( Exception e ) {
                    WCMessages.throwError( e );

                }

            } );

    }

    /**
     * Handles unlink requests for either the executing player or another player.
     *
     * @param player   The player who executed the command.
     * @param wikiUser The MediaWiki user to unlink from the player's UUID.
     *
     * @since 0.1.0
     */
    private static void handleUnlinkRequest( Player player, String wikiUser ) {
        if ( wikiUser == null ) {
            handleSelfUnlink( player );
            return;

        }

        if ( !player.hasPermission( "wikicraft.command.account.unlink.others" ) ) {
            player.sendMessage( WCMessages.message( "error", "You do not have permission to unlink other players." ) );
            return;

        }

        handleOtherUnlink( player, wikiUser );

    }

    /**
     * Handles the unlink request for the executing player.
     *
     * @param player The player who executed the command.
     *
     * @since 0.1.0
     */
    private static void handleSelfUnlink( @NotNull Player player ) {
        String currentLink = WCAccountBridge.getUUIDToWikiUserMap().get( player.getUniqueId() );

        if ( currentLink == null ) {
            player.sendMessage( WCMessages.message( "error", "You don't have any accounts to unlink!" ) );
            return;

        }

        WCAccountBridge.removeLinkWithUUID( player.getUniqueId() );
        WCPermissions.revokePermission( player.getUniqueId(), "wikicraft.pages.manage" );
        player.sendMessage( WCMessages.message( "info", "Unlinked your account from your MediaWiki account. You will be unable to manage pages or their content until you link your account again." ) );

    }

    /**
     * Handles the unlink request for another player.
     * @param player   The player who executed the command.
     * @param wikiUser The MediaWiki user to unlink from the player's UUID.
     *
     * @since 0.1.0
     */
    private static void handleOtherUnlink( @NotNull Player player, String wikiUser ) {
        UUID linkedUUID = WCAccountBridge.getWikiUserToUUIDMap().get( wikiUser );
        Player linkedPlayer = WCUtils.getPlayer( linkedUUID );

        if ( linkedUUID == null ) {
            player.sendMessage( WCMessages.message( "error", wikiUser + " is not linked to any accounts!" ) );
            return;

        }

        WCMessages.debug( "info", "Unlinking " + linkedPlayer.getName() + " from " + wikiUser + "..." );
        WCAccountBridge.removeLinkWithWikiUser( wikiUser );
        player.sendMessage( WCMessages.message( "info", "Successfully unlinked " + wikiUser + " from " + linkedPlayer.getName() + "!" ) );

    }

}

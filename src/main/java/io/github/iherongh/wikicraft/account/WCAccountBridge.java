package io.github.iherongh.wikicraft.account;

import com.google.gson.*;
import io.github.iherongh.wikicraft.WikiCraft;
import io.github.iherongh.wikicraft.messages.WCMessages;
import io.github.iherongh.wikicraft.utils.WCUtils;
import io.github.iherongh.wikicraft.wiki.WCWiki;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the bridge between Minecraft accounts and MediaWiki accounts.
 *
 * @see WCAccountBridge#addLink(UUID, String)
 * @see WCAccountBridge#hyphenateUUID(String)
 * @see WCAccountBridge#hyphenateUUID(String)
 * @see WCAccountBridge#getAccountData()
 * @see WCAccountBridge#getAccountPair(Map.Entry, String)
 * @see WCAccountBridge#getAccountsFile()
 * @see WCAccountBridge#getCachedData()
 * @see WCAccountBridge#getUUID(String)
 * @see WCAccountBridge#getUUIDToWikiUserMap()
 * @see WCAccountBridge#getWikiUser(UUID)
 * @see WCAccountBridge#getWikiUser(Player)
 * @see WCAccountBridge#getWikiUserToUUIDMap()
 * @see WCAccountBridge#instantiateAccountFile()
 * @see WCAccountBridge#loadAccountLinks()
 * @see WCAccountBridge#relinkAccount(UUID, String)
 * @see WCAccountBridge#removeLink(String, UUID)
 * @see WCAccountBridge#removeLinkWithUUID(UUID)
 * @see WCAccountBridge#removeLinkWithWikiUser(String)
 * @see WCAccountBridge#requestLink(UUID, String)
 * @see WCAccountBridge#setCachedData(JsonObject)
 * @see WCAccountBridge#updateAccountData(JsonObject)
 * @see WCAccountBridge#updateAccountData(JsonObject, UUID, String, String)
 * @see WCAccountBridge#UUIDOfWikiUser(String)
 * @see WCAccountBridge#wikiUserOfUUID(String)
 */
public class WCAccountBridge {

    /**
     * The file storing the account bridge.
     */
    private static final File accountsFile = new File( WikiCraft.getInstance().getDataFolder().getPath() + "/account_bridge.json" );

    /**
     * Maps a UUID to a MediaWiki account, or vice versa.
     */
    private static final Map<UUID, String> uuidToWikiUser = new HashMap<>();
    private static final Map<String, UUID> wikiUserToUUID = new HashMap<>();

    private static JsonObject cachedData = null;
    private static long lastCached = 0;
    private static final long CACHE_DURATION = 20000;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Gets the accounts file.
     * <p>This file is used to store the UUIDs of players and their corresponding MediaWiki accounts.
     * This is used to attribute any edits made by players to their respective accounts.
     *
     * @return The accounts file.
     *
     * @since 0.1.0
     */
    public static File getAccountsFile() {
        return accountsFile;

    }

    public static JsonObject getCachedData() {
        if ( cachedData == null || System.currentTimeMillis() - lastCached > CACHE_DURATION ) {
            try {
                if ( !getAccountsFile().exists() ) {
                    instantiateAccountFile();

                }

            } catch ( Exception e ) {
                WCMessages.debug( "warning", "Could not read account bridge file: " + e.getMessage() );

            }

        }

        return cachedData;

    }

    public static void setCachedData( JsonObject data ) {
        cachedData = data;
        lastCached = System.currentTimeMillis();

    }

    /**
     * Gets the map of UUIDs to MediaWiki accounts.
     *
     * @return The map of UUIDs to MediaWiki accounts.
     *
     * @since 0.1.0
     */
    public static Map<String, UUID> getWikiUserToUUIDMap() {
        return wikiUserToUUID;

    }

    /**
     * Gets the UUID of a linked MediaWiki account.
     *
     * @param wikiUser The MediaWiki account.
     * @return The UUID of the linked MediaWiki account.
     *
     * @since 0.1.0
     */
    public static String UUIDOfWikiUser( String wikiUser ) {
        return wikiUserToUUID.get( wikiUser ).toString();

    }

    /**
     * Gets the map of MediaWiki accounts to UUIDs.
     *
     * @return The map of MediaWiki accounts to UUIDs.
     *
     * @since 0.1.0
     */
    public static Map<UUID, String> getUUIDToWikiUserMap() {
        return uuidToWikiUser;

    }

    /**
     * Gets the map of MediaWiki accounts to UUIDs.
     *
     * @return The map of MediaWiki accounts to UUIDs.
     *
     * @since 0.2.0
     */
    public static String wikiUserOfUUID( String uuid ) {
        return uuidToWikiUser.get( UUID.fromString( uuid ) );

    }

    /**
     * Instantiates the account bridge file.
     * <p>This method is called when the plugin is enabled and creates the accounts file if it does not exist.
     */
    public static void instantiateAccountFile() {
        try {
            if ( !getAccountsFile().exists() ) {
                if ( getAccountsFile().createNewFile() ) {
                    WCMessages.debug( "info", "Created account bridge file." );

                    JsonObject data = new JsonObject();
                    data.add( "accounts", new JsonArray() );

                    // Update file and cache
                    updateAccountData( data );

                }

            }

        } catch ( Exception e ) {
            WCMessages.debug( "warning", "Could not create account bridge file: " + e.getMessage() );

        }

    }

    /**
     * Loads the account bridge file.
     *
     * @param data The data to load.
     *
     * @since 0.2.0
     */
    private static void updateAccountData( JsonObject data ) {
        try ( FileWriter writer = new FileWriter( getAccountsFile() ) ) {
            gson.toJson( data, writer );
            setCachedData( data );
            WCMessages.debug( "info", "Account data updated successfully" );

        } catch ( Exception e ) {
            WCMessages.throwError( e );

        }

    }

    /**
     * Updates the account bridge file.
     *
     * @param data The data to update.
     * @param uuid The UUID of the player.
     * @param wikiUsername The MediaWiki account of the player.
     * @param mode The mode to use.
     *
     * @since 0.2.0
     */
    private static void updateAccountData( JsonObject data, UUID uuid, String wikiUsername, @NotNull String mode ) {
        updateAccountData( data );

        switch ( mode.toLowerCase() ) {
            case "add" -> {
                getUUIDToWikiUserMap().put( uuid, wikiUsername );
                getWikiUserToUUIDMap().put( wikiUsername, uuid );

            }
            case "remove" -> {
                getUUIDToWikiUserMap().remove( uuid );
                getWikiUserToUUIDMap().remove( wikiUsername );

            }
            default -> WCMessages.debug( "warning", "Invalid mode: " + mode );

        }

    }
    /**
      * Loads accounts in the account bridge file to the plugin's account maps.
     *
     * @since 0.2.0
      */
    public static void loadAccountLinks() {
        try ( FileReader reader = new FileReader( getAccountsFile() ) ) {
            JsonObject data = gson.fromJson( reader, JsonObject.class );
            JsonArray accounts = data.getAsJsonArray( "accounts" );

            for ( JsonElement element : accounts ) {
                JsonObject account = element.getAsJsonObject();

                UUID uuid = UUID.fromString( account.get( "uuid" ).getAsString());
                String wikiUsername = account.get( "wiki_account" ).getAsString();
                boolean linked = account.get( "linked" ).getAsBoolean();

                if ( linked ) {
                    uuidToWikiUser.put( uuid, wikiUsername );
                    wikiUserToUUID.put( wikiUsername, uuid );
                    WCMessages.debug( "info", "Loaded account link: " + wikiUsername + " <-> " + WCUtils.getPlayer( uuid ).getName() );

                }

            }

            WCMessages.debug( "info", "Loaded " + uuidToWikiUser.size() + " account link(s)" );

        } catch ( Exception e ) {
            WCMessages.debug("error", "Failed to load account links: " + e.getMessage() );

        }

    }
    /**
     * Gets the UUID associated with a MediaWiki account.
     *
     * @param wikiUsername The MediaWiki account to get the UUID of.
     * @return The UUID associated with the MediaWiki account.
     *
     * @since 0.2.0
     */
    public static @Nullable String getUUID( String wikiUsername ) {
        try ( FileReader reader = new FileReader( getAccountsFile() ) ) {
            JsonObject data = gson.fromJson( reader, JsonObject.class );
            JsonArray accounts = data.getAsJsonArray( "accounts" );

            for ( JsonElement element : accounts ) {
                JsonObject account = element.getAsJsonObject();

                if ( account.get( "wiki_account" ).getAsString().equals( wikiUsername ) && account.get( "linked" ).getAsBoolean()) {
                    String uuid = account.get( "uuid" ).getAsString();
                    WCMessages.debug( "info", wikiUsername + " found linked to " + WCUtils.getPlayer( uuid ).getName() + " (UUID: " + uuid + ") ." );
                    return uuid;

                }

            }

        } catch ( Exception e ) {
            WCMessages.throwError( e );

        }

        return null;

    }

    /**
     * Gets the MediaWiki account associated with a UUID.
     *
     * @param player The player to get the MediaWiki account of.
     * @return The MediaWiki account associated with the UUID.
     *
     * @since 0.1.0
     */
    public static String getWikiUser( @NotNull Player player ) {
        return getWikiUser( player.getUniqueId() );

    }

    /**
     * Gets the MediaWiki account associated with a UUID.
     *
     * @param uuid The UUID to get the MediaWiki account of.
     * @return The MediaWiki account associated with the UUID.
     *
     * @since 0.1.0
     */
    public static @Nullable String getWikiUser( UUID uuid ) {
        try ( FileReader reader = new FileReader( getAccountsFile() ) ) {
            JsonObject data = gson.fromJson( reader, JsonObject.class );
            JsonArray accounts = data.getAsJsonArray( "accounts" );

            for ( JsonElement element : accounts ) {
                JsonObject account = element.getAsJsonObject();
                if ( account.get( "uuid" ).getAsString().equals( uuid.toString() ) && account.get( "linked" ).getAsBoolean() ) {
                    String wikiUsername = account.get( "wiki_account" ).getAsString();
                    WCMessages.debug( "info", uuid + " found linked to wiki user " + wikiUsername + "." );
                    return wikiUsername;

                }

            }

        } catch ( Exception e ) {
            WCMessages.throwError( e );

        }

        return null;

    }

    /**
     * Requests a link between a player and a MediaWiki account.
     * <p>Fails if:
     * <ul>
     *     <li>The player is already linked to a MediaWiki account.
     *     <li>The URL for the player's WikiCraft subpage does not exist.
     *     <li>The user's WikiCraft subpage does not contain their UUID.
     * </ul>
     *
     * @param uuid The UUID of the player to link.
     * @param wikiUsername The MediaWiki account to link to.
     * @return <ul>
     *
     * @since 0.1.0
     *             <li>{@code mediawiki_account_already_linked} if the MediaWiki account is already linked to a player.
     *             <li>{@code uuid_already_linked} if the UUID is already linked to a MediaWiki account.
     *             <li>{@code user_subpage_not_found} if the player's WikiCraft subpage does not exist.
     *             <li>{@code user_subpage_creator_mismatch} if the player's WikiCraft subpage creator does not match the player's UUID.
     *             <li>{@code uuid_not_found} if the player's UUID is not found in their WikiCraft subpage.
     *             <li>{@code success} if the link was successful.
     *             <li>{@code add_link_failed} if the link could not be added.
     *             <li>{@code null} if an unknown error occurs.
     *         </ul>
     */
    public static @NotNull String requestLink( UUID uuid, String wikiUsername ) {
        JsonObject data = getAccountData();
        JsonArray accounts = data.getAsJsonArray( "accounts" );

        for ( JsonElement element : accounts ) {
            JsonObject account = element.getAsJsonObject();
            if ( !account.get( "linked" ).getAsBoolean() ) {
                continue;

            }

            if ( account.get( "uuid" ).getAsString().equals( uuid.toString() ) ) {
                WCMessages.debug( "warning", WCUtils.getPlayer( uuid ).getName() + " is already linked to a wiki account!" );
                return "uuid_already_linked";

            }

            if ( account.get( "wiki_account" ).getAsString().equals( wikiUsername ) ) {
                WCMessages.debug( "warning", wikiUsername + " is already linked to a player!" );
                return "mediawiki_account_already_linked";

            }

        }

        String wikiUserURL = "User:" + wikiUsername + "/WikiCraft";

        // Check if the user's WikiCraft subpage exists
        if ( !WCWiki.getWiki().exists( wikiUserURL ) ) {
            WCMessages.debug( "warning", "Could not find wiki page for " + wikiUsername + "!" );
            return "user_subpage_not_found";

        }

        if ( WCWiki.getWiki().getPageCreator( wikiUserURL ).equals( wikiUserOfUUID( uuid.toString() ) ) ) {
            WCMessages.debug( "warning", "Page creator does not match UUID for " + wikiUsername + "." );
            return "user_subpage_creator_mismatch";

        }

        // Check if the UUID is present in the page content
        String content = WCWiki.getWiki().getPageText( wikiUserURL );

        if ( content == null || !content.contains( uuid.toString() ) ) {
            WCMessages.debug( "warning", "Could not find UUID for " + wikiUsername + " on page " + WCUtils.userURL( wikiUsername, "WikiCraft" ) + "!" );
            return "uuid_not_found";

        }

        // Proceed with the link request
        WCMessages.debug( "info", "Found UUID for " + wikiUsername + ": " + uuid );
        return addLink( uuid, wikiUsername ) ? "success" : "add_link_failed";

    }

    /**
     * Adds a link between a MediaWiki account and a UUID.
     * <p>Checks if either the UUID or the MediaWiki username exists in the account bridge before verifying the link.
     *
     * @param uuid The UUID to link.
     * @param username The MediaWiki account to link.
     * @return {@code true} if the link was added, {@code false} otherwise.
     *
     * @since 0.1.0
     */
    public static boolean addLink( UUID uuid, String username ) {
        WCMessages.debug( "info", "Starting link request for " + username + "..." );

        try ( FileReader reader = new FileReader( getAccountsFile() ) ) {
            JsonObject data = gson.fromJson( reader, JsonObject.class );
            JsonArray accounts = data.getAsJsonArray( "accounts" );

            // Check for existing links
            for ( JsonElement element : accounts ) {
                JsonObject account = element.getAsJsonObject();
                String existingUUID = account.get( "uuid" ).getAsString();
                String existingUsername = account.get( "wiki_account" ).getAsString();

                if ( existingUUID.equals( uuid.toString() ) || existingUsername.equals( username ) ) {
                    account.addProperty( "linked", true );
                    account.addProperty( "last_link", System.currentTimeMillis() );
                    updateAccountData( data, uuid, account.get( "wiki_account" ).getAsString(), "add" );
                    return true;

                }


            }

            // Create new link
            JsonObject newAccount = new JsonObject();

            newAccount.addProperty( "uuid", uuid.toString() );
            newAccount.addProperty( "wiki_account", username );
            newAccount.addProperty( "linked", true );
            newAccount.addProperty( "last_link", System.currentTimeMillis() );
            newAccount.addProperty( "last_edit", 0 );

            accounts.add( newAccount );
            updateAccountData( data );

            getUUIDToWikiUserMap().put(uuid, username);
            getWikiUserToUUIDMap().put(username, uuid);

            return true;

        } catch ( Exception e ) {
            WCMessages.throwError( e );
            return false;

        }

    }

    /**
     * Removes a link between a MediaWiki account and a UUID.
     *
     * @param uuid The UUID to remove the link from.
     */
    public static void removeLinkWithUUID( @NotNull UUID uuid ) {
        try {
            Player player = WCUtils.getPlayer( uuid );
            String wikiUser = getUUIDToWikiUserMap().get( uuid );

            if ( player == null ) {
                WCMessages.debug( "error", "No player with UUID " + uuid + " exists!" );
                return;

            }

            if ( wikiUser == null ) {
                WCMessages.debug( "error", "Could not find wiki user for " + player.getName() + "!" );
                return;

            }

            WCMessages.debug( "info", "Removing link for " + player.getName() + "..." );
            removeLink( wikiUser, uuid );

        } catch ( Exception e ) {
            WCMessages.throwError( e );

        }

    }

    /**
     * Removes a link between a MediaWiki account and a UUID.
     *
     * @param wikiUser The MediaWiki account to remove the link from.
     */
    public static void removeLinkWithWikiUser( @NotNull String wikiUser ) {
        try {
            UUID uuid = getWikiUserToUUIDMap().get( wikiUser );
            if ( uuid == null ) {
                WCMessages.debug( "error", "Could not find UUID for " + wikiUser + "!" );
                return;

            }

            WCMessages.debug( "info", "Removing link for " + wikiUser + "..." );
            removeLink( wikiUser, uuid );

        } catch ( Exception e ) {
            WCMessages.throwError( e );

        }

    }

    /**
     * Removes a link between a MediaWiki account and a UUID.
     *
     * @param wikiUser The MediaWiki account to remove the link from.
     * @param uuid The UUID to remove the link from.
     */
    private static void removeLink( @NotNull String wikiUser, UUID uuid ) {
        WCMessages.debug( "info", "Removing link between " + uuid + " and " + wikiUser + "..." );

        try ( FileReader reader = new FileReader( getAccountsFile() ) ) {
            JsonObject data = gson.fromJson( reader, JsonObject.class );
            JsonArray accounts = data.getAsJsonArray( "accounts" );
            JsonArray newAccounts = new JsonArray();

            for ( JsonElement element : accounts ) {
                JsonObject account = element.getAsJsonObject();

                WCMessages.debug( "info", "Checking account " + account.get( "wiki_account" ).getAsString() + "..." );
                if ( !account.get( "wiki_account" ).getAsString().equals( wikiUser ) ) {
                    newAccounts.add( account );

                } else {
                    WCMessages.debug( "info", "Removing link for " + account.get( "wiki_account" ).getAsString() + "..." );
                    account.addProperty( "linked", false );
                    newAccounts.add( account );

                }

            }

            data.add( "accounts", newAccounts );

            WCMessages.debug( "info", "Writing new accounts file..." );
            updateAccountData( data, uuid, wikiUser, "remove" );

            WCMessages.debug( "info", "Unlinked " + wikiUser + " and " + WCUtils.getPlayer( uuid ).getName() + "." );

        } catch ( Exception e ) {
            WCMessages.throwError( e );

        }

    }

    /**
     * Adds hyphens to a UUID.
     *
     * @param uuid The UUID as a string to add hyphens to.
     * @return The UUID with hyphens.
     *
     * @since 0.1.0
     */
    public static @NotNull String hyphenateUUID( @NotNull String uuid ) {
        return String.format( "%s-%s-%s-%s-%s",
            uuid.substring( 0, 8 ),
            uuid.substring( 8, 12 ),
            uuid.substring( 12, 16 ),
            uuid.substring( 16, 20 ),
            uuid.substring( 20, 32 )

        );

    }

    public static @NotNull Component getAccountPair( Map.@NotNull Entry<UUID, String> entry, String mcUsername ) {
        String wikiUsername = entry.getValue();
        String wikiUrl = WCUtils.userURL( wikiUsername );

        return Component.text( mcUsername + " <-> " )
            .append( Component.text( wikiUsername )
                .clickEvent( ClickEvent.openUrl( wikiUrl ) )
                .hoverEvent( HoverEvent.showText( Component.text( "Click to open " + wikiUsername + "'s profile!" ) ) )

            );

    }

    public static JsonObject getAccountData() {
        if ( cachedData == null || System.currentTimeMillis() - lastCached > CACHE_DURATION ) {
            try ( FileReader reader = new FileReader( getAccountsFile() ) ) {
                cachedData = gson.fromJson( reader, JsonObject.class );
                lastCached = System.currentTimeMillis();
                WCMessages.debug( "info", "Account cache refreshed." );

            } catch ( Exception e ) {
                WCMessages.throwError( e );

            }

        }

        return cachedData;

    }

    public static @NotNull String relinkAccount( UUID uuid, String wikiUser ) {

        // Attempt to re-link an existing account bridge
        WCMessages.debug( "info", "Attempting to re-link account..." );

        try ( FileReader reader = new FileReader( getAccountsFile() ) ) {
            JsonObject data = gson.fromJson( reader, JsonObject.class );
            JsonArray accounts = data.getAsJsonArray( "accounts" );

            WCMessages.debug( "info", "Found " + accounts.size() + " account(s)" );

            for ( JsonElement element : accounts ) {
                JsonObject account = element.getAsJsonObject();

                WCMessages.debug( "info", "Checking account: " + account.get( "wiki_account" ).getAsString() );
                if ( account.get( "uuid" ).getAsString().equals( uuid.toString() ) ) {
                    if ( account.get( "linked" ).getAsBoolean() ) {
                        WCMessages.debug( "info", "Account already linked." );
                        return "bridge_already_linked";

                    }

                    WCMessages.debug( "info", "Re-linking " + WCUtils.getPlayer( uuid ).getName() + " to " + account.get( "wiki_account" ).getAsString() + "..." );

                    account.addProperty( "linked", true );
                    account.addProperty( "last_link", System.currentTimeMillis() );

                    updateAccountData( data, uuid, account.get( "wiki_account" ).getAsString(), "add" );

                    return "success";

                }

            }

            return "no_account";

        } catch ( Exception e ) {
            WCMessages.throwError( e );
            return "error";

        }

    }

}

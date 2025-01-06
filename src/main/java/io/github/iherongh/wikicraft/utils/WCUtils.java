package io.github.iherongh.wikicraft.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.iherongh.wikicraft.config.WCConfigUtils;
import io.github.iherongh.wikicraft.messages.WCMessages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Contains utility methods for the plugin.
 *
 * @author iHeronGH
 *
 * @version 0.2.0
 *
 * @since 0.1.0
 */
public class WCUtils {

    /**
     * Constructs a new {@code WCUtils} object.
     */
    public WCUtils() {}

    /**
     * A UUID-to-long map that tracks when a given account last made a request to the wiki.
     * <p>This is used in conjunction with methods that make requests to the wiki and is
     * purposed with ensuring no single user can flood requests.
     */
    private static final Map<UUID, Long> lastRequestTime = new HashMap<>();

    /**
     * Gets a player object from a player name, regardless of whether they are online or not.
     *
     * @param playerName The name of the player to get.
     *
     * @return The player object representation of the player's name.
     */
    public static Player getPlayer( String playerName ) {
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            if ( player.getName().equals( playerName ) ) {
                return player;

            }

        }

        return Bukkit.getOfflinePlayer( playerName ).getPlayer();

    }

    /**
     * Gets a player object from a UUID, regardless of whether they are online or not.
     *
     * @param uuid The UUID of the player to get.
     *
     * @return The player object representation of the player's UUID.
     */
    public static Player getPlayer( UUID uuid ) {
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            if ( player.getUniqueId().equals( uuid ) ) {
                return player;

            }

        }

        return Bukkit.getOfflinePlayer( uuid.toString() ).getPlayer();

    }

    /**
     * Creates a URL for a user page on the wiki.
     *
     * @param user The user to create a URL for.
     *
     * @return The URL for the user's page.
     */
    public static @NotNull String userURL( String user ) {
        return "https://" + WCConfigUtils.getWikiURL() + "/wiki/User:" + user;

    }

    /**
     * Creates a URL for a user subpage on the wiki.
     *
     * @param user The user to create a URL for.
     * @param subpage The subpage to create a URL for.
     *
     * @return The URL for the user's subpage.
     */
    public static @NotNull String userURL( String user, String subpage ) {
        return userURL( user ) + "/" + subpage;

    }

    /**
     * Creates a URL for a page on the wiki.
     *
     * @param page The page to create a URL for.
     *
     * @return The URL for the page.
     */
    public static @NotNull String pageURL( String page ) {
        return "https://" + WCConfigUtils.getWikiURL() + "/wiki/" + page;

    }

    /**
     * Creates a URL for a page on the wiki.
     *
     * @param page The page to create a URL for.
     * @param section The section to create a URL for.
     *
     * @return The URL for the page.
     */
    public static @NotNull String pageURL( String page, String section ) {
        return pageURL( page ) + "#" + section;

    }

    /**
     * Creates a URL for a page on the wiki.
     *
     * @param page The page to create a URL for.
     * @param section The section to create a URL for.
     * @param anchor The anchor to create a URL for.
     *
     * @return The URL for the page.
     */
    public static @NotNull String pageURL( String page, String section, String anchor ) {
        return pageURL( page, section ) + ":" + anchor;

    }

    /**
     * Creates a URL for a page on the wiki.
     *
     * @param page The page to create a URL for.
     * @param section The section to create a URL for.
     * @param anchor The anchor to create a URL for.
     * @param query The query to create a URL for.
     *
     * @return The URL for the page.
     */
    public static @NotNull String pageURL( String page, String section, String anchor, String query ) {
        return pageURL( page, section, anchor ) + "?" + query;

    }

    /**
     * Gets the last time a player sent a request to the wiki.
     *
     * @return The last time a player sent a request to the wiki.
     *
     * @since 0.1.0
     */
    public static Map<UUID, Long> getLastRequestTimeMap() {
        return lastRequestTime;

    }

    /**
     * Sets the last time a player sent a request to the wiki to the current time.
     *
     * @param player The player to set the last request time for.
     */
    public static void setLastRequestTime( @NotNull Player player ) {
        long now = System.currentTimeMillis();
        WCMessages.debug( "info", "Updating last request time for " + player.getName() + "..." );
        setLastRequestTime( player.getUniqueId(), now );
        WCMessages.debug( "debug", "Set last request time for " + player.getName() + " to " + now + " ms." );

    }

    /**
     * Sets the last time a player sent a request to the wiki.
     *
     * @param uuid The UUID of the player.
     * @param time The time the request was sent.
     */
    public static void setLastRequestTime( UUID uuid, long time ) {
        lastRequestTime.put( uuid, time );

    }

    /**
     * Gets the minimum time between requests to the wiki.
     *
     * @param uuid The UUID of a player.
     * @return True if the player is on cooldown, false otherwise.
     *
     * @since 0.1.0
     */
    public static boolean isOnRequestCooldown( UUID uuid ) {
        long now = System.currentTimeMillis();
        long lastRequestTime = getLastRequestTimeMap().getOrDefault( uuid, 0L );

        if ( lastRequestTime == 0L ) {
            return false;

        }

        return ( now - lastRequestTime < WCConfigUtils.getMinRequestTime() * 1000L );

    }

    /**
     * Checks if the player is on wiki request cooldown.
     *
     * @param player The player to check.
     * @return True if the player is on cooldown, false otherwise.
     *
     * @since 0.1.0
     */
    public static boolean isOnRequestCooldown( @NotNull Player player ) {
        return isOnRequestCooldown( player.getUniqueId() );

    }

    /**
     * Converts a JSON object to a map.
     *
     * @param json The JSON object to convert.
     *
     * @return The map representation of the JSON object.
     */
    public static @NotNull Map<String, Object> convertJsonToMap( @NotNull JsonObject json ) {
        Map<String, Object> map = new HashMap<>();

        for ( Map.Entry<String, JsonElement> entry : json.entrySet() ) {
            if ( entry.getValue().isJsonObject() ) {
                map.put( entry.getKey(), convertJsonToMap( entry.getValue().getAsJsonObject() ) );

            } else {
                map.put( entry.getKey(), entry.getValue().getAsString() );

            }

        }

        return map;

    }

    /**
     * Hyperlinks a string.
     *
     * @param text The text to hyperlink.
     *
     * @return The hyperlinked text.
     */
    public static @NotNull Component hyperlink( String text ) {
        return hyperlink( text, text );

    }

    /**
     * Hyperlinks a string.
     *
     * @param text The text to hyperlink.
     * @param url The URL to link to.
     *
     * @return The hyperlinked text.
     */
    public static @NotNull Component hyperlink( String text, String url ) {
        return hyperlink( text, url, null );

    }

    /**
     * Hyperlinks a string.
     *
     * @param text The text to hyperlink.
     * @param url The URL to link to.
     * @param hoverText The text to display when hovering over the link.
     *
     * @return The hyperlinked text.
     */
    public static @NotNull Component hyperlink( String text, String url, String hoverText ) {
        return hyperlink( text, url, hoverText, true );

    }

    /**
     * Hyperlinks a string.
     *
     * @param text The text to hyperlink.
     * @param url The URL to link to.
     * @param hoverText The text to display when hovering over the link.
     * @param underlined Whether the link should be underlined or not.
     *
     * @return The hyperlinked text.
     */
    public static @NotNull Component hyperlink( String text, String url, String hoverText, boolean underlined ) {
        if ( hoverText == null ) {
            if ( !underlined ) {
                return Component.text( text )
                    .clickEvent( ClickEvent.openUrl( url ) );

            }

            return Component.text( text )
                .clickEvent( ClickEvent.openUrl( url ) )
                .decorate( TextDecoration.UNDERLINED );
        }


        if ( !underlined ) {
            return Component.text( text )
                .clickEvent( ClickEvent.openUrl( url ) )
                .hoverEvent( HoverEvent.showText( Component.text( hoverText ) ) );

        }

        return Component.text( text )
            .decorate( TextDecoration.UNDERLINED )
            .clickEvent( ClickEvent.openUrl( url ) )
            .hoverEvent( HoverEvent.showText( Component.text( hoverText ) ) );

    }

}

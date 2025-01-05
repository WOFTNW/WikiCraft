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

public class WCUtils {

    /**
     * A UUID-to-long map that tracks when a given account last made a request to the wiki.
     * <p>This is used in conjunction with methods that make requests to the wiki and is
     * purposed with ensuring no single user can flood requests.
     */
    private static final Map<UUID, Long> lastRequestTime = new HashMap<>();

    public static Player getPlayer( String playerName ) {
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            if ( player.getName().equals( playerName ) ) {
                return player;

            }

        }

        return Bukkit.getOfflinePlayer( playerName ).getPlayer();

    }

    public static Player getPlayer( UUID uuid ) {
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            if ( player.getUniqueId().equals( uuid ) ) {
                return player;

            }

        }

        return Bukkit.getOfflinePlayer( uuid.toString() ).getPlayer();

    }

    public static @NotNull String userURL( String user ) {
        return "https://" + WCConfigUtils.getWikiURL() + "/wiki/User:" + user;

    }

    public static @NotNull String userURL( String user, String subpage ) {
        return userURL( user ) + "/" + subpage;

    }

    public static @NotNull String pageURL( String page ) {
        return "https://" + WCConfigUtils.getWikiURL() + "/wiki/" + page;

    }

    public static @NotNull String pageURL( String page, String section ) {
        return pageURL( page ) + "#" + section;

    }

    public static @NotNull String pageURL( String page, String section, String anchor ) {
        return pageURL( page, section ) + ":" + anchor;

    }

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
     * @param uuid The UUID of the player.
     */
    public static void setLastRequestTime( Player player ) {
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

    public static @NotNull Component hyperlink( String text ) {
        return hyperlink( text, text );

    }

    public static @NotNull Component hyperlink( String text, String url ) {
        return hyperlink( text, url, null );

    }

    public static @NotNull Component hyperlink( String text, String url, String hoverText ) {
        return hyperlink( text, url, hoverText, true );

    }

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

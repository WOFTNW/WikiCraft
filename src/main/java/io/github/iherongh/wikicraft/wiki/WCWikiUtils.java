package io.github.iherongh.wikicraft.wiki;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.fastily.jwiki.core.NS;
import io.github.iherongh.wikicraft.WikiCraft;
import io.github.iherongh.wikicraft.account.WCAccountBridge;
import io.github.iherongh.wikicraft.messages.WCMessages;
import io.github.iherongh.wikicraft.utils.WCUtils;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utility methods for wiki operations.
 *
 * @see WCWikiUtils#accountExists(String)
 * @see WCWikiUtils#getAllAccounts()
 * @see WCWikiUtils#getAllPages()
 * @see WCWikiUtils#getAllPages(String)
 * @see WCWikiUtils#getCacheTime()
 * @see WCWikiUtils#getHeader()
 * @see WCWikiUtils#getLastRequest()
 * @see WCWikiUtils#getMaxLag()
 * @see WCWikiUtils#getPageCache()
 * @see WCWikiUtils#getPageInfo(String)
 * @see WCWikiUtils#getResultCount(String)
 * @see WCWikiUtils#getUserCache()
 * @see WCWikiUtils#searchWiki(String)
 */
public class WCWikiUtils {

    /**
     * Constructs a new {@code WCWikiUtils} object.
     */
    public WCWikiUtils() {}

    /**
     * MediaWiki convention requires a maximum of 5 seconds for a request to be processed.
     * <br>Do not increase this value beyond 5!
     * <br>
     * <br>View more info here: <a href="https://www.mediawiki.org/wiki/Manual:Creating_a_bot#Bot_best_practices">Manual: Creating a bot</a>.
     */
    private static final int MAXLAG = 5;

    /**
     * MediaWiki convention requires a header to be sent with each request.
     * <br>Without this header, unsafe requests, or even the bot itself, may be blocked.
     * <br>
     * <br>View more info here: <a href="https://foundation.wikimedia.org/wiki/Policy:Wikimedia_Foundation_User-Agent_Policy">Policy: Wikimedia Foundation User-Agent Policy</a>.
     */
    private static final String HEADER = "WikiCraftBot/" + WikiCraft.getVersion() + " (https://github.com/iherongh/WikiCraft; heron@woftnw.org)";

    /**
     * To reduce the amount of requests made to the wiki, caches are used to store the results of previous requests.
     */
    private static ArrayList<String> pageCache = new ArrayList<>();
    private static ArrayList<String> userCache = new ArrayList<>();

    /**
     * The last time a request was made.
     */
    private static long lastRequest = 0;

    /**
     * The time in milliseconds that a request can be cached for.
     * <br>Currently set to 10 seconds in milliseconds.
     */
    private static final long CACHE_TIME = 10000;

    /**
     * Get the maximum lag allowed for a request to be processed.
     *
     * @return The maximum lag allowed for a request to be processed.
     *
     * @since 0.1.0
     */
    static int getMaxLag() {
        return MAXLAG;

    }

    /**
     * Get the header to be sent with each request.
     *
     * @return The header to be sent with each request.
     *
     * @since 0.1.0
     */
    public static String getHeader() {
        WCMessages.debug( "debug", "Getting header..." );
        return HEADER;

    }

    /**
     * Get the cache of pages that have been requested.
     *
     * @return The cache of pages that have been requested.
     *
     * @since 0.1.0
     */
    public static ArrayList<String> getPageCache() {
        long now = System.currentTimeMillis();

        if ( pageCache.isEmpty() || now - getLastRequest() > getCacheTime() ) {
            WCMessages.debug( "info", "Page cache is empty or cache time has expired. Updating cache..." );
            pageCache = getAllPages();
            lastRequest = now;
            WCMessages.debug( "debug", "Page cache updated with " + pageCache.size() + " entries." );

        } else {
            WCMessages.debug( "debug", "Using existing page cache..." );

        }

        return pageCache;

    }

    /**
     * Get the cache of users that have been requested.
     *
     * @return The cache of users that have been requested.
     *
     * @since 0.1.0
     */
    public static @NotNull JsonArray getUserCache() {
        long now = System.currentTimeMillis();
        JsonArray users = new JsonArray();

        if ( userCache.isEmpty() || now - getLastRequest() > getCacheTime() ) {
            WCMessages.debug( "info", "User cache is empty or cache time has expired. Updating cache..." );
            userCache = getAllPages("user");
            lastRequest = now;
            WCMessages.debug("info", "User cache updated with " + userCache.size() + " entries." );

        }

        for ( String userName : userCache ) {
            JsonObject user = new JsonObject();
            boolean isLinked = WCAccountBridge.getWikiUserToUUIDMap().containsKey( userName );
            user.addProperty( "name", userName );
            user.addProperty( "linked", isLinked );
            users.add( user );

            WCMessages.debug( "debug", "Added user " + userName + " (linked: " + isLinked + ") to cache." );

        }

        return users;

    }

    /**
     * Get the last time a request was made.
     *
     * @return The last time a request was made.
     *
     * @since 0.1.0
     */
    static long getLastRequest() {
        return lastRequest;

    }

    /**
     * Get the time in milliseconds that a request can be cached for.
     *
     * @return The time in milliseconds that a request can be cached for.
     *
     * @since 0.1.0
     */
    static long getCacheTime() {
        return CACHE_TIME;

    }

    /**
     * Checks if the given account exists on the wiki.
     *
     * @param name The name of the account to check.
     * @return {@code true} if the account exists, {@code false} otherwise.
     *
     * @since 0.1.0
     */
    public static boolean accountExists( String name ) {
        try {
            WCMessages.debug( "info", "Checking if " + name + " exists..." );
            ArrayList<String> users = getAllAccounts();

            if ( users == null || users.isEmpty() ) {
                WCMessages.debug( "warning", "No accounts found!" );
                return false;

            }

            for ( String user : users ) {
                WCMessages.debug( "info", "Checking account: " + user );

                if ( user.equals( name ) ) {
                    WCMessages.debug( "info", "Account found: " + WCUtils.userURL( user ) );
                    return true;

                }

            }

            WCMessages.debug( "warning", name + " not found!" );
            return false;

        } catch ( Exception e ) {
            WCMessages.debug( "severe", "Unable to check if account exists: " + e.getMessage() );
            return true;

        }

    }

    /**
     * Gets a list of all accounts in the configured wiki.
     *
     * @return A list of all accounts in the configured wiki.
     *
     * @since 0.1.0
     */
    private static @Nullable ArrayList<String> getAllAccounts() {
        try {
            WCMessages.debug( "info", "Getting all MediaWiki accounts..." );

            ResponseBody response = WCWiki.getWiki().basicGET(
                "query",
                "formatversion", "2",
                "list", "allusers",
                "aulimit", "max",
                "maxlag", String.valueOf( getMaxLag() )
            ).body();

            if ( response == null ) {
                WCMessages.debug( "warning", "Wiki API returned no response." );
                return null;

            }

            ArrayList<String> list = new ArrayList<>();
            JsonElement json = JsonParser.parseString( response.string() )
                .getAsJsonObject()
                .getAsJsonObject( "query" )
                .getAsJsonArray( "allusers" );

            response.close();

            for ( JsonElement element : json.getAsJsonArray() ) {
                list.add( element.getAsJsonObject().get( "name" ).getAsString() );

            }

            WCMessages.debug( "info", "Found " + list.size() + " accounts." );

            return list;

        } catch ( Exception e ) {
            WCMessages.debug( "severe", "Failed to retrieve accounts: " + e.getMessage() );
            return null;

        }

    }

    /**
     * Search the wiki for a given query.
     *
     * @param query The query to search for.
     * @return A list of all pages that match the query.
     *
     * @since 0.1.0
     */
    public static @NotNull ArrayList<String> searchWiki( String query ) {
        try {
            WCMessages.debug( "info", "Searching wiki for: " + query );
            ArrayList<String> results = WCWiki.getWiki().search( query, 5000 );

            if ( results.isEmpty() ) {
                WCMessages.debug( "info", "No results found for query: " + query );

            } else {
                WCMessages.debug( "info", "Found " + results.size() + " results for: " + query );

            }

            return results;

        } catch ( Exception e ) {
            WCMessages.debug( "error", "Wiki search failed: " + e.getMessage() );
            return new ArrayList<>();

        }

    }

    /**
     * Get the result count for a given query.
     *
     * @param query The query to search for.
     * @return The result count for the given query.
     *
     * @since 0.1.0
     */
    public static int getResultCount( String query ) {
        WCMessages.debug( "info", "Getting result count for: " + query );
        int count = searchWiki( query ).size();
        WCMessages.debug( "info", "Found " + count + " results for query " + query );
        return count;

    }

    /**
     * Get all pages in the main namespace.
     *
     * @return A list of all pages in the main namespace.
     *
     * @since 0.1.0
     */
    public static @NotNull ArrayList<String> getAllPages() {
        return getAllPages( "main" );

    }

    /**
     * Get all pages in a specified namespace
     *
     * @param namespace The namespace to get pages from.
     *                  <p>Expected values are:
     *                  <ul>
     *                      <li>{@code category}
     *                      <li>{@code file}
     *                      <li>{@code help}
     *                      <li>{@code talk}
     *                      <li>{@code template}
     *                      <li>{@code user}
     *                  </ul>
     * @return A list of all pages in the specified namespace.
     *
     * @since 0.1.0
     */
    public static @NotNull ArrayList<String> getAllPages( @NotNull String namespace ) {
        WCMessages.debug( "info", "Getting all pages in " + namespace );

        ArrayList<String> pages = switch ( namespace.toLowerCase() ) {
            case "category" -> {
                WCMessages.debug( "info", "Getting category pages..." );
                yield WCWiki.getWiki().allPages( "", false, false, 5000, NS.CATEGORY );

            }
            case "file" -> {
                WCMessages.debug( "info", "Getting file pages..." );
                yield WCWiki.getWiki().allPages( "", false, false, 5000, NS.FILE );

            }
            case "help" -> {
                WCMessages.debug( "info", "Getting help pages..." );
                yield WCWiki.getWiki().allPages( "", false, false, 5000, NS.HELP );

            }
            case "talk" -> {
                WCMessages.debug( "info", "Getting talk pages..." );
                yield WCWiki.getWiki().allPages( "", false, false, 5000, NS.TALK );

            }
            case "template" -> {
                WCMessages.debug( "info", "Getting template pages..." );
                yield WCWiki.getWiki().allPages( "", false, false, 5000, NS.TEMPLATE );

            }
            case "user" -> {
                WCMessages.debug( "info", "Getting user pages..." );
                yield WCWiki.getWiki().allPages( "", false, false, 5000, NS.USER );

            }
            default -> {
                WCMessages.debug( "info", "Getting main pages..." );
                yield WCWiki.getWiki().allPages( "", false, false, 5000, NS.MAIN );

            }

        };

        WCMessages.debug( "info", "Found " + pages.size() + " pages in " + namespace );
        return pages;

    }

    /**
     * Retrieves information about a specific wiki page.
     *
     * @param page The title or name of the wiki page to retrieve information for.
     * @return A HashMap containing key-value pairs of page information. Currently returns an empty HashMap.
     *
     * @since 0.1.0
     */
    @Contract ( pure = true )
    public static @NotNull HashMap<String, String> getPageInfo( String page ) {
        HashMap<String, String> info = new HashMap<>();

        try {
            WCMessages.debug( "info", "Getting info for " + page );

            if ( !WCWiki.getWiki().exists( page ) ) {
                WCMessages.debug( "error", "Page " + page + " does not exist." );
                return info;

            }

            info.put( "title", page );
            info.put( "url", WCUtils.pageURL( page ) );
            info.put( "categories", String.join( ", ", WCWiki.getWiki().getCategoriesOnPage( page ) ) );
            info.put( "last_editor", WCWiki.getWiki().getLastEditor( page ) );

        } catch ( Exception e ) {
            WCMessages.debug( "error", "Failed to get info for " + page + ": " + e.getMessage() );

        }

        return info;

    }

    /**
     * Gets a valid token for the wiki.
     *
     * @return A valid token for the wiki.
     */
    public static @Nullable String getToken() {
        try {
            WCMessages.debug( "info", "Getting CSRF token..." );

            Response tokenResponse = WCWiki.getWiki().basicGET(
                "query",
                "meta", "tokens",
                "type", "csrf"
            );

            String token = JsonParser.parseString( tokenResponse.body().string() )
                .getAsJsonObject()
                .getAsJsonObject( "query" )
                .getAsJsonObject( "tokens" )
                .get( "csrftoken" )
                .getAsString();

            return token;

        } catch ( Exception e ) {
            WCMessages.debug( "error", "Failed to get CSRF token: " + e.getMessage() );
            return null;

        }

    }

}

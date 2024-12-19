package io.github.iherongh.wikicraft.wiki;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.fastily.jwiki.core.Wiki;
import io.github.iherongh.wikicraft.config.WCConfigUtils;
import io.github.iherongh.wikicraft.messages.WCMessages;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.util.HashMap;

public class WCWiki {

    private static Wiki wiki;

    /**
     * Retrieves the {@link Wiki} instance.
     *
     * @return The {@link Wiki} instance, or null if it hasn't been generated yet.
     */
    public static Wiki getWiki() {
        if ( wiki == null ) {
            WCMessages.debug( "info", "Wiki has not been generated! The value of wiki will return as null." );
            
        }
        
        return wiki;

    }
    
    /**
     * Retrieves information about a specific wiki page.
     *
     * @TODO Populate map with page info such as title, categories, outgoing links, contributors, etc.
     *
     * @param page The title or name of the wiki page to retrieve information for.
     * @return A HashMap containing key-value pairs of page information. Currently returns an empty HashMap.
     */
    public static HashMap<String, String> getPageInfo( String page ) {
        HashMap<String, String> map = new HashMap<>();
        return map;
        
    }

    /**
     * Builds the {@link Wiki} based on the link as set in config.yml.
     *
     * @return The built {@link Wiki} instance.
     */
    public static Wiki buildWiki() {
        try {
            String wikiUrl = WCConfigUtils.getWikiURL();
            if ( wikiUrl == null || wikiUrl.isEmpty() ) {
                WCMessages.debug( "warning", "Wiki URL is not set correctly in the configuration file." );
                return getWiki();

            }
            wiki = new Wiki.Builder().withDomain( wikiUrl ).build();

        } catch ( Exception e ) {
            WCMessages.debug( "severe", "Unable to build the wiki: " + e.getMessage() );

        }

        return getWiki();

    }
    
    /**
     * Retrieves a GET request to the registered wiki's API to obtain a login token for authentication purposes.
     * <br>
     * @return A String containing the login token if successful, or an error message.
     */
    public static String getLoginToken() {
        try {
            // Pull login token from registered wiki
            ResponseBody response = getWiki().basicGET( "query", "format", "json", "meta", "tokens", "continue", "", "formatversion", "latest", "type", "login" ).body();
            JsonObject json = JsonParser.parseString( response.string() ).getAsJsonObject();
            response.close();

            // @todo save the login token to account_bridge.txt
            return json.getAsJsonObject( "query" ).getAsJsonObject( "tokens" ).get( "logintoken" ).toString();

        } catch ( Exception e ) {
            return "Error: " + e.getMessage();

        }

    }

    public static boolean validateToken( String token ) {
        try {
            // Test the token with a lightweight query
            ResponseBody response = getWiki().basicGET( "query", "format", "json", "meta", "userinfo", "token", token ).body();
            JsonObject json = JsonParser.parseString( response.string() ).getAsJsonObject();
            response.close();

            return !json.has( "error" ); // Check if there's no error in the response

        } catch ( Exception e ) {
            WCMessages.debug( "error", "Error checking token validity: " + e.getMessage() );
            return false;

        }

    }

    public static boolean login( String username, String password ) {
        HashMap<String, String> map = new HashMap<>();
        map.put( "format", "json" );
        map.put( "login", "json" );
        Response response = getWiki().basicPOST( "clientlogin", map );

        response.close();

        return false;

    }

}

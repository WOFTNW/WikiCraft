package io.github.iherongh.wikicraft.wiki;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.fastily.jwiki.core.Wiki;
import io.github.iherongh.wikicraft.config.WCConfigUtils;
import io.github.iherongh.wikicraft.messages.WCMessages;
import okhttp3.ResponseBody;

@SuppressWarnings ( "SpellCheckingInspection" )
public class WCWiki {

    private static Wiki wiki;

    public static Wiki getWiki() {
        return wiki;

    }

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

    public static String getLoginToken() {
        try {
            // Pull login token from registered wiki
            ResponseBody response = getWiki().basicGET( "query", "format", "json", "meta", "tokens", "continue", "", "formatversion", "latest", "type", "login" ).body();
            JsonObject json = JsonParser.parseString( response.string() ).getAsJsonObject();
            response.close();

            // TODO: save the login token to account_bridge.txt
            return json.getAsJsonObject( "query" ).getAsJsonObject( "tokens" ).get( "logintoken" ).toString();

        } catch ( Exception e ) {
            return "error: " + e.getMessage();

        }


    }

}

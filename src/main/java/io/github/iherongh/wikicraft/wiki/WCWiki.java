package io.github.iherongh.wikicraft.wiki;

import java.util.ArrayList;

import io.github.fastily.jwiki.core.Wiki;
import io.github.iherongh.wikicraft.config.WCConfigUtils;
import io.github.iherongh.wikicraft.messages.WCMessages;

public class WCWiki {

    private static Wiki wiki;

    /**
     * Retrieves the {@link Wiki} instance.
     *
     * @return The {@link Wiki} instance, or null if it hasn't been generated yet.
     *
     * @since 0.1.0
     */
    public static Wiki getWiki() {
        if ( wiki == null ) {
            WCMessages.debug( "warning", "No wiki has been generated!" );

        }

        return wiki;

    }

    /**
     * Builds the {@link Wiki} based on the link as set in config.yml.
     * <p>Fails if:
     * <ul>
     *     <li>The wiki URL is empty.
     *     <li>The wiki URL is set to "example.com".
     *     <li>The account username is empty.
     *     <li>The account password is empty.
     * </ul>
     */
    public static void buildWiki() {
        try {
            WCMessages.debug( "info", "Getting wiki and account information from config.yml..." );

            // Get login and wiki information
            String wikiUrl = WCConfigUtils.getWikiURL();
            String wikiUsername = WCConfigUtils.getWikiBotUsername();
            String wikiPassword = WCConfigUtils.getWikiBotPassword();
            String header = WCWikiUtils.getHeader();

            // Check if wiki-url key in config.yml is valid
            WCMessages.debug( "info", "Validating wiki URL..." );
            if ( wikiUrl == null || wikiUrl.isEmpty() || wikiUrl.equals( "example.com" ) ) {
                WCMessages.debug( "warning", "Wiki URL is not set correctly in the configuration file." );
                return;

            }

            // Check if wiki-bot-username and wiki-bot-password keys in config.yml are valid
            WCMessages.debug( "info", "Validating login credentials..." );
            if ( wikiUsername == null || wikiUsername.isEmpty() || wikiPassword == null || wikiPassword.isEmpty() ) {
                WCMessages.debug( "warning", "Account details are not set correctly in your config! Check the file for errors." );
                return;

            }

            // Attempt to build wiki with the given information
            WCMessages.debug( "info", "Building wiki at " + wikiUrl + " as user " + wikiUsername + "..." );
            wiki = new Wiki.Builder()
                        // Set the wiki URL, login credentials, and user agent
                        .withDomain( wikiUrl )
                        .withLogin( wikiUsername, wikiPassword )
                        .withUserAgent( header )
                        .build();

            ArrayList<String> rights = wiki.listUserRights( wikiUsername );
            WCMessages.debug( "info", "WikiCraft has the following rights: " + rights );
            
            // Get the wiki
            getWiki();

        } catch ( Exception e ) {
            WCMessages.debug( "severe", "Unable to build the wiki: " + e.getMessage() );
            
        }

    }

}

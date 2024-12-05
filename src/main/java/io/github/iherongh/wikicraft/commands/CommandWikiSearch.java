package io.github.iherongh.wikicraft.commands;

import io.github.fastily.jwiki.core.Wiki;
import io.github.iherongh.wikicraft.WikiCraft;
import io.github.iherongh.wikicraft.config.ConfigUtils;

import java.util.ArrayList;

public class CommandWikiSearch {
    private static Wiki wiki;

    public CommandWikiSearch() {
         try {
             String wikiUrl = ConfigUtils.getWikiURL();
             if ( wikiUrl == null || wikiUrl.isEmpty() ) {
                 WikiCraft.getInstance().getLogger().severe( "Wiki URL is not set in the configuration file." );

             } else {
                 wiki = new Wiki.Builder().withDomain( ConfigUtils.getWikiURL() ).build();
             }
         }
    }

    public Wiki getWiki() {
        return wiki;

    }

    public static ArrayList<String> searchWiki( String query ) {
        return wiki.search( query, 10 );

    }

}

package io.github.iherongh.wikicraft.commands;

import io.github.fastily.jwiki.core.NS;
import io.github.fastily.jwiki.core.Wiki;
import io.github.iherongh.wikicraft.wiki.WCWikiBuilder;

import java.util.ArrayList;

public class WCCommandWikiSearch {

    public static ArrayList<String> searchWiki( String query ) {
        WCWikiBuilder.buildWiki();
        return WCWikiBuilder.getWiki().search( query, 10 );

    }

    public static int getResultCount( String query ) {
        WCWikiBuilder.buildWiki();
        return searchWiki( query ).size();

    }

    public static ArrayList<String> pullAllPages() {
        return WCWikiBuilder.buildWiki().allPages( "", false, false, 50, NS.MAIN );

    }

}

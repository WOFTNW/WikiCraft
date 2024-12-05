package io.github.iherongh.wikicraft.wiki;

import io.github.fastily.jwiki.core.Wiki;
import io.github.iherongh.wikicraft.WikiCraft;
import io.github.iherongh.wikicraft.commands.WCCommandWikiLogin;
import io.github.iherongh.wikicraft.config.WCConfigUtils;
import io.github.iherongh.wikicraft.messages.WCMessages;
import org.bukkit.command.CommandSender;

public class WCWikiBuilder {
	private static Wiki wiki;

	public static Wiki getWiki() {
		return wiki;

	}

	public static Wiki buildWiki() {
		try {
			String wikiUrl = WCConfigUtils.getWikiURL();
			if ( wikiUrl == null || wikiUrl.isEmpty() ) {
				WCMessages.debug( "warning", "Wiki URL is not set in the configuration file." );

			} else {
				wiki = new Wiki.Builder().withDomain( "wiki.woftnw.org" ).build();

			}

		} catch ( Exception e ) {
			WCMessages.debug( "severe", "Unable to search through the wiki: " + e.getMessage() );

		}

		return wiki;

	}

	public static Wiki buildWiki( CommandSender sender, String username, String password ) {
		wiki = buildWiki();
		WCCommandWikiLogin.requestLogin(sender, username, password);
		return wiki;

	}

}

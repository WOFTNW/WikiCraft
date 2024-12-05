package io.github.iherongh.wikicraft.commands;

import io.github.fastily.jwiki.core.Wiki;
import io.github.iherongh.wikicraft.WikiCraft;
import io.github.iherongh.wikicraft.file.WCFileAccountBridge;
import io.github.iherongh.wikicraft.messages.WCMessages;
import io.github.iherongh.wikicraft.wiki.WCWikiBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WCCommandWikiLogin {
	private static final Wiki wiki = WCWikiBuilder.buildWiki();

	public static String requestLogin( CommandSender sender, String username, String password ) {
		username = username.replace( "\"", "" );
		password = password.replace( "\"", "" );

		if ( wiki.login( username, password ) ) {
			WikiCraft.getInstance().getLogger().info( "Login successful under username " + username );

			if ( sender instanceof Player player ) {
				WCMessages.debug( "info", "Attempting account link..." );
				if ( WCFileAccountBridge.addLink( player.getUniqueId(), username ) ) {
					WCMessages.debug( "info", username + " successfully linked to " + player.getUniqueId() );

				}

			} else {
				WCMessages.debug( "warning", "Command sender is not a player; aborting account link." );

			}


			return "Login successful! Welcome, " + username + "!";

		} else {
			WikiCraft.getInstance().getLogger().info( "Login unsuccessful under username " + username );
			return "Login unsuccessful. Attempted with" + username + " " + password;

		}

	}

	public static String refreshLogin() {
		if ( wiki.whoami() == null || wiki.whoami().isEmpty() ) {
			return "No login found. Use /wiki login <username> <password> to attempt login.";

		}

		return "Logged in as " + wiki.whoami();

	}

}

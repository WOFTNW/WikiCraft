package io.github.iherongh.wikicraft.commands;

import io.github.fastily.jwiki.core.Wiki;
import io.github.iherongh.wikicraft.file.WCFileAccountBridge;
import io.github.iherongh.wikicraft.messages.WCMessages;
import io.github.iherongh.wikicraft.wiki.WCWikiBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.shanerx.mojang.Mojang;
import org.shanerx.mojang.PlayerProfile;

public class WCCommandWikiLogin {
	private static final Wiki wiki = WCWikiBuilder.buildWiki();

	public static boolean requestLogin( CommandSender sender, String wikiUsername, String wikiPassword ) {
		if ( sender instanceof Player player ) {
			return requestLogin( sender, player.getName(), wikiUsername, wikiPassword );

		} else {
			return requestLogin( sender, null, wikiUsername, wikiPassword );

		}

	}


	public static boolean requestLogin( CommandSender sender, String mcUsername, String wikiUsername, String wikiPassword ) {
		Mojang m = new Mojang().connect();
		PlayerProfile playerProfile = m.getPlayerProfile( mcUsername );

		wikiUsername = wikiUsername.replace( "\"", "" );
		wikiPassword = wikiPassword.replace( "\"", "" );

		if ( !wiki.login( wikiUsername, wikiPassword ) ) {
			WCMessages.debug( "info", "Login unsuccessful under username " + wikiUsername );
			return false;

		}

		WCMessages.debug(  "info", "Login successful under username " + wikiUsername );

		if ( mcUsername == null ) {
			WCMessages.debug( "warning", "Command sender is not a player; aborting account link." );

		} else {
			WCMessages.debug( "info", "Attempting account link..." );
			if ( !WCFileAccountBridge.addLink( WCFileAccountBridge.formatStringUUIDToUUID( playerProfile.getUUID() ), wikiUsername ) ) {
				WCMessages.debug( "warning", "Unable to link " + mcUsername + " to wiki user " + wikiUsername + "!" );
				return false;

			}
			WCMessages.debug( "info", wikiUsername + " successfully linked to " + playerProfile.getUUID() );

		}
		return true;

	}

	public static Component refreshLogin() {
		if ( wiki.whoami() == null || wiki.whoami().isEmpty() ) {
			return WCMessages.message( "error", "No login found. Use /wiki login <username> <password> to attempt login." );

		}

		return WCMessages.message( "info", "Logged in as " + wiki.whoami() + "." );

	}

}

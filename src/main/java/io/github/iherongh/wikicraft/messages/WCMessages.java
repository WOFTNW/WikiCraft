package io.github.iherongh.wikicraft.messages;

import io.github.iherongh.wikicraft.WikiCraft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class WCMessages {

	public static Component message( String level, String msg ) {
		return switch ( level ) {
			case "info" -> WikiCraft.PREFIX.append( Component.text( msg ).color( TextColor.color( WikiCraft.TEXT_INFO ) ) );
			case "error" -> WikiCraft.PREFIX.append( Component.text( msg ).color( TextColor.color( WikiCraft.TEXT_ERROR ) ) );
			default -> Component.text("No level specified: ").color( TextColor.color( NamedTextColor.DARK_RED ) ).append( Component.text( msg ).color( TextColor.color( WikiCraft.TEXT_ERROR ) ) );
		};

	}


	public static void debug( String level, String msg ) {
		switch ( level ) {
			case "info": WikiCraft.instance.getLogger().info( msg ); break;
			case "warning": WikiCraft.instance.getLogger().warning( msg ); break;
			case "severe": WikiCraft.instance.getLogger().severe( msg ); break;

		}

	}

}

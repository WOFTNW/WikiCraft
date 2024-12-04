package io.github.iherongh.wikicraft.config;

import org.bukkit.configuration.file.FileConfiguration;

import static io.github.iherongh.wikicraft.WikiCraft.instance;

public class ConfigDefault {

	public static void instantiateWikiCraftConfig() {
		FileConfiguration config = instance.getConfig();

		if ( !config.contains( "wiki-url" ) )       { config.set( "wiki-url", "https://example.com/wiki" ); }

		if ( !config.contains( "max-results" ) )    { config.set( "max-results", 10 ); }

		if ( !config.contains( "locale" ) )         { config.set( "locale", "en-US" ); }

		if ( !config.contains( "messages" ) )       {

			// Usage messages
			config.set( "messages.en-US.usage.config", "" );
			config.set( "messages.en-US.usage.disable", "" );
			config.set( "messages.en-US.usage.help", "" );
			config.set( "messages.en-US.usage.reload", "" );
			config.set( "messages.en-US.usage.search", "" );

			// Generic fail messages
			config.set( "messages.en-US.fail.generic.not-enough-args", "Not enough arguments provided!" );
			config.set( "messages.en-US.fail.generic.too-many-args", "Too many arguments provided!" );
			config.set( "messages.en-US.fail.generic.no-perm", "You don't have permission to run that command! Contact an administrator if you believe this is incorrect." );

			// Command-specific fail messages
				// /wiki config
			config.set( "messages.en-US.fail.commands.config.not-enough-args", "{generic.not-enough-args}" );
			config.set( "messages.en-US.fail.commands.config.too-many-args", "{generic.too-many-args}" );
			config.set( "messages.en-US.fail.commands.config.no-perm", "{generic.no-perm}" );
				// /wiki disable
			config.set( "messages.en-US.fail.commands.disable.not-enough-args", "{generic.not-enough-args}" );
			config.set( "messages.en-US.fail.commands.disable.too-many-args", "{generic.too-many-args}" );
			config.set( "messages.en-US.fail.commands.disable.no-perm", "{generic.no-perm}" );
				// /wiki help
			config.set( "messages.en-US.fail.commands.help", "{generic.not-enough-args}" );
				// /wiki reload
			config.set( "messages.en-US.fail.commands.reload.too-many-args", "{generic.too-many-args}" );
			config.set( "messages.en-US.fail.commands.reload.no-perm", "{generic.no-perm}" );
				// /wiki search
			config.set( "messages.en-US.fail.commands.search.not-enough-args", "{generic.not-enough-args}" );
			config.set( "messages.en-US.fail.commands.search.too-many-args", "{generic.too-many-args}" );
			config.set( "messages.en-US.fail.commands.search.no-perm", "{generic.no-perm}" );
			config.set( "messages.en-US.fail.commands.search.no-results", "There were no results that matched your query." );
			config.set( "messages.en-US.fail.commands.search.timed-out", "The request timed out." );

		}

		instance.saveConfig();

	}

}

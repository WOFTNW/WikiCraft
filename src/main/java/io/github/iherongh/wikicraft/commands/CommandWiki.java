package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.ExecutableCommand;
import org.jetbrains.annotations.NotNull;

public class CommandWiki {

	public @NotNull ExecutableCommand<?, ?> getCommand() {
		return new CommandAPICommand( "wiki" ).withSubcommands(
				new CommandAPICommand( "config" ).withSubcommands(
						new CommandAPICommand( "get" ),
						new CommandAPICommand( "reload" ),
						new CommandAPICommand( "set" )
				),
				new CommandAPICommand( "disable" ),
				new CommandAPICommand( "reload" ),
				new CommandAPICommand( "disable" )
		);

	}

}

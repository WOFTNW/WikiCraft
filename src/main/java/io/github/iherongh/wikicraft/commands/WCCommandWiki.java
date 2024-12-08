package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.ExecutableCommand;
import org.jetbrains.annotations.NotNull;

public class WCCommandWiki {

    public @NotNull ExecutableCommand<?, ?> getCommand() {
        // /wiki <account|config|disable|login|reload|search>
        return new CommandAPICommand( "wiki" ).withSubcommands(
            // /wiki account <getToken|getPlayer|getWikiUser|login|refresh>
            WCCommandWikiAccount.getCommand(),

            // /wiki config <get|reload|set>
            WCCommandWikiConfig.getCommand(),

            // /wiki disable
            WCCommandWikiDisable.getCommand(),

            // /wiki reload
            WCCommandWikiReload.getCommand(),

            // /wiki search [<query>]
            // TODO: add more ways to search
            WCCommandWikiSearch.getCommand()

            // /wiki edit
            // TODO: add /wiki edit

            // /wiki remove
            // TODO: add /wiki remove

        );

    }

}

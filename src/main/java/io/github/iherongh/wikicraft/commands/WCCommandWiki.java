package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.ExecutableCommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WCCommandWiki {

    /**
     * Constructs and returns the main 'wiki' command with its subcommands.
     * <br><br>
     * This method creates the primary 'wiki' command structure, including various
     * subcommands for account management, configuration, disabling, reloading,
     * searching, and page operations.
     * <br><br>
     * The command structure is as follows:
     * <br>
     * - {@code /wiki account <get|list|login|refresh>}
     * <br>
     * - {@code /wiki config <get|reload|set>}
     * <br>
     * - {@code /wiki disable}
     * <br>
     * - {@code /wiki reload}
     * <br>
     * - {@code /wiki pages <add|delete|edit|info|search>}
     *
     * @return An {@link ExecutableCommand} object representing the complete 'wiki'
     *         command structure with all its subcommands.
     */
    public @NotNull ExecutableCommand<?, ?> getCommand() {
        // /wiki <account|config|disable|login|reload|search>
        return new CommandAPICommand( "wiki" ).withSubcommands(
            // /wiki account <get|list|login|refresh>
            WCCommandWikiAccount.getCommand(),

            // /wiki config <get|reload|set>
            WCCommandWikiConfig.getCommand(),

            // /wiki disable
            WCCommandWikiDisable.getCommand(),

            // /wiki reload
            WCCommandWikiReload.getCommand(),

            // /wiki pages <add|delete|edit|info|search>
            // @todo add /wiki pages <add|delete|edit|info|search>
            WCCommandWikiPages.getCommand()

        );

    }
    
    /**
     * Creates a {@link PlayerArgument} for command input with customisable entity inclusion.
     *
     * @param includeEntities   A boolean flag to determine whether to include
     *                          entity selectors or only online players.
     *                          <br><br>
     *                          If {@code true}, entity selectors ({@code @a},
     *                          {@code @e}, etc.) will be included.
     *                          <br>
     *                          If {@code false}, only online players will be suggested.
     * @return A {@link PlayerArgument} object configured based on the {@code includeEntities} parameter.
     *         If {@code includeEntities} is true, a standard {@link PlayerArgument} is returned.
     *         If false, a {@link PlayerArgument} with safe suggestions limited to online players is returned.
     */
    public static PlayerArgument playerNameArgument( boolean includeEntities ) {
        if (includeEntities) {
            return new PlayerArgument("name");
        }
        return (PlayerArgument) new PlayerArgument("name")
            .replaceSafeSuggestions(SafeSuggestions.suggest(info ->
                Bukkit.getOnlinePlayers().toArray(new Player[0])
            ));
    }
    
    /**
     * Generates a list of pages on the configured wiki, to be passed as command
     * arguments.
     *
     * @return A list of {@link StringArgument} objects representing all available wiki pages.
     */
    public static List<Argument<?>> wikiPagesAsArgument() {
        List<Argument<?>> pages = new ArrayList<>();
        
        pages.add( new StringArgument( "page" )
            .replaceSuggestions( ArgumentSuggestions.strings( WCCommandWikiPages.pullAllPages().toArray( new String[ 0 ] ) ) )
        
        );
        
        return pages;
        
    }

}

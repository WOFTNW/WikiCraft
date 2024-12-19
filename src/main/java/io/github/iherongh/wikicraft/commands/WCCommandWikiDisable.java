package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import io.github.iherongh.wikicraft.WikiCraft;
import io.github.iherongh.wikicraft.messages.WCMessages;
import org.bukkit.entity.Player;

public class WCCommandWikiDisable {

    public static CommandAPICommand getCommand() {
        return new CommandAPICommand( "disable" )
            .withPermission( CommandPermission.OP )
            .executes( ( sender, args ) -> {
                try {
                    if ( sender != null ) {
                        sender.sendMessage( "Disabling WikiCraft..." );
                    }

                    WikiCraft.disableWikiCraft();

                    if ( sender != null ) {
                        sender.sendMessage( "WikiCraft disabled." );

                    }
                } catch ( Exception e ) {
                    if ( sender instanceof Player player ) {
                        WCMessages.throwError( e, player );

                    } else {
                        WCMessages.throwError( e );

                    }

                }

            } );

    }

}

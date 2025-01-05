package io.github.iherongh.wikicraft.commands;

import dev.jorel.commandapi.CommandAPICommand;
import io.github.iherongh.wikicraft.messages.WCMessages;
import io.github.iherongh.wikicraft.wiki.WCWiki;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static io.github.iherongh.wikicraft.WikiCraft.PRIMARY;
import static io.github.iherongh.wikicraft.WikiCraft.TEXT_INFO;

/**
 * The {@code /wiki help} subcommand.
 *
 * @author iHeronGH
 * @version 0.1.0
 * @since 0.1.0
 *
 * @see WCCommandWikiHelp#getCommand()
 */
public class WCCommandWikiHelp {

    /**
     * Creates the {@code /wiki help} subcommand.
     * <p><b>Permission:</b> {@code wikicraft.command.help}
     * <p><b>Usage:</b> {@code /wiki help}
     * <ul>
     *     <li>{@code /wiki help}: Shows help information about WikiCraft commands
     * </ul>
     *
     * @return A {@link CommandAPICommand} object representing the {@code /wiki help} subcommand
     *
     * @since 0.1.0
     *
     * @see WCCommandWiki#getCommand()
     */
    public static CommandAPICommand getCommand() {
        // Log subcommand load
        WCMessages.debug("info", "Creating command: /wiki help");

        // /wiki help
        return new CommandAPICommand("help")
            .withPermission("wikicraft.command.help")
            .withShortDescription("Show help information")
            .executesPlayer((player, args) -> {
                try {
                    if (WCWiki.getWiki() == null) {
                        player.sendMessage(createLimitedHelpMessage());
                        return;
                    }

                    player.sendMessage(createFullHelpMessage());

                } catch (Exception e) {
                    WCMessages.throwError(e);

                }
            });
    }

    /**
     * Creates and returns the full help message component.
     *
     * @return A {@link Component} containing the formatted full help message
     *
     * @since 0.1.0
     */
    @Contract ( " -> new" )
    public static @NotNull Component createFullHelpMessage() {
        return Component.text()
            .append(Component.text("Access and manage your MediaWiki through Minecraft.\n")
                .color((TextColor) TEXT_INFO))
            .append(Component.text("Available commands:\n")
                .color((TextColor) TEXT_INFO))
            .append(Component.text("- /wiki account: ")
                .color((TextColor) PRIMARY))
            .append(Component.text("Manage wiki accounts, login, and view linked users.\n")
                .color((TextColor) TEXT_INFO))
            .append(Component.text("- /wiki config: ")
                .color((TextColor) PRIMARY))
            .append(Component.text("View and modify WikiCraft configuration.\n")
                .color((TextColor) TEXT_INFO))
            .append(Component.text("- /wiki disable: ")
                .color((TextColor) PRIMARY))
            .append(Component.text("Safely shut down the WikiCraft plugin.\n")
                .color((TextColor) TEXT_INFO))
            .append(Component.text("- /wiki reload: ")
                .color((TextColor) PRIMARY))
            .append(Component.text("Reload the WikiCraft plugin configuration.\n")
                .color((TextColor) TEXT_INFO))
            .append(Component.text("- /wiki pages: ")
                .color((TextColor) PRIMARY))
            .append(Component.text("Create, edit, delete, search, and view wiki pages.")
                .color((TextColor) TEXT_INFO))
            .build();
    }

    /**
     * Creates and returns the full help message component.
     *
     * @return A {@link Component} containing the formatted full help message
     *
     * @since 0.1.0
     */
    @Contract ( " -> new" )
    public static @NotNull Component createLimitedHelpMessage() {
        return Component.text()
            .append(Component.text("Access and manage your MediaWiki through Minecraft.\n")
                .color((TextColor) TEXT_INFO))
            .append(Component.text("Available commands:\n")
                .color((TextColor) TEXT_INFO))
            .append(Component.text("- /wiki config: ")
                .color((TextColor) PRIMARY))
            .append(Component.text("View and modify WikiCraft configuration.\n")
                .color((TextColor) TEXT_INFO))
            .append(Component.text("- /wiki disable: ")
                .color((TextColor) PRIMARY))
            .append(Component.text("Safely shut down the WikiCraft plugin.\n")
                .color((TextColor) TEXT_INFO))
            .append(Component.text("- /wiki help: ")
                .color((TextColor) PRIMARY))
            .append(Component.text("Show this message.\n")
                .color((TextColor) TEXT_INFO))
            .append(Component.text("- /wiki pages: ")
                .color((TextColor) PRIMARY))
            .append(Component.text("Search and view wiki pages.")
                .color((TextColor) TEXT_INFO))
            .build();
    }
}

package io.github.iherongh.wikicraft.arguments;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.*;
import io.github.iherongh.wikicraft.account.WCAccountBridge;
import io.github.iherongh.wikicraft.config.WCConfigUtils;
import io.github.iherongh.wikicraft.wiki.WCWikiUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;

/**
 * Manages argument handling and validation.
 */
public class WCArguments {

    /**
     * Creates a command argument for player names with customizable entity inclusion.
     *
     * @param includeEntities A boolean flag to determine whether to include entity selectors or only online players.
     *                       <br><br>
     *                       If {@code true}, entity selectors ({@code @a}, {@code @e}, etc.) will be included.
     *                       <br>
     *                       If {@code false}, only online players will be suggested.
     *
     * @return A list of {@link StringArgument} objects representing player names.
     *
     * @since 0.1.0
     */
    public static Argument<String> playerNameArgument( boolean includeEntities ) {
        return new CustomArgument<>( new StringArgument( "name" ), info -> {
            String input = info.input();

            if ( includeEntities ) {
                return input;

            }

            // Check if input matches an online player
            for ( Player player : Bukkit.getOnlinePlayers() ) {
                if ( player.getName().equals( input ) ) {
                    return input;

                }

            }

            return null;

        } ).replaceSuggestions( ArgumentSuggestions.strings( info ->
            Bukkit.getOnlinePlayers().stream()
                .map( Player::getName )
                .toArray( String[]::new )

        ) );

    }

    /**
     * Generates a list of pages on the configured wiki, to be passed as command arguments.
     *
     * @return A list of {@link StringArgument} objects representing all available wiki pages.
     *
     * @since 0.1.0
     */
    public static Argument<String> wikiPagesArgument() {
        return new CustomArgument<>( new GreedyStringArgument( "page" ), info -> {
            String input = info.input();
            ArrayList<String> validPages = getWikiPages();

            if ( validPages.contains( input ) ) {
                return input;

            }

            return null;

        } ).replaceSuggestions( ArgumentSuggestions.strings( info -> getWikiPages().toArray( new String[ 0 ] ) ) );

    }

    public static ArrayList<String> getWikiPages() {
        return WCWikiUtils.getPageCache();

    }

    /**
     * Generates a list of users on the configured wiki, to be passed as command arguments.
     *
     * @return A list of {@link StringArgument} objects representing all available wiki users.
     *
     * @since 0.1.0
     */
    public static Argument<String> wikiUserArgument() {
        JsonArray allUsers = WCWikiUtils.getUserCache();
        ArrayList<String> validUsers = new ArrayList<>();

        for ( JsonElement user : allUsers ) {
            JsonObject userAsJsonObject = user.getAsJsonObject();
            String userName = userAsJsonObject.get( "name" ).getAsString();

            // Only consider user pages without slashes
            if ( !userName.contains( "/" ) ) {
                // Remove "User:" prefix
                validUsers.add( userName.replace( "User:", "" ) );

            }

        }

        return new CustomArgument<>( new GreedyStringArgument( "name" ), info -> validUsers.contains( info.input() ) ? info.input() : null )
            .replaceSuggestions( ArgumentSuggestions.strings( info -> validUsers.toArray( new String[ 0 ] ) ) );

    }

    /**
     * Generates a list of keys from the plugin's configuration file, to be passed as command arguments.
     *
     * @return A list of {@link StringArgument} objects representing all valid configuration keys.
     *
     * @since 0.1.0
     */
    public static Argument<String> configKeyArgument() {
        Set<String> validKeys = WCConfigUtils.getConfig().getKeys( true );

        return new CustomArgument<>( new StringArgument( "key" ), info -> {
            String input = info.input();

            if ( validKeys.contains( input ) ) {
                return input;

            }

            return null;

        } ).replaceSuggestions( ArgumentSuggestions.strings( info -> validKeys.toArray( new String[ 0 ] ) ) );

    }

    /**
     * Generates a list of users linked to a Minecraft account.
     *
     * @return A list of {@link StringArgument} objects representing all linked users.
     *
     * @since 0.1.0
     */
    public static Argument<String> linkedUsersArgument() {
        Set<String> linkedUsers = getLinkedUsers();

        return new CustomArgument<>( new GreedyStringArgument( "wikiUser" ), info -> linkedUsers.contains( info.input() ) ? info.input() : null )
            .replaceSuggestions( ArgumentSuggestions.strings( info -> linkedUsers.toArray( new String[ 0 ] ) ) );

    }

    /**
     * Generates a list of users that are linked to a Minecraft account.
     *
     * @return A list of linked users.
     *
     * @since 0.1.0
     */
    private static @NotNull Set<String> getLinkedUsers() {
        return WCAccountBridge.getWikiUserToUUIDMap().keySet();

    }

    /**
     * Generates a list of users not linked to any Minecraft account.
     *
     * @return A list of {@link StringArgument} objects representing all unlinked users.
     *
     * @since 0.1.0
     */
    public static Argument<String> unlinkedUsersArgument() {
        return new CustomArgument<>( new GreedyStringArgument( "wikiAccount" ), info -> getUnlinkedUsers().contains( info.input() ) ? info.input() : null )
            .replaceSuggestions( ArgumentSuggestions.strings( info -> getUnlinkedUsers().toArray( new String[ 0 ] )

        ));

    }

    /**
     * Gets a list of users not linked to any Minecraft account.
     *
     * @return A list of unlinked users.
     *
     * @since 0.1.0
     */
    private static @NotNull ArrayList<String> getUnlinkedUsers() {
        JsonArray allUsers = WCWikiUtils.getUserCache();
        ArrayList<String> unlinkedUsers = new ArrayList<>();
        Set<String> linkedUsers = getLinkedUsers();

        for ( JsonElement user : allUsers ) {
            JsonObject userObj = user.getAsJsonObject();
            String userName = userObj.get( "name" ).getAsString().replace( "User:", "" );

            if ( !userName.contains( "/" ) && !linkedUsers.contains( userName )) {
                unlinkedUsers.add( userName );

            }

        }

        return unlinkedUsers;

    }

}

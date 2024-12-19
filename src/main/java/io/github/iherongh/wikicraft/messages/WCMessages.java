package io.github.iherongh.wikicraft.messages;

import io.github.iherongh.wikicraft.WikiCraft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

public class WCMessages {

    /**
     * Creates a formatted message component based on the specified level and message.
     *
     * @param level The severity level of the message.
     *              <br>
     *              Accepted values are:
     *              <br>
     *              <code>info</code> - for informational messages
     *              <br>
     *              <code>error</code> - for error messages
     *              <br>
     *              Any other value will be treated as an unknown level.
     *              <br>
     * @param msg   The content of the message to be formatted.
     * @return      A <code>Component</code> object representing the formatted message.
     *              The returned component includes the WikiCraft prefix and
     *              the message with appropriate coloring based on the level.
     *              For unknown levels, it returns an unknown severity
     *              specified error alongside the message.
     */
    public static Component message( String level, String msg ) {
        return switch ( level ) {
            case "info" -> WikiCraft.PREFIX.append( Component.text( msg ).color( TextColor.color( WikiCraft.TEXT_INFO ) ) );
            case "error" -> WikiCraft.PREFIX.append( Component.text( msg ).color( TextColor.color( WikiCraft.TEXT_ERROR ) ) );
            default -> Component.text( "Unknown severity specified: " ).color( TextColor.color( NamedTextColor.DARK_RED ) ).append( Component.text( msg ).color( TextColor.color( WikiCraft.TEXT_ERROR ) ) );

        };

    }


    /**
     * Logs a debug message to the console with the specified severity level.
     *
     * @param level The severity level of the debug message.
     *              <br>
     *              Accepted values are:
     *              <br>
     *              <code>info</code> - informational messages
     *              <br>
     *              <code>warning</code> - warning messages
     *              <br>
     *              <code>severe</code> - severe error messages
     *              <br>
     * @param msg   The debug message to be logged.
     */
    public static void debug( String level, String msg ) {
        switch ( level ) {
            case "info":
                WikiCraft.getInstance().getLogger().info( msg );
                break;
            case "warning":
                WikiCraft.getInstance().getLogger().warning( msg );
                break;
            case "severe":
                WikiCraft.getInstance().getLogger().severe( msg );
                break;

        }

    }
    
    /**
     * Logs an error to the console.
     *
     * @param error The exception that occurred, used for logging and creating the error message.
     */
    public static void throwError( Exception error ) {
        WCMessages.debug( "error", "Error: " + error.getMessage() );

    }

    /**
     * Logs an error and sends an error message to the specified command sender.
     * This method combines logging the error and notifying the sender in one call.
     *
     * @param error     The exception that occurred, used for logging and creating the error message.
     * @param sender    The command sender to receive the error message.
     */
    public static void throwError( Exception error, CommandSender sender ) {
        throwError( error );
        sender.sendMessage( WCMessages.message( "error", "Error: " + error.getMessage() ) );
    }

}

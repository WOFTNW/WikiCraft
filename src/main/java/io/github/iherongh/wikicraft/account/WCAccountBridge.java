package io.github.iherongh.wikicraft.account;

import io.github.iherongh.wikicraft.WikiCraft;
import io.github.iherongh.wikicraft.messages.WCMessages;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WCAccountBridge {

    private static final File accountsFile = new File( WikiCraft.getInstance().getDataFolder().getPath() + "/account_bridge.txt" );

    private static final Map<UUID, String> uuidToWikiUser = new HashMap<>();
    private static final Map<String, UUID> wikiUserToUUID = new HashMap<>();

    public static File getAccountsFile() {
        return accountsFile;

    }

    public static Map<String, UUID> getWikiUserToUUIDMap() {
        return wikiUserToUUID;

    }

    public static Map<UUID, String> getUUIDToWikiUserMap() {
        return uuidToWikiUser;

    }

    public static boolean instantiateAccountFile() {
        if ( !accountsFile.exists() ) {
            try {
                return accountsFile.createNewFile();

            } catch ( Exception e ) {
                WCMessages.debug( "warning", "Could not create account bridge file: " + e.getMessage() );
                return false;

            }

        }
        return true;

    }

    public static String getUUID( String wikiUsername ) {
        Map<String, UUID> map = getWikiUserToUUIDMap();

        try {
            String uuid = formatStringUUIDToString( String.valueOf( map.get( wikiUsername ) ) );
            WCMessages.debug( "info", "Wiki user " + wikiUsername + " found linked to " + uuid + "." );
            return uuid;

        } catch ( Exception e ) {
            WCMessages.throwError( e );
            return null;

        }
/*
        try {
            BufferedReader reader = new BufferedReader( new FileReader( accountsFile ) );
            String line;

            while ( ( line = reader.readLine() ) != null ) {
                String uuid = line.split( "\\|" )[ 0 ];
                String user = line.split( "\\|" )[ 1 ];

                if ( user.equals( wikiUsername ) ) {
                    WCMessages.debug( "info", "Wiki user " + wikiUsername + " found linked to " + uuid );
                    return uuid;

                }

            }
            reader.close();
            return WCMessages.message( "error", "No user matches UUID " + wikiUsername ).toString();

        } catch ( Exception e ) {
            WCMessages.debug( "warning", "Error reading file: " + e.getMessage() );

        }
        return "";
*/

    }

    public static String getWikiUser( Player player ) {
        return getWikiUser( player.getUniqueId() );

    }

    public static String getWikiUser( UUID uuid ) {
        Map<UUID, String> map = getUUIDToWikiUserMap();

        try {
            String wikiUsername = formatStringUUIDToString( String.valueOf( map.get( uuid ) ) );
            WCMessages.debug( "info", uuid + " found linked to wiki user " + wikiUsername + "." );
            return wikiUsername;

        } catch ( Exception e ) {
            WCMessages.throwError( e );
            return null;

        }
/*
        try {
            BufferedReader reader = new BufferedReader( new FileReader( getAccountsFile() ) );
            String line;

            while ( ( line = reader.readLine() ) != null ) {
                String wikiUser = line.split( "\\|", 2 )[ 1 ];

                WCMessages.debug( "info", wikiUser );
                if ( line.contains( uuid.toString() ) ) {
                    WCMessages.debug( "info", "Wiki user " + wikiUser + " found linked to " + uuid );
                    return wikiUser;

                }

            }

        } catch ( Exception e ) {
            WCMessages.debug( "warning", "Error reading the file: " + e.getMessage() );

        }

        return null;
*/

    }

    public static boolean addLink( UUID uuid, String username ) {
        if ( !getAccountsFile().exists() ) {
            WCMessages.debug( "info", "Account bridge file not found! Attempting instantiating..." );

            try {
                if ( instantiateAccountFile() ) {
                    WCMessages.debug( "info", "Account bridge file instantiated." );

                }
            } catch ( Exception e ) {
                WCMessages.throwError( e );

            }
        }

        try {
            BufferedWriter writer = new BufferedWriter( new FileWriter( getAccountsFile() ) );
            writer.write( uuid.toString() + "|" + username );
            writer.newLine();
            writer.close();

            uuidToWikiUser.put( uuid, username );
            wikiUserToUUID.put( username, uuid );

            return true;

        } catch ( Exception e ) {
            WCMessages.debug( "warning", "Error writing to file: " + e.getMessage() );

        }
        return false;

    }

    public static boolean removeLink( UUID uuid, String username ) {
        if ( !getAccountsFile().exists() ) {
            return true;

        }

        try {
            BufferedReader reader = new BufferedReader( new FileReader( getAccountsFile() ) );
            String line = reader.readLine();

            while ( line != null ) {
                line = reader.readLine();

                if ( line.contains( uuid.toString() ) || line.contains( username ) ) {
                    WCMessages.debug( "info", "Removing link between " + uuid + " and " + username );
                    return true;

                }

            }

        } catch ( Exception e ) {
            WCMessages.throwError( e );

        }
        return false;

    }

    public static String formatStringUUIDToString( String uuid ) {
        return String.format( "%s-%s-%s-%s-%s", uuid.substring( 0, 8 ), uuid.substring( 8, 12 ), uuid.substring( 12, 16 ), uuid.substring( 16, 20 ), uuid.substring( 20, 32 ) );

    }

    public static UUID formatStringUUIDToUUID( String uuid ) {
        return UUID.fromString( formatStringUUIDToString( uuid ) );

    }

}

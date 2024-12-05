package io.github.iherongh.wikicraft.file;

import io.github.iherongh.wikicraft.WikiCraft;
import io.github.iherongh.wikicraft.messages.WCMessages;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WCFileAccountBridge {
	
	private static final File accountsFile = new File( WikiCraft.getInstance().getDataFolder().getPath() + "/account_bridge.txt" );

	private static final Map<UUID, String> uuidToWikiUser = new HashMap<>();
	private static final Map<String, UUID> wikiUserToUUID = new HashMap<>();

	public static File getAccountsFile() {
		return accountsFile;
		
	}

	public static Map<String, UUID> getWikiUserToUUIDMap() {
		return wikiUserToUUID;

	}

	public static Map<UUID, String> getUuidToWikiUserMap() {
		return uuidToWikiUser;

	}

	public static void instantiateAccountFile() {
		if ( !accountsFile.exists() ) {
			try {
				accountsFile.createNewFile();

			} catch ( Exception e ) {
				WCMessages.debug( "warning", "Could not create account bridge file." );

			}

		}

	}

	public static String getUUID( String wikiUser ) {
		try {
			BufferedReader reader = new BufferedReader( new FileReader( accountsFile ) );
			String line;

			while ( ( line = reader.readLine() ) != null ) {
				String uuid = line.split( "\\|" )[ 0 ];
				String user = line.split( "\\|" )[ 1 ];

				if ( user.equals( wikiUser ) ) {
					WCMessages.debug( "info", "Wiki user " + wikiUser + " found linked to " + uuid );
					return uuid;

				}

			}
			reader.close();
			return WCMessages.message( "error", "No user matches UUID " + wikiUser ).toString();

		} catch ( Exception e ) {
			WCMessages.debug( "warning", "Error reading to file: " + e.getMessage() );

		}
		return "";

	}
	
	public static String getWikiUser( UUID uuid ) {
		try {
			BufferedReader reader = new BufferedReader( new FileReader( accountsFile ) );
			String line;

			while ( ( line = reader.readLine() ) != null ) {
				String wikiUser = line.split( "\\|", 2 )[ 1 ];

				WCMessages.debug( "info", wikiUser);
				if ( line.contains( uuid.toString() ) ) {
					WCMessages.debug( "info", "Wiki user " + wikiUser + " found linked to " + uuid );
					return wikiUser;

				}

			}
			return WCMessages.message( "error", "No user matches UUID " + uuid ).toString();

		} catch ( Exception e ) {
			WCMessages.debug( "warning", "Error reading the file: " + e.getMessage() );

		}
		return "";

	}

	public static boolean addLink( UUID uuid, String username ) {
		if ( !accountsFile.exists() ) { instantiateAccountFile(); }

		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter( accountsFile ) );
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

	public static String formatStringUUIDToString(String uuid ) {
		return String.format( "%s-%s-%s-%s-%s", uuid.substring( 0, 8 ), uuid.substring( 8, 12 ), uuid.substring( 12, 16 ), uuid.substring( 16, 20 ), uuid.substring( 20, 32 ) );

	}

	public static UUID formatStringUUIDToUUID( String uuid ) {
		return UUID.fromString( formatStringUUIDToString( uuid ) );

	}

}

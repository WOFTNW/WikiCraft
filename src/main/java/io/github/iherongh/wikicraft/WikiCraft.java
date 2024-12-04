package io.github.iherongh.wikicraft;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.github.iherongh.wikicraft.commands.CommandWiki;
import io.github.iherongh.wikicraft.config.ConfigDefault;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class WikiCraft extends JavaPlugin {

	public static Plugin instance;

	public static final RGBLike PRIMARY = TextColor.color( 54, 179, 160 );
	public static final RGBLike SECONDARY = TextColor.color( 21, 101, 107 );
	public static final RGBLike TERTIARY = TextColor.color( 18, 55, 54 );

	public static TextComponent PREFIX =
			Component.text( "[" ).color( TextColor.color( TERTIARY ) )
				.append( Component.text( "Wiki" ).color( TextColor.color( PRIMARY ) )
				.append( Component.text( "Craft" ).color( TextColor.color( SECONDARY ) ) )
				.append( Component.text( "]" ).color( TextColor.color( TERTIARY ) ) ) );

	public WikiCraft() {
		instance = this;

	}

	public static Plugin getInstance() {
		return instance;

	}

	public void setInstance( Plugin plugin ) {
		instance = plugin;

	}

	@Override
    public void onEnable() {
		try {
			setInstance( this );

			// Load configuration from config.yml file
			loadConfig();

			// Initialize CommandAPI
			CommandAPI.onLoad( new CommandAPIBukkitConfig( this ) );
			CommandAPI.onEnable();

			// Register commands
			registerCommands();

			// Declare successful enable
			getLogger().info( "WikiCraft successfully enabled." );

		} catch ( Exception e ) {
			getLogger().severe( "A fatal error has occurred and WikiCraft was unable to start: " + e.getMessage() );
			disableWikiCraft();
			throw new RuntimeException( e );

		}

	}

	@Override
	public void onDisable() {
		// Declare successful disable
		getLogger().info( "WikiCraft successfully disabled." );

	}

	public static void disableWikiCraft() {
		try {
			instance.getLogger().info( "Disabling WikiCraft..." );
			CommandAPI.unregister( "wiki" );
			CommandAPI.onDisable();
			Bukkit.getPluginManager().disablePlugin( instance );

		} catch ( Exception e ) {
			instance.getLogger().severe( "Unable to disable WikiCraft: " + e.getMessage() );

		}

	}

	private void loadConfig() {
		getLogger().info( "Attempting to load configurations..." );

		try {
			// Populate and instantiate default values
			ConfigDefault.instantiateWikiCraftConfig();

			// Reload the getKeys
			this.reloadConfig();

		} catch ( Exception e ) {
			throw new RuntimeException( e );

		}

		getLogger().info( "Configurations successfully loaded." );

	}

	private void registerCommands() {
		getLogger().info( "Registering commands..." );

		try {
			new CommandWiki().getCommand().register();

		} catch ( Exception e ) {
			getLogger().warning( "/wiki was unable to register: " + e.getMessage() );

		}

		getLogger().info( "Commands registered successfully." );

	}

}

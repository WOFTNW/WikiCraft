package io.github.iherongh.wikicraft;

import io.github.iherongh.CommandAPI;
import io.github.iherongh.CommandAPIBukkitConfig;
import io.github.iherongh.wikicraft.commands.CommandWiki;
import io.github.iherongh.wikicraft.file.config.DefaultConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class WikiCraft extends JavaPlugin {

	public static Plugin instance;

	public static final RGBLike PRIMARY = TextColor.color( 54, 179, 160 );
	public static final RGBLike SECONDARY = TextColor.color( 21, 101, 107 );
	public static final RGBLike TERTIARY = TextColor.color(54, 55, 130);

	public static TextComponent PREFIX =
			Component.text( "[" ).color( NamedTextColor.GRAY )
				.append( Component.text( "Wiki" ).color( TextColor.color( PRIMARY ) )
				.append( Component.text( "Craft" ).color( TextColor.color( SECONDARY ) ) )
				.append( Component.text( "]" ).color( NamedTextColor.GRAY ) ) );

	public WikiCraft() {
		instance = this;

	}

	@Override
    public void onEnable() {
		try {
			setInstance( this );

			// Load configuration from config.yml file
			loadConfig();

			// Register commands
			CommandAPI.onLoad( new CommandAPIBukkitConfig( this ) );
			CommandAPI.onEnable();
			registerCommands();

			// Declare successful enable
			getLogger().info( "WikiCraft successfully enabled." );

			YamlConfiguration yaml = YamlConfiguration.loadConfiguration( new File( "plugins/WikiCraft/plugin.yaml" ) );

			for ( String key : yaml.getKeys( true ) ) {
				getLogger().info( key );

			}
		} catch ( Exception e ) {
			getLogger().severe( "A fatal error has occurred and WikiCraft was unable to start: " + e.getMessage() );
			disableWikiCraft();
			throw new RuntimeException( e );

		}

	}

	@Override
	public void onDisable() {
		// Declare successful disable
		CommandAPI.onDisable();
		getLogger().info( "WikiCraft successfully disabled." );

	}

	private void loadConfig() {
		getLogger().info( "Loading configurations..." );

		// Save default config if it doesn't exist
		saveDefaultConfig();

		// Reload the config
		reloadConfig();

		// Populate and instantiate default values
		DefaultConfig.instantiateWikiCraftConfig();

		getLogger().info( "Configurations successfully loaded." );

	}

	private void registerCommands() {
		getLogger().info( "Registering commands..." );

		new CommandWiki().getCommand().register();

		getLogger().info( "Commands registered successfully." );

	}

	public static Plugin getInstance() {
		return instance;

	}

	public void setInstance( Plugin plugin ) {
		instance = plugin;

	}

	public static boolean disableWikiCraft() {
		try {
			Bukkit.getPluginManager().disablePlugin( instance );

		} catch ( Exception e ) {
			instance.getLogger().warning( "Unable to disable WikiCraft: " + e.getMessage() );
			return false;

		}

		return true;

	}

}

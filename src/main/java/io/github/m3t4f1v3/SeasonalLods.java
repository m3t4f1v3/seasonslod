package io.github.m3t4f1v3;


import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.casperge.realisticseasons.api.SeasonsAPI;

public final class SeasonalLods extends JavaPlugin implements Listener {

    public static Logger pluginLogger;
    public static SeasonsAPI instance;
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        pluginLogger = getLogger();
        instance = SeasonsAPI.getInstance();
        getServer().getPluginManager().registerEvents(new ListenerStuff(), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "voxy:sync_biomes");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "voxy:sync_season");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

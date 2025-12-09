package io.github.m3t4f1v3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.casperge.interfaces.NmsCode;
import me.casperge.realisticseasons.RealisticSeasons;
import me.casperge.realisticseasons.api.SeasonsAPI;
import me.casperge.realisticseasons.biome.BiomeRegister;
import me.casperge.realisticseasons.commands.ToggleSeasonsCommand;
import me.casperge.realisticseasons.season.SubSeason;

public final class SeasonalLods extends JavaPlugin implements Listener {

    public static Logger pluginLogger;
    public static SeasonsAPI instance;
    private int invalidId = 5555;

    public static String biomeJson;

    @Override
    public void onEnable() {
        // Plugin startup logic
        pluginLogger = getLogger();
        instance = SeasonsAPI.getInstance();
        getServer().getPluginManager().registerEvents(new ListenerStuff(), this);
        RealisticSeasons rsInstance = RealisticSeasons.getInstance();
        Bukkit.getScheduler().runTask(this, () -> {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(rsInstance.getPlugin().getName());
            if (plugin != null && plugin.isEnabled()) {
                Map<String, BiomeReplacement> biomeMap = new HashMap<>();
                for (Biome biome: RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME)) {
                    if (biome.getKey().getNamespace().equalsIgnoreCase("realisticseasons")) continue;
                    NmsCode nms = rsInstance.getNMSUtils();
                    int biomeId = nms.getBiomeID(biome);
                    BiomeReplacement br = new BiomeReplacement();

                    Seasons seasons = new Seasons();
                    int winterId = BiomeRegister.getWinterReplacement(biomeId);
                    int summerId = BiomeRegister.getSummerReplacement(biomeId);
                    int springId = BiomeRegister.getSpringReplacement(biomeId);
                    List<Integer> fallIds = BiomeRegister.getFallReplacements(biomeId);

                    if (winterId != invalidId) seasons.setWINTER(nms.getBiome(winterId));
                    if (summerId != invalidId) seasons.setSUMMER(nms.getBiome(summerId));
                    if (springId != invalidId) seasons.setSPRING(nms.getBiome(springId));    
                    
                    List<String> fallList = new ArrayList<>();
                    for (int fid : fallIds) {
                        if (fid != invalidId) fallList.add(nms.getBiome(fid));
                    }
                    if (!fallList.isEmpty()) seasons.setFALL(fallList);       

                    if (!seasons.isEmpty()) br.setSEASONS(seasons);

                    Map<String, Seasons> subSeasonMap = new HashMap<>();
                    for (SubSeason subSeason : SubSeason.values()) {
                        if (subSeason == SubSeason.END) continue;

                        Seasons subSeasons = new Seasons();

                        int winterSubId = BiomeRegister.getMixWinterReplacement(biomeId, subSeason.getPhase());
                        int summerSubId = BiomeRegister.getMixSummerReplacement(biomeId, subSeason.getPhase());
                        int springSubId = BiomeRegister.getMixSpringReplacement(biomeId, subSeason.getPhase());
                        int fallSubId = BiomeRegister.getMixFallReplacement(biomeId, subSeason.getPhase());

                        if (winterSubId != invalidId) subSeasons.setWINTER(nms.getBiome(winterSubId));
                        if (summerSubId != invalidId) subSeasons.setSUMMER(nms.getBiome(summerSubId));
                        if (springSubId != invalidId) subSeasons.setSPRING(nms.getBiome(springSubId));    
                        if (fallSubId != invalidId) subSeasons.setFALL(List.of(nms.getBiome(fallSubId)));

                        if (!subSeasons.isEmpty()) subSeasonMap.put(subSeason.name(), subSeasons);
                    }
                    if (!subSeasonMap.isEmpty()) br.setSUB_SEASONS(subSeasonMap);
                    if (!br.isEmpty()) biomeMap.put(biome.getKey().asString(), br);
                }

                Gson gson = new GsonBuilder()
                    // .setPrettyPrinting()
                    .create();
                String jsonOutput = gson.toJson(biomeMap);

                SeasonalLods.pluginLogger.info("Generated Biome Replacement JSON");
                // SeasonalLods.pluginLogger.info(jsonOutput);
                biomeJson = jsonOutput;
            }
        });
        Networking.NETWORK_REGISTRY.addServerboundHandler(Networking.DISCOVER_PACKET, (packet, player) -> {
            if (!ToggleSeasonsCommand.disabled.contains(player.getPlayer().getUniqueId())) ToggleSeasonsCommand.disabled.add(player.getPlayer().getUniqueId());
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

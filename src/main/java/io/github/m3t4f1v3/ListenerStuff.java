package io.github.m3t4f1v3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.casperge.realisticseasons.api.SeasonBiome;
import me.casperge.realisticseasons.api.SeasonChangeEvent;
import me.casperge.realisticseasons.season.Season;
import xyz.bluspring.modernnetworking.bukkit.api.BukkitNetworkSender;

public class ListenerStuff implements Listener {
    private static void sendSeason(Player player, Season season) {
        BukkitNetworkSender.sendPacketServer(player, new Networking.SeasonPacket(season.getConfigName()));
    }

    @EventHandler
    public void onSeasonChange(SeasonChangeEvent e) {
        // Season oldSeason = e.getOldSeason();
        Season newSeason = e.getNewSeason();
        World w = e.getWorld();
        for (Player player : w.getPlayers()) {
            sendSeason(player, newSeason);
        }
    }

    final record SeasonBiomeEntry(
            int water,
            List<Integer> foliages,
            int grass) {
    }

    final record SeasonOutput(
            Map<String, SeasonBiomeEntry> biomes) {
    }

    private static void sendStuff(Player player) {
        Map<String, SeasonOutput> output = new HashMap<>();
        RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME).forEach(biome -> {
            String biomeId = biome.getKey().asString();

            for (Season season : new Season[] { Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER }) {

                SeasonBiome sBiome = SeasonalLods.instance.getReplacementSeasonBiome(biome, season);
                if (sBiome == null)
                    continue;

                String seasonName = season.getConfigName().toUpperCase();

                output.computeIfAbsent(seasonName, s -> new SeasonOutput(new HashMap<>()));
                Map<String, SeasonBiomeEntry> seasonBiomes = output.get(seasonName).biomes();

                String waterColorHex = sBiome.getWaterColoHex();
                String grassColorHex = sBiome.getGrassColorHex();

                int water = -1;
                int grass = -1;

                try {
                    water = Integer.parseInt(waterColorHex, 16);
                } catch (NumberFormatException ex) {

                }
                try {
                    grass = Integer.parseInt(grassColorHex, 16);
                } catch (NumberFormatException ex) {
                    
                }

                List<Integer> foliages = new ArrayList<>();
                for (String hex : sBiome.getFoliageColorsHex()) {
                    try {
                        foliages.add(Integer.parseInt(hex, 16));
                    } catch (NumberFormatException ex) {

                    }
                }

                seasonBiomes.put(biomeId, new SeasonBiomeEntry(water, foliages, grass));
            }
        });

        Gson gson = new GsonBuilder()
                    // .setPrettyPrinting()
                    .create();
        
        String json = gson.toJson(output);

        BukkitNetworkSender.sendPacketServer(player, new Networking.BiomePacket(json));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        sendStuff(player);
        sendSeason(player, SeasonalLods.instance.getSeason(player.getWorld()));
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        sendStuff(player);
        sendSeason(player, SeasonalLods.instance.getSeason(player.getWorld()));
    }
}

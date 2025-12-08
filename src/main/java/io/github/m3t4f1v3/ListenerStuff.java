package io.github.m3t4f1v3;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;

import me.casperge.realisticseasons.RealisticSeasons;
import me.casperge.realisticseasons.api.SeasonsAPI;
import me.casperge.realisticseasons.calendar.Date;
import me.casperge.realisticseasons.season.Season;
import me.casperge.realisticseasons.season.SubSeason;
import xyz.bluspring.modernnetworking.bukkit.api.BukkitNetworkSender;

public class ListenerStuff implements Listener {

    private Season currentSeason = null;
    private int currentSubSeasonPhase = -1;


    private void sendData(Player player) {
        String seasonName = currentSeason.name();
        BukkitNetworkSender.sendPacketServer(player, new Networking.SeasonPacket(SeasonalLods.biomeJson, seasonName, currentSubSeasonPhase, RealisticSeasons.getInstance().getSettings().subSeasonsEnabled));
        // BukkitNetworkSender.sendPacketServer(player, new Networking.ReloadPacket()); 
    }

    private void updateDataThenSend(Player player, boolean override) {
        Season season = SeasonsAPI.getInstance().getSeason(player.getWorld());
        int subSeasonPhase = getCurrentSubSeason(SeasonsAPI.getInstance().getDate(player.getWorld()), player.getWorld().getFullTime()).getPhase();
        if (season != currentSeason || subSeasonPhase != currentSubSeasonPhase || override) {
            currentSeason = season;
            currentSubSeasonPhase = subSeasonPhase;
            sendData(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerRegisterChannelEvent event) {
        Player player = event.getPlayer();
        updateDataThenSend(player, true);
        // BukkitNetworkSender.sendPacketServer(player, new Networking.ReloadPacket());
    }

    SubSeason getCurrentSubSeason(Date date, long fullTime) {
        if (date == null) {
            return SubSeason.MIDDLE;
        } else {
            Long time = fullTime % 24000L;
            double var4 = RealisticSeasons.getInstance().getTimeManager().getSeasonProgressPercentage(date, time);
            if (var4 < 9.0D) {
                return SubSeason.START;
            } else if (var4 >= 9.0D && var4 < 18.0D) {
                return SubSeason.EARLY;
            } else if (var4 >= 18.0D && var4 < 84.0D) {
                return SubSeason.MIDDLE;
            } else if (var4 >= 84.0D && var4 < 92.0D) {
                return SubSeason.LATE;
            } else {
                return var4 >= 92.0D ? SubSeason.END : SubSeason.MIDDLE;
            }
        }
    }

    @EventHandler
    public void onTick(ServerTickStartEvent event) {
        for (World world : RealisticSeasons.getInstance().getServer().getWorlds()) {
            for (Player player : world.getPlayers()) {
                updateDataThenSend(player, false);
            }
        }
    }
}

package io.github.m3t4f1v3;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

import me.casperge.realisticseasons.RealisticSeasons;
import me.casperge.realisticseasons.api.SeasonChangeEvent;
import me.casperge.realisticseasons.api.SeasonsAPI;
import me.casperge.realisticseasons.calendar.Date;
import me.casperge.realisticseasons.calendar.DayChangeEvent;
import me.casperge.realisticseasons.season.Season;
import me.casperge.realisticseasons.season.SubSeason;
import xyz.bluspring.modernnetworking.bukkit.api.BukkitNetworkSender;

public class ListenerStuff implements Listener {

    private Season currentSeason = null;
    private int currentSubSeasonPhase = -1;

    private void updateDataThenSend(Player player, boolean includeBiomeData) {
        Season season = SeasonsAPI.getInstance().getSeason(player.getWorld());
        int subSeasonPhase = getCurrentSubSeason(SeasonsAPI.getInstance().getDate(player.getWorld()),
                player.getWorld().getFullTime()).getPhase();
        if (season != currentSeason || subSeasonPhase != currentSubSeasonPhase) {
            currentSeason = season;
            currentSubSeasonPhase = subSeasonPhase;
            BukkitNetworkSender.sendPacketServer(player, new Networking.GameplaySyncPacket(currentSeason.name(),
                    currentSubSeasonPhase, RealisticSeasons.getInstance().getSettings().subSeasonsEnabled));

        } else if (includeBiomeData) {
            BukkitNetworkSender.sendPacketServer(player,
                    new Networking.InitialSyncPacket(SeasonalLods.biomeJson, currentSeason.name(),
                            currentSubSeasonPhase, RealisticSeasons.getInstance().getSettings().subSeasonsEnabled));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerRegisterChannelEvent event) {
        Player player = event.getPlayer();
        updateDataThenSend(player, true);
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

    private void scheduleSyncUpdate(World world) {
        Bukkit.getScheduler().runTaskLater(
                SeasonalLods.getPlugin(SeasonalLods.class),
                () -> {
                    for (Player player : world.getPlayers()) {
                        updateDataThenSend(player, false);
                    }
                },
                1L);
    }

    @EventHandler
    public void onSeasonChange(SeasonChangeEvent e) {
        scheduleSyncUpdate(e.getWorld());
    }

    @EventHandler
    public void onDayChange(DayChangeEvent e) {
        scheduleSyncUpdate(e.getWorld());
    }
}

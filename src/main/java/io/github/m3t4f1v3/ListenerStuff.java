package io.github.m3t4f1v3;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

import me.casperge.realisticseasons.RealisticSeasons;
import me.casperge.realisticseasons.api.SeasonChangeEvent;
import me.casperge.realisticseasons.api.SeasonsAPI;
import me.casperge.realisticseasons.calendar.DayChangeEvent;
import me.casperge.realisticseasons.season.Season;
import me.casperge.realisticseasons.season.SubSeason;
import xyz.bluspring.modernnetworking.bukkit.api.BukkitNetworkSender;

public class ListenerStuff implements Listener {

    private static SubSeason currentSubSeason;

    @EventHandler
    public void onPlayerJoin(PlayerRegisterChannelEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (RealisticSeasons.getInstance().getSettings().subSeasonsEnabled) {
            SubSeason subSeason = RealisticSeasons.getInstance().getTimeManager().getCorrectSubSeason(world);
            BukkitNetworkSender.sendPacketServer(player, new Networking.SubSeasonPacket(subSeason.getPhase()));
        }
        Season season = SeasonsAPI.getInstance().getSeason(world);
        BukkitNetworkSender.sendPacketServer(player, new Networking.SeasonPacket(season.name()));
        BukkitNetworkSender.sendPacketServer(player, new Networking.BiomePacket(SeasonalLods.biomeJson));
    }

    @EventHandler
    public void onSeasonChange(SeasonChangeEvent e) {
        Season season = e.getNewSeason();
        World w = e.getWorld();
        for (Player player : w.getPlayers()) {
            BukkitNetworkSender.sendPacketServer(player, new Networking.SeasonPacket(season.name()));
        }
    }

    @EventHandler
    public void dayChange(DayChangeEvent e) {
        if (RealisticSeasons.getInstance().getSettings().subSeasonsEnabled) {
            int phase = RealisticSeasons.getInstance().getTimeManager().getCorrectSubSeason(e.getWorld()).getPhase();
            if (currentSubSeason == null || currentSubSeason.getPhase() != phase) {
                currentSubSeason = RealisticSeasons.getInstance().getTimeManager().getCorrectSubSeason(e.getWorld());
                for (Player player : e.getWorld().getPlayers()) {
                    BukkitNetworkSender.sendPacketServer(player, new Networking.SubSeasonPacket(currentSubSeason.getPhase()));
                }
            }
        }
    }
}

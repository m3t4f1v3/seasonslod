package io.github.m3t4f1v3;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.casperge.realisticseasons.RealisticSeasons;
import me.casperge.realisticseasons.api.SeasonChangeEvent;
import me.casperge.realisticseasons.calendar.DayChangeEvent;
import me.casperge.realisticseasons.season.Season;

public class ListenerStuff implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
    }

    @EventHandler
    public void onSeasonChange(SeasonChangeEvent e) {
        Season season = e.getNewSeason();
        World w = e.getWorld();
    }

    @EventHandler
    public void dayChange(DayChangeEvent e) {
        if (RealisticSeasons.getInstance().getSettings().subSeasonsEnabled) {
            int phase = RealisticSeasons.getInstance().getTimeManager().getCorrectSubSeason(e.getWorld()).getPhase();
        }
    }
}

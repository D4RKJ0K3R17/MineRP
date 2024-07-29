package com.coderandom.mine_rp.modules.jobs.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static com.coderandom.mine_rp.MineRP.PLAYER_JOBS_DATA;

public class OnPlayerQuitRemoveJob implements Listener {
    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e) {
        PLAYER_JOBS_DATA.removePlayerJob(e.getPlayer());
    }
}

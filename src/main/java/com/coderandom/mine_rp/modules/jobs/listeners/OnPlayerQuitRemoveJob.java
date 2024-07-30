package com.coderandom.mine_rp.modules.jobs.listeners;

import com.coderandom.mine_rp.modules.jobs.data.PlayerJobsData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuitRemoveJob implements Listener {
    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e) {
        PlayerJobsData.getInstance().removePlayerJob(e.getPlayer());
    }
}

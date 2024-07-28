package com.coderandom.mine_rp.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static com.coderandom.mine_rp.MineRP.ECONOMY_MANAGER;
import static com.coderandom.mine_rp.MineRP.PLAYER_JOBS_DATA;

public class OnPlayerQuit implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        PLAYER_JOBS_DATA.removePlayerJob(e.getPlayer().getUniqueId());

        ECONOMY_MANAGER.saveBalance(e.getPlayer().getUniqueId());
    }
}

package com.coderandom.mine_rp.listeners;

import com.coderandom.mine_rp.MineRP;
import com.coderandom.mine_rp.modules.economy.EconomyManagerFactory;
import com.coderandom.mine_rp.modules.jobs.data.PlayerJobsData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Logger;

public class OnPlayerQuit implements Listener {
    private static final Logger LOGGER = MineRP.getInstance().getLogger();

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        removeJob(player);
        saveBalance(player);
    }

    private void removeJob(Player player) {
        PlayerJobsData.getInstance().removePlayerJob(player);
    }

    private void saveBalance(Player player) {
        EconomyManagerFactory.getInstance().saveBalance(player.getUniqueId());
    }
}

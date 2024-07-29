package com.coderandom.mine_rp.modules.economy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static com.coderandom.mine_rp.modules.economy.managers.EconomyManagerFactory.getInstance;

public class OnPlayerQuitUnloadBalance implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        getInstance().saveBalance(e.getPlayer().getUniqueId());
    }
}

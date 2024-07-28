package com.coderandom.mine_rp.listeners;

import com.coderandom.mine_rp.modules.economy.managers.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

import static com.coderandom.mine_rp.MineRP.ECONOMY_MANAGER;

public class onEnable implements Listener {
    @EventHandler
    private void onEnable(PluginEnableEvent e) {
        Bukkit.getServer().getOnlinePlayers().forEach(player -> ECONOMY_MANAGER.loadBalance(player.getUniqueId()));
    }
}

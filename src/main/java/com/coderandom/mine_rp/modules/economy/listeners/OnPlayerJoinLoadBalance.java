package com.coderandom.mine_rp.modules.economy.listeners;

import com.coderandom.mine_rp.modules.economy.managers.EconomyManagerFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static com.coderandom.mine_rp.MineRP.*;

public class OnPlayerJoinLoadBalance implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (!player.hasPlayedBefore()) {
            if (!ECONOMY.createPlayerAccount(player)) {
                MINE_RP.getLogger().severe("Error creating player economy account for player: " + player.getName());
            } else {
                double initialBalance = CONFIG.getDouble("economy.initial_balance", 100.0);
                EconomyManagerFactory.getInstance().setBalance(player.getUniqueId(), initialBalance);
                MINE_RP.getLogger().info("Setting initial balance of " + initialBalance + " for new player " + player.getName());
            }
        } else {
            MINE_RP.getLogger().info("Loading existing balance for player " + player.getName());
            EconomyManagerFactory.getInstance().loadBalance(player.getUniqueId());
        }
    }
}

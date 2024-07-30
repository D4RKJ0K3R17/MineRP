package com.coderandom.mine_rp.modules.economy.listeners;

import com.coderandom.mine_rp.MineRP;
import com.coderandom.mine_rp.modules.economy.managers.EconomyManagerFactory;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Logger;

public class OnPlayerJoinLoadBalance implements Listener {
    private static final Logger LOGGER = MineRP.getInstance().getLogger();
    private static final Economy ECONOMY = MineRP.getInstance().getEconomy();
    private static final double initialBalance = MineRP.getInstance().getConfiguration().getDouble("economy.initial_balance", 100.0);

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (!player.hasPlayedBefore()) {
            if (!ECONOMY.createPlayerAccount(player)) {
                LOGGER.severe("Error creating player economy account for player: " + player.getName());
            } else {
                EconomyManagerFactory.getInstance().setBalance(player.getUniqueId(), initialBalance);
                LOGGER.info("Setting initial balance of " + initialBalance + " for new player " + player.getName());
            }
        } else {
            LOGGER.info("Loading existing balance for player " + player.getName());
            EconomyManagerFactory.getInstance().loadBalance(player.getUniqueId());
        }
    }
}

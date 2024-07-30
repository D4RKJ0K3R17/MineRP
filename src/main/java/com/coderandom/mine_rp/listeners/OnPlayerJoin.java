package com.coderandom.mine_rp.listeners;

import com.coderandom.mine_rp.MineRP;
import com.coderandom.mine_rp.modules.economy.EconomyManagerFactory;
import com.coderandom.mine_rp.modules.jobs.JobsManager;
import com.coderandom.mine_rp.modules.jobs.data.JobData;
import com.coderandom.mine_rp.modules.jobs.data.PlayerJobsData;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Logger;

public class OnPlayerJoin implements Listener {
    private static final Logger LOGGER = MineRP.getInstance().getLogger();
    private static final Economy ECONOMY = MineRP.getInstance().getEconomy();
    private static final double INITIAL_BALANCE = MineRP.getInstance().getConfiguration().getDouble("economy.initial_balance", 100.0);
    private static final String DEFAULT_JOB = MineRP.getInstance().getConfiguration().getString("default_job", "citizen");

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        loadEconomyBalance(player);
        loadDefaultJob(player);
    }

    private void loadDefaultJob(Player player) {
        if (DEFAULT_JOB != null && !DEFAULT_JOB.isEmpty()) {
            PlayerJobsData.getInstance().setPlayerJob(player, DEFAULT_JOB);
            LOGGER.info("Assigned default job '" + DEFAULT_JOB + "' to player " + player.getName());

            JobData jobData = JobsManager.getInstance().getJob(DEFAULT_JOB);
            if (jobData != null) {
                player.sendMessage("You've been assigned the job " + jobData.getName() + '!');
            } else {
                LOGGER.warning("JobData for default job '" + DEFAULT_JOB + "' is null.");
                player.sendMessage("You've been assigned a job, but there was an error finding job details.");
            }
        } else {
            LOGGER.warning("Default job is not set or is empty in the config.");
        }
    }

    private void loadEconomyBalance(Player player) {
        if (!player.hasPlayedBefore()) {
            if (!ECONOMY.createPlayerAccount(player)) {
                LOGGER.severe("Error creating player economy account for player: " + player.getName());
            } else {
                EconomyManagerFactory.getInstance().setBalance(player.getUniqueId(), INITIAL_BALANCE);
                LOGGER.info("Setting initial balance of " + INITIAL_BALANCE + " for new player " + player.getName());
            }
        } else {
            LOGGER.info("Loading existing balance for player " + player.getName());
            EconomyManagerFactory.getInstance().loadBalance(player.getUniqueId());
        }
    }
}

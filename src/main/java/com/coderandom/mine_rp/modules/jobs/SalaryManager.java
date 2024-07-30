package com.coderandom.mine_rp.modules.jobs;

import com.coderandom.mine_rp.MineRP;
import com.coderandom.mine_rp.modules.jobs.data.JobData;
import com.coderandom.mine_rp.modules.jobs.data.PlayerJobsData;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SalaryManager {
    private static Economy ECONOMY;
    private static volatile SalaryManager instance;
    private final long salaryFrequency;
    private final Plugin plugin;
    private final Logger logger;

    private SalaryManager(Plugin plugin) {
        this.plugin = plugin;
        this.logger = MineRP.getInstance().getLogger();
        this.salaryFrequency = MineRP.getInstance().getConfiguration().getInt("salary_frequency", 20) * 60L * 20L;
        this.ECONOMY = MineRP.getInstance().getEconomy();

        startSalaryPayments();
    }

    public static void initialize(Plugin plugin) {
        if (instance == null) {
            synchronized (SalaryManager.class) {
                if (instance == null) {
                    instance = new SalaryManager(plugin);
                }
            }
        }
    }

    private void startSalaryPayments() {
        logger.log(Level.INFO, "Starting salary payments every {0} minutes ({1} ticks).", new Object[]{salaryFrequency / 60 / 20, salaryFrequency});
        new BukkitRunnable() {
            @Override
            public void run() {
                paySalaries();
            }
        }.runTaskTimer(plugin, salaryFrequency, salaryFrequency);
    }

    private void paySalaries() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (UUID playerId : PlayerJobsData.getInstance().getAllPlayerJobs().keySet()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    JobData jobData = PlayerJobsData.getInstance().getPlayerJob(playerId);
                    if (jobData != null) {
                        double salary = jobData.getSalary();
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            ECONOMY.depositPlayer(player, salary);
                            logger.log(Level.INFO, "Paid {0} a salary of {1} for the job {2}.",
                                    new Object[]{player.getName(), salary, jobData.getName()});
                            player.sendMessage("You've been paid " + ECONOMY.format(salary) + " for being a " + jobData.getName());
                        });
                    } else {
                        logger.log(Level.WARNING, "Job data not found for player {0}.", player.getName());
                    }
                } else {
                    logger.log(Level.INFO, "Player {0} is not online. Skipping salary payment.", playerId);
                }
            }
        });
    }
}

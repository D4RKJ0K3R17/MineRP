package com.coderandom.mine_rp.modules.jobs.managers;

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

public class SalaryManager {
    private static final Economy ECONOMY = MineRP.getInstance().getEconomy();
    private static volatile SalaryManager instance;
    private final long salary_frequency;
    private final Plugin plugin;

    private SalaryManager(Plugin plugin) {
        this.plugin = plugin;
        this.salary_frequency = (long) MineRP.getInstance().getConfiguration().getInt("salary_frequency", 20) * 60 * 20;

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
        MineRP.getInstance().getLogger().log(Level.INFO, "Starting salary payments every " + salary_frequency / 60 / 20 + " minutes (" + salary_frequency + " ticks).");
        new BukkitRunnable() {
            @Override
            public void run() {
                paySalaries();
            }
        }.runTaskTimer(plugin, salary_frequency, salary_frequency);
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
                            plugin.getLogger().log(Level.INFO, "Paid {0} a salary of {1} for the job {2}",
                                    new Object[]{player.getName(), salary, jobData.getName()});
                            player.sendMessage("You've been paid " + ECONOMY.format(salary) + " for being a " + jobData.getName());
                        });
                    } else {
                        plugin.getLogger().log(Level.WARNING, "Job data not found for player {0}", player.getName());
                    }
                }
            }
        });
    }
}

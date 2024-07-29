package com.coderandom.mine_rp.modules.jobs.managers;

import com.coderandom.mine_rp.modules.jobs.data.JobData;
import com.coderandom.mine_rp.modules.jobs.data.PlayerJobsData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.logging.Level;

import static com.coderandom.mine_rp.MineRP.*;

public class SalaryManager {
    private final PlayerJobsData playerJobData;
    private final JavaPlugin plugin;
    private final long salary_frequency;

    public SalaryManager(JavaPlugin plugin, PlayerJobsData playerJobData) {
        this.plugin = plugin;
        this.playerJobData = playerJobData;
        this.salary_frequency = (long) CONFIG.getInt("salary_frequency", 20) * 60 * 20;

        startSalaryPayments();
    }

    public void startSalaryPayments() {
        MINE_RP.getLogger().log(Level.INFO, "Starting salary payments every " + salary_frequency / 60 / 20 + " minutes (" + salary_frequency + " ticks).");
        new BukkitRunnable() {
            @Override
            public void run() {
                paySalaries();
            }
        }.runTaskTimer(plugin, salary_frequency, salary_frequency);
    }

    private void paySalaries() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (UUID playerId : playerJobData.getAllPlayerJobs().keySet()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    JobData jobData = playerJobData.getPlayerJob(playerId);
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

package com.coderandom.mine_rp.managers;

import com.coderandom.mine_rp.modules.jobs.data.JobData;
import com.coderandom.mine_rp.modules.jobs.data.PlayerJobsData;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.logging.Level;

import static com.coderandom.mine_rp.MineRP.ECONOMY;

public class SalaryManager {
    private final PlayerJobsData playerJobData;
    private final JavaPlugin plugin;

    public SalaryManager(JavaPlugin plugin, PlayerJobsData playerJobData) {
        this.plugin = plugin;
        this.playerJobData = playerJobData;
    }

    public void startSalaryPayments(long intervalTicks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                paySalaries();
            }
        }.runTaskTimer(plugin, intervalTicks, intervalTicks);
    }

    private void paySalaries() {
        for (UUID playerId : playerJobData.getAllPlayerJobs().keySet()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                JobData jobData = playerJobData.getPlayerJob(playerId);
                if (jobData != null) {
                    double salary = jobData.getSalary();
                    ECONOMY.depositPlayer(player, salary);
                    plugin.getLogger().log(Level.INFO, "Paid {0} a salary of {1} for the job {2}",
                            new Object[]{player.getName(), salary, jobData.getName()});
                    player.sendMessage("You've been paid " + salary + " for being a " + jobData.getName());
                } else {
                    plugin.getLogger().log(Level.WARNING, "Job data not found for player {0}", player.getName());
                }
            }
        }
    }
}

package com.coderandom.mine_rp.modules.jobs.data;

import com.coderandom.mine_rp.modules.jobs.JobsManager;
import com.coderandom.mine_rp.modules.jobs.events.JobChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerJobsData {
    private static volatile PlayerJobsData instance;
    private final HashMap<UUID, String> playerJobs;

    private PlayerJobsData() {
        this.playerJobs = new HashMap<>();
    }

    public static void initialize() {
        if (instance == null) {
            synchronized (PlayerJobsData.class) {
                if (instance == null) {
                    instance = new PlayerJobsData();
                }
            }
        }
    }

    public static PlayerJobsData getInstance() {
        if (instance == null) {
            throw new IllegalStateException("PlayerJobsData has not been initialized. Call initialize() first.");
        }
        return instance;
    }

    public void setPlayerJob(Player player, String jobKey) {
        UUID uuid = player.getUniqueId();

        JobData oldJob = getPlayerJob(uuid);
        playerJobs.put(uuid, jobKey);
        JobData newJob = JobsManager.getInstance().getJob(jobKey);

        Bukkit.getPluginManager().callEvent(new JobChangeEvent(player, oldJob, newJob));
    }

    public void removePlayerJob(Player player) {
        UUID uuid = player.getUniqueId();

        JobData oldJob = getPlayerJob(uuid);
        playerJobs.remove(uuid);

        Bukkit.getPluginManager().callEvent(new JobChangeEvent(player, oldJob, null));
    }

    public JobData getPlayerJob(UUID playerId) {
        String jobKey = playerJobs.get(playerId);
        if (jobKey != null) {
            return JobsManager.getInstance().getJob(jobKey);
        }
        return null;
    }

    public HashMap<UUID, String> getAllPlayerJobs() {
        return playerJobs;
    }
}

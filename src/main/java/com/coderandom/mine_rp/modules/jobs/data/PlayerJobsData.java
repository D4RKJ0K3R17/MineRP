package com.coderandom.mine_rp.modules.jobs.data;

import java.util.HashMap;
import java.util.UUID;

import static com.coderandom.mine_rp.MineRP.JOBS_MANAGER;

public class PlayerJobsData {
    private final HashMap<UUID, String> playerJobs;

    public PlayerJobsData() {
        this.playerJobs = new HashMap<>();
    }

    public void setPlayerJob(UUID playerId, String jobName) {
        playerJobs.put(playerId, jobName);
    }

    public JobData getPlayerJob(UUID playerId) {
        String jobName = playerJobs.get(playerId);
        if (jobName != null) {
            return JOBS_MANAGER.getJob(jobName);
        }
        return null;
    }

    public void removePlayerJob(UUID playerId) {
        playerJobs.remove(playerId);
    }

    public HashMap<UUID, String> getAllPlayerJobs() {
        return playerJobs;
    }
}

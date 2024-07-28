package com.coderandom.mine_rp.listeners;

import com.coderandom.mine_rp.modules.jobs.data.JobData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static com.coderandom.mine_rp.MineRP.*;

public class OnPlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String defaultJob = CONFIG.getString("default_job", "citizen");
        Player player = e.getPlayer();

        if (defaultJob != null && !defaultJob.isEmpty()) {
            PLAYER_JOBS_DATA.setPlayerJob(player.getUniqueId(), defaultJob);
            MINE_RP.getLogger().info("Assigned default job '" + defaultJob + "' to player " + e.getPlayer().getName());

            // Fetch the JobData and ensure it is not null
            JobData jobData = JOBS_MANAGER.getJob(defaultJob);
            if (jobData != null) {
                player.sendMessage("You've been assigned the job " + jobData.getName() + '!');
            } else {
                MINE_RP.getLogger().warning("JobData for default job '" + defaultJob + "' is null.");
                player.sendMessage("You've been assigned a job, but there was an error finding job details.");
            }
        } else {
            MINE_RP.getLogger().warning("Default job is not set or is empty in the config.");
        }

        ECONOMY_MANAGER.loadBalance(player.getUniqueId());
    }
}

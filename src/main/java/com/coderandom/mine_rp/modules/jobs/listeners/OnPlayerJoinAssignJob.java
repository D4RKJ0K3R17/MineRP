package com.coderandom.mine_rp.modules.jobs.listeners;

import com.coderandom.mine_rp.MineRP;
import com.coderandom.mine_rp.modules.jobs.data.JobData;
import com.coderandom.mine_rp.modules.jobs.data.PlayerJobsData;
import com.coderandom.mine_rp.modules.jobs.managers.JobsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Logger;

public class OnPlayerJoinAssignJob implements Listener {
    private static final Logger LOGGER = MineRP.getInstance().getLogger();
    private static final String defaultJob = MineRP.getInstance().getConfiguration().getString("default_job", "citizen");

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (defaultJob != null && !defaultJob.isEmpty()) {
            PlayerJobsData.getInstance().setPlayerJob(player, defaultJob);
            LOGGER.info("Assigned default job '" + defaultJob + "' to player " + e.getPlayer().getName());

            // Fetch the JobData and ensure it is not null
            JobData jobData = JobsManager.getInstance().getJob(defaultJob);
            if (jobData != null) {
                player.sendMessage("You've been assigned the job " + jobData.getName() + '!');
            } else {
                LOGGER.warning("JobData for default job '" + defaultJob + "' is null.");
                player.sendMessage("You've been assigned a job, but there was an error finding job details.");
            }
        } else {
            LOGGER.warning("Default job is not set or is empty in the config.");
        }
    }
}

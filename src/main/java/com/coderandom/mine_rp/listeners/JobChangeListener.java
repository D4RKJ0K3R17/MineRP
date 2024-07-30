package com.coderandom.mine_rp.listeners;

import com.coderandom.mine_rp.modules.jobs.data.JobData;
import com.coderandom.mine_rp.modules.jobs.events.JobChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class JobChangeListener implements Listener {
    private final Plugin plugin;

    public JobChangeListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJobChange(JobChangeEvent event) {
        Player player = event.getPlayer();
        JobData oldJob = event.getOldJob();
        JobData newJob = event.getNewJob();

        // Get the player's PermissionAttachment
        PermissionAttachment attachment = player.addAttachment(plugin);

        // Remove old permissions
        if (oldJob != null) {
            Arrays.stream(oldJob.getPermissions()).forEach(attachment::unsetPermission);
        }

        // Add new permissions
        if (newJob != null) {
            Arrays.stream(newJob.getPermissions()).forEach(permission -> attachment.setPermission(permission, true));
        }
    }
}

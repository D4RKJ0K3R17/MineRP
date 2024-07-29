package com.coderandom.mine_rp.modules.jobs.listeners;

import com.coderandom.mine_rp.modules.jobs.data.JobData;
import com.coderandom.mine_rp.modules.jobs.events.JobChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

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

        // Remove old permissions
        if (oldJob != null) {
            PermissionAttachment attachment = player.addAttachment(plugin);
            for (String permission : oldJob.getPermissions()) {
                attachment.unsetPermission(permission);
            }
        }

        // Add new permissions
        if (newJob != null) {
            PermissionAttachment attachment = player.addAttachment(plugin);
            for (String permission : newJob.getPermissions()) {
                attachment.setPermission(permission, true);
            }
        }
    }
}

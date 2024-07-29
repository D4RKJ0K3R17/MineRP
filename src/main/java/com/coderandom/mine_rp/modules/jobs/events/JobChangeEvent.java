package com.coderandom.mine_rp.modules.jobs.events;

import com.coderandom.mine_rp.modules.jobs.data.JobData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JobChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final JobData oldJob;
    private final JobData newJob;

    public JobChangeEvent(Player player, JobData oldJob, JobData newJob) {
        this.player = player;
        this.oldJob = oldJob;
        this.newJob = newJob;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public JobData getOldJob() {
        return oldJob;
    }

    public JobData getNewJob() {
        return newJob;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

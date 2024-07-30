package com.coderandom.mine_rp.modules.jobs.commands;

import com.coderandom.mine_rp.modules.jobs.data.JobData;
import com.coderandom.mine_rp.modules.jobs.data.PlayerJobsData;
import com.coderandom.mine_rp.modules.jobs.managers.JobsManager;
import com.coderandom.mine_rp.util.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class JobCommand extends BaseCommand {
    /**
     * Constructs a new command with the specified parameters.
     */
    public JobCommand() {
        super(
                "job",
                new String[]{},
                "mine_rp.job.apply",
                "/job <job_name>",
                "Allows a player to switch jobs"
        );
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /job <job_name>");
                return;
            }

            String jobKey = args[0];
            JobData job = JobsManager.getInstance().getJob(jobKey);

            if (job != null) {
                if (player.hasPermission("mine_rp.job." + jobKey)) {
                    PlayerJobsData.getInstance().setPlayerJob(player, jobKey);
                    player.sendMessage(ChatColor.GREEN + "You have successfully switched to the job: " + ChatColor.YELLOW + job.getName());
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have permission to switch to this job.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Job not found!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String partialJob = args[0].toLowerCase();
            return JobsManager.getInstance().getJobKeys().stream()
                    .filter(job -> job.toLowerCase().startsWith(partialJob))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}

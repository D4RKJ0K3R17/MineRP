package com.coderandom.mine_rp.modules.economy.commands;

import com.coderandom.mine_rp.MineRP;
import com.coderandom.mine_rp.util.BaseCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BalanceCommand extends BaseCommand {
    private static final Economy ECONOMY = MineRP.getInstance().getEconomy();

    public BalanceCommand() {
        super(
                "balance",
                new String[]{"bal"},
                "mine_rp.economy.balance",
                "/balance | /bal",
                "Allows the user to check their balance."
        );
    }

    @Override
    public void executeCommand(final CommandSender sender, final String[] args) {
        if (sender instanceof Player player && args.length == 0) {
            CompletableFuture.runAsync(() -> {
                double balance = ECONOMY.getBalance(player);
                player.sendMessage(ChatColor.GREEN + "Balance: " + ECONOMY.format(balance));
            });
        } else if (args.length == 1 && sender.hasPermission(this.getPermission() + ".others")) {
            CompletableFuture.runAsync(() -> {
                //noinspection deprecation
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                if (target != null && ECONOMY.hasAccount(target)) {
                    double balance = ECONOMY.getBalance(target);
                    sender.sendMessage(target.getName() + "'s balance: " + ECONOMY.format(balance));
                } else {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                }
            });
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /bal <player>");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}

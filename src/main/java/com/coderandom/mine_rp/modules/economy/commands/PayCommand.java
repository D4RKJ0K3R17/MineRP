package com.coderandom.mine_rp.modules.economy.commands;

import com.coderandom.mine_rp.util.BaseCommand;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static com.coderandom.mine_rp.MineRP.ECONOMY;

public class PayCommand extends BaseCommand {
    public PayCommand() {
        super(
                "pay",
                new String[]{},
                "mine_rp.economy.pay",
                "/pay <player_name> <amount>",
                "Allows a user to pay another player"
        );

    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("Usage: /pay <player_name> <amount>");
            return;
        }

        String targetPlayerName = args[0];
        String amountStr = args[1];

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            sender.sendMessage("The amount must be a valid number.");
            return;
        }

        if (amount <= 0) {
            sender.sendMessage("The amount must be greater than zero.");
            return;
        }

        Player senderPlayer = sender instanceof Player ? (Player) sender : null;
        if (senderPlayer != null) {
            double senderBalance = ECONOMY.getBalance(senderPlayer);
            if (senderBalance < amount) {
                sender.sendMessage("You do not have enough money to complete this transaction.");
                return;
            }

            EconomyResponse withdrawalResponse = ECONOMY.withdrawPlayer(senderPlayer, amount);
            if (!withdrawalResponse.transactionSuccess()) {
                sender.sendMessage("Failed to withdraw money from your account: " + withdrawalResponse.errorMessage);
                return;
            }
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);

        if (!ECONOMY.hasAccount(targetPlayer)) {
            sender.sendMessage("The specified player does not have an economy account.");
            if (senderPlayer != null) {
                ECONOMY.depositPlayer(senderPlayer, amount); // Refund the sender if the target player doesn't have an account
            }
            return;
        }

        EconomyResponse depositResponse = ECONOMY.depositPlayer(targetPlayer, amount);
        if (!depositResponse.transactionSuccess()) {
            sender.sendMessage("Failed to deposit money to the recipient's account: " + depositResponse.errorMessage);
            if (senderPlayer != null) {
                ECONOMY.depositPlayer(senderPlayer, amount); // Refund the sender if the deposit fails
            }
            return;
        }

        sender.sendMessage("You have successfully paid " + ECONOMY.format(amount) + " to " + targetPlayerName + ".");
        if (targetPlayer.isOnline()) {
            ((Player) targetPlayer).sendMessage("You have received " + ECONOMY.format(amount) + " from " + sender.getName() + ".");
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
        } else if (args.length == 2) {
            return List.of("10", "100", "1000"); // Provide example amounts
        }
        return List.of();
    }
}

package com.coderandom.mine_rp.modules.economy.commands;

import com.coderandom.mine_rp.util.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coderandom.mine_rp.MineRP.ECONOMY;

public class EconomyAdminCommand extends BaseCommand {
    public EconomyAdminCommand() {
        super(
                "economy",
                new String[]{"econ", "money"},
                "mine_rp.economy.admin",
                "/economy help",
                "A collection of admin commands for economy."
        );
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            showHelp(sender);
            return;
        }

        if (args.length < 3) {
            sender.sendMessage("Usage: /economy <set|deposit|withdraw> <player_name> <amount>");
            return;
        }

        String subCommand = args[0].toLowerCase();
        String targetPlayerName = args[1];
        String amountStr = args[2];

        double amount;
        String amountFormat;

        try {
            amount = Double.parseDouble(amountStr);
            amountFormat = ECONOMY.format(amount);
        } catch (NumberFormatException e) {
            sender.sendMessage("The amount must be a valid number.");
            return;
        }

        Optional<OfflinePlayer> optionalPlayer = Optional.ofNullable(Bukkit.getOfflinePlayer(targetPlayerName));
        if (optionalPlayer.isEmpty() || !ECONOMY.hasAccount(optionalPlayer.get())) {
            sender.sendMessage("Player " + targetPlayerName + " not found or does not have an economy account.");
            return;
        }

        OfflinePlayer player = optionalPlayer.get();

        switch (subCommand) {
            case "set":
                handleSetBalance(player, amount);
                sender.sendMessage("Set balance of " + targetPlayerName + " to " + amountFormat);
                break;
            case "deposit":
                handleDeposit(player, amount);
                sender.sendMessage("Deposited " + amountFormat + " to " + targetPlayerName + "'s account.");
                break;
            case "withdraw":
                handleWithdraw(player, amount);
                sender.sendMessage("Withdrew " + amountFormat + " from " + targetPlayerName + "'s account.");
                break;
            default:
                sender.sendMessage("Unknown subcommand. Use /economy help for more information.");
                break;
        }
    }

    private void handleWithdraw(OfflinePlayer player, double amount) {
        ECONOMY.withdrawPlayer(player, amount);
    }

    private void handleDeposit(OfflinePlayer player, double amount) {
        ECONOMY.depositPlayer(player, amount);
    }

    private void handleSetBalance(OfflinePlayer player, double amount) {
        double currentBalance = ECONOMY.getBalance(player);
        ECONOMY.withdrawPlayer(player, currentBalance);
        ECONOMY.depositPlayer(player, amount);
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(
                "§6=====[ §eEconomy Commands §6]=====\n" +
                        "§e/economy set <player_name> <amount> §7- Set a player's balance to a specific amount.\n" +
                        "§e/economy deposit <player_name> <amount> §7- Deposit a specific amount to a player's balance.\n" +
                        "§e/economy withdraw <player_name> <amount> §7- Withdraw a specific amount from a player's balance.\n" +
                        "§e/economy help §7- Show this help message.\n" +
                        "§6==============================="
        );
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String partialCommand = args[0].toLowerCase();
            return Stream.of("set", "deposit", "withdraw", "help")
                    .filter(command -> command.toLowerCase().startsWith(partialCommand))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            String partialName = args[1].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
        } else if (args.length == 3) {
            return List.of("10", "100", "1000");
        }
        return List.of();
    }
}

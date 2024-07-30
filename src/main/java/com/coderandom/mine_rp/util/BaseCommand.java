package com.coderandom.mine_rp.util;

import com.coderandom.mine_rp.MineRP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public abstract class BaseCommand extends BukkitCommand {

    /**
     * Constructs a new command with the specified parameters.
     *
     * @param command     the name of the command
     * @param aliases     the aliases for the command
     * @param permission  the required permission to use the command
     * @param usage       the usage message for the command
     * @param description the description of the command
     */
    protected BaseCommand(String command, String[] aliases, String permission, String usage, String description) {
        super(command);
        setDescription(description);
        setPermission(permission);
        setUsage(usage);
        setPermissionMessage(ChatColor.RED + "You don't have " + permission + " to use this command");
        if (aliases != null) {
            setAliases(Arrays.asList(aliases));
        }

        registerCommand(command);
    }

    /**
     * Registers the command in the Bukkit command map.
     *
     * @param command the name of the command to register
     */
    private void registerCommand(String command) {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(MineRP.getInstance().getName(), this);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to register command '" + command + "': " + e.getMessage(), e);
        }
    }

    /**
     * Executes the command.
     *
     * @param commandSender the source of the command
     * @param s             the alias of the command which was used
     * @param strings       the arguments passed to the command
     * @return true if the command was handled successfully, otherwise false
     */
    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        // This stops command block usage just in case
        if (!(commandSender instanceof Player) && !(commandSender instanceof ConsoleCommandSender)) {
            commandSender.sendMessage("This command can only be used by a player or the console.");
            return true;
        }
        executeCommand(commandSender, strings);
        return false;
    }

    /**
     * Abstract method to be implemented by subclasses with specific command logic.
     *
     * @param sender the source of the command
     * @param args   the arguments passed to the command
     */
    public abstract void executeCommand(CommandSender sender, String[] args);

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return tabComplete(sender, args);
    }


    public abstract List<String> tabComplete(CommandSender sender, String[] args);
}

package com.coderandom.mine_rp;

import com.coderandom.mine_rp.commands.economy.BalanceCommand;
import com.coderandom.mine_rp.commands.economy.EconomyAdminCommand;
import com.coderandom.mine_rp.commands.economy.PayCommand;
import com.coderandom.mine_rp.commands.jobs.JobCommand;
import com.coderandom.mine_rp.listeners.OnPlayerJoin;
import com.coderandom.mine_rp.listeners.OnPlayerQuit;
import com.coderandom.mine_rp.modules.economy.EconomyManagerFactory;
import com.coderandom.mine_rp.modules.economy.VaultEconomyManager;
import com.coderandom.mine_rp.modules.jobs.JobsManager;
import com.coderandom.mine_rp.modules.jobs.SalaryManager;
import com.coderandom.mine_rp.modules.jobs.data.PlayerJobsData;
import com.coderandom.mine_rp.util.MySQLManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class MineRP extends JavaPlugin {
    private static volatile MineRP instance;
    private FileConfiguration config;
    private Economy economy;
    private Permission permission;

    public static MineRP getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        config = getConfig();

        // Setup Vault Economy
        if (!setupEconomy()) {
            getLogger().log(Level.SEVERE, "Vault dependency not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize Managers
        initializeManagers();

        // Register event listeners
        registerEvents();

        // Register Commands
        registerCommands();

        loadPlayerData();
    }

    @Override
    public void onDisable() {
        if (MySQLManager.getInstance() != null) {
            MySQLManager.getInstance().disconnect();
        }

        unloadPlayerData();
    }

    private void initializeManagers() {
        if (config.getBoolean("MySQL.enabled")) {
            MySQLManager.initialize(this);
        }

        EconomyManagerFactory.initialize(this);
        JobsManager.initialize();
        PlayerJobsData.initialize();
        SalaryManager.initialize(this);
    }

    private void loadPlayerData() {
        String defaultJob = JobsManager.getInstance().getDefaultJob();

        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            PlayerJobsData.getInstance().setPlayerJob(player, defaultJob);
            getLogger().log(Level.INFO, "Assigned default job '" + defaultJob + "' to player " + player.getName());
            EconomyManagerFactory.getInstance().loadBalance(player.getUniqueId());
        });
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerQuit(), this);
    }

    private void unloadPlayerData() {
        Bukkit.getServer().getOnlinePlayers().forEach(PlayerJobsData.getInstance()::removePlayerJob);
        getLogger().log(Level.INFO, "Saving all balances before shutdown.");
        EconomyManagerFactory.getInstance().saveAllBalances();
    }

    private void registerCommands() {
        new BalanceCommand();
        new PayCommand();
        new EconomyAdminCommand();
        new JobCommand();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().log(Level.SEVERE, "Vault plugin not found!");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().log(Level.SEVERE, "No Economy provider found! Registering VaultEconomyManager.");
            economy = new VaultEconomyManager(this);
            getServer().getServicesManager().register(Economy.class, economy, this, ServicePriority.Highest);
        } else {
            economy = rsp.getProvider();
        }
        return economy != null;
    }

    public FileConfiguration getConfiguration() {
        return config;
    }

    public Economy getEconomy() {
        return economy;
    }

    public Permission getPermission() {
        return permission;
    }
}

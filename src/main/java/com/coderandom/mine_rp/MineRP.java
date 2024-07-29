package com.coderandom.mine_rp;

import com.coderandom.mine_rp.managers.MySQLManager;
import com.coderandom.mine_rp.modules.economy.commands.BalanceCommand;
import com.coderandom.mine_rp.modules.economy.commands.EconomyAdminCommand;
import com.coderandom.mine_rp.modules.economy.commands.PayCommand;
import com.coderandom.mine_rp.modules.economy.listeners.OnPlayerJoinLoadBalance;
import com.coderandom.mine_rp.modules.economy.listeners.OnPlayerQuitUnloadBalance;
import com.coderandom.mine_rp.modules.economy.managers.EconomyManagerFactory;
import com.coderandom.mine_rp.modules.economy.managers.VaultEconomyManager;
import com.coderandom.mine_rp.modules.jobs.commands.JobCommand;
import com.coderandom.mine_rp.modules.jobs.data.PlayerJobsData;
import com.coderandom.mine_rp.modules.jobs.managers.JobsManager;
import com.coderandom.mine_rp.modules.jobs.managers.SalaryManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Main class for the MineRP plugin.
 * Handles initialization, configuration, and shutdown processes.
 */
public final class MineRP extends JavaPlugin {

    // Static references for global access
    public static MineRP MINE_RP;
    public static FileConfiguration CONFIG;
    public static PlayerJobsData PLAYER_JOBS_DATA;
    public static SalaryManager SALARY_MANAGER;
    public static Economy ECONOMY;

    @Override
    public void onEnable() {
        // Plugin startup logic
        MINE_RP = this;
        saveDefaultConfig();
        CONFIG = getConfig();

        // Initialize Managers
        initializeManagers();

        // Setup Vault Economy
        if (!setupEconomy()) {
            getLogger().log(Level.SEVERE, "Vault dependency not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize Jobs and Salary managers
        PLAYER_JOBS_DATA = new PlayerJobsData();
        SALARY_MANAGER = new SalaryManager(this, PLAYER_JOBS_DATA);

        // Register event listeners
        registerEvents();

        // Register Commands
        registerCommands();

        loadPlayerData();
    }

    private void initializeManagers() {
        if (CONFIG.getBoolean("MySQL.enabled")) {
            MySQLManager.initialize(this);
        }

        EconomyManagerFactory.initialize(this);
        JobsManager.initialize();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (MySQLManager.getInstance() != null) {
            MySQLManager.getInstance().disconnect();
        }

        unloadPlayerData();
    }

    private void loadPlayerData() {
        String defaultJob = JobsManager.getInstance().getDefaultJob();

        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            PLAYER_JOBS_DATA.setPlayerJob(player, defaultJob);
            getLogger().log(Level.INFO, "Assigned default job '" + defaultJob + "' to player " + player.getName());
            EconomyManagerFactory.getInstance().loadBalance(player.getUniqueId());
        });
    }

    private void unloadPlayerData() {
        Bukkit.getServer().getOnlinePlayers().forEach(PLAYER_JOBS_DATA::removePlayerJob);
        MINE_RP.getLogger().log(Level.INFO, "Saving all balances before shutdown.");
        EconomyManagerFactory.getInstance().saveAllBalances();
    }

    /**
     * Registers the event listeners for the plugin.
     */
    private void registerEvents() {
        // Register event listeners
        getServer().getPluginManager().registerEvents(new OnPlayerJoinLoadBalance(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerQuitUnloadBalance(), this);
    }

    /**
     * Registers the commands for the plugin.
     */
    private void registerCommands() {
        new BalanceCommand();
        new PayCommand();
        new EconomyAdminCommand();
        new JobCommand(PLAYER_JOBS_DATA);
    }

    /**
     * Setup Vault Economy.
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().log(Level.SEVERE, "Vault plugin not found!");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().log(Level.SEVERE, "No Economy provider found! Registering VaultEconomyManager.");
            ECONOMY = new VaultEconomyManager(this);
            getServer().getServicesManager().register(Economy.class, ECONOMY, this, ServicePriority.Highest);
        } else {
            ECONOMY = rsp.getProvider();
        }
        return ECONOMY != null;
    }
}

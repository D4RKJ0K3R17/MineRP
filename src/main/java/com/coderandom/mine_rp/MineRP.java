package com.coderandom.mine_rp;

import com.coderandom.mine_rp.listeners.OnPlayerJoin;
import com.coderandom.mine_rp.listeners.OnPlayerQuit;
import com.coderandom.mine_rp.managers.MySQLManager;
import com.coderandom.mine_rp.modules.economy.managers.EconomyManager;
import com.coderandom.mine_rp.modules.economy.managers.EconomyMySQLManager;
import com.coderandom.mine_rp.modules.economy.managers.EconomyJsonManager;
import com.coderandom.mine_rp.modules.economy.managers.VaultEconomyManager;
import com.coderandom.mine_rp.managers.SalaryManager;
import com.coderandom.mine_rp.modules.jobs.data.PlayerJobsData;
import com.coderandom.mine_rp.modules.jobs.managers.JobsManager;
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
    public static MySQLManager MYSQL_MANAGER;
    public static boolean USING_MYSQL = false;
    public static JobsManager JOBS_MANAGER;
    public static PlayerJobsData PLAYER_JOBS_DATA;
    public static SalaryManager SALARY_MANAGER;
    public static EconomyManager ECONOMY_MANAGER;
    public static Economy ECONOMY;

    @Override
    public void onEnable() {
        // Plugin startup logic
        MINE_RP = this;
        saveDefaultConfig();
        CONFIG = getConfig();

        // Setup Vault Economy
        if (!setupEconomy()) {
            getLogger().log(Level.SEVERE, "Vault dependency not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize MySQL if enabled
        initializeMySQL();

        // Fallback to JSON-based economy manager if MySQL is not used
        if (!USING_MYSQL) {
            getLogger().log(Level.INFO, "MySQL not enabled or failed to connect. Using JSON for economy data.");
            ECONOMY_MANAGER = new EconomyJsonManager(null, "balances");
        }

        // Initialize managers
        JOBS_MANAGER = new JobsManager();
        PLAYER_JOBS_DATA = new PlayerJobsData();
        SALARY_MANAGER = new SalaryManager(this, PLAYER_JOBS_DATA);

        // Register event listeners
        registerEvents();

        // Load player data on enable
        loadPlayerData();

        // Start salary payments
        startSalaryPayments();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (ECONOMY_MANAGER != null) {
            getLogger().log(Level.INFO, "Saving all balances before shutdown.");
            ECONOMY_MANAGER.saveAllBalances();
        }
        if (MYSQL_MANAGER != null) {
            MYSQL_MANAGER.disconnect();
        }
    }

    /**
     * Initializes the MySQL manager if MySQL is enabled in the config.
     */
    private void initializeMySQL() {
        if (CONFIG.getBoolean("MySQL.enabled", false)) {
            getLogger().log(Level.INFO, "Initializing MySQL...");
            String host = CONFIG.getString("MySQL.host", "localhost");
            String port = CONFIG.getString("MySQL.port", "3306");
            String database = CONFIG.getString("MySQL.database", "mine_rp");
            String username = CONFIG.getString("MySQL.username", "root");
            String password = CONFIG.getString("MySQL.password", "");

            MYSQL_MANAGER = new MySQLManager(MINE_RP, host, port, database, username, password);
            USING_MYSQL = MYSQL_MANAGER.connect();
            if (USING_MYSQL) {
                getLogger().log(Level.INFO, "Connected to MySQL database.");
                ECONOMY_MANAGER = new EconomyMySQLManager(MYSQL_MANAGER);
                ((EconomyMySQLManager) ECONOMY_MANAGER).createTables();
            } else {
                getLogger().log(Level.SEVERE, "Failed to connect to MySQL database. Defaulting to JSON.");
            }
        }
    }

    /**
     * Registers the event listeners for the plugin.
     */
    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerQuit(), this);
    }

    /**
     * Loads player data (balances and job assignments) when the plugin is enabled.
     */
    private void loadPlayerData() {
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            ECONOMY_MANAGER.loadBalance(player.getUniqueId());
            String defaultJob = CONFIG.getString("default_job", "citizen");
            PLAYER_JOBS_DATA.setPlayerJob(player.getUniqueId(), defaultJob);
            getLogger().log(Level.INFO, "Assigned default job '" + defaultJob + "' to player " + player.getName());
        });
    }

    /**
     * Starts the salary payments based on the configured frequency.
     */
    private void startSalaryPayments() {
        int salaryFrequencyMinutes = CONFIG.getInt("salary_frequency", 20);
        long salaryFrequencyTicks = (long) salaryFrequencyMinutes * 60 * 20;
        getLogger().log(Level.INFO, "Starting salary payments every " + salaryFrequencyMinutes + " minutes (" + salaryFrequencyTicks + " ticks).");
        SALARY_MANAGER.startSalaryPayments(salaryFrequencyTicks);
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

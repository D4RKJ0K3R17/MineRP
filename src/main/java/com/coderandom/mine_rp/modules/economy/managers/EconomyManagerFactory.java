package com.coderandom.mine_rp.modules.economy.managers;

import com.coderandom.mine_rp.managers.MySQLManager;
import org.bukkit.plugin.Plugin;

public class EconomyManagerFactory {
    private static volatile EconomyManager instance;

    public static void initialize(Plugin plugin) {
        if (instance == null) {
            synchronized (EconomyManagerFactory.class) {
                if (instance == null) {
                    if (plugin.getConfig().getBoolean("MySQL.enabled", false)) {
                        MySQLManager mySQLManager = MySQLManager.getInstance();
                        if (mySQLManager.connect()) {
                            EconomyMySQLManager economyMySQLManager = new EconomyMySQLManager(mySQLManager);
                            economyMySQLManager.createTables();
                            instance = economyMySQLManager;
                        } else {
                            plugin.getLogger().severe("Failed to connect to MySQL database. Defaulting to JSON.");
                            instance = new EconomyJsonManager();
                        }
                    } else {
                        instance = new EconomyJsonManager();
                    }
                }
            }
        }
    }

    public static EconomyManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("EconomyManager has not been initialized. Call initialize() first.");
        }
        return instance;
    }
}

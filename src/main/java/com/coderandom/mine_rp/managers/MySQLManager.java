package com.coderandom.mine_rp.managers;

import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.logging.Level;

public class MySQLManager {
    private static volatile MySQLManager instance;
    private static Plugin plugin;
    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String password;
    protected Connection connection;

    private MySQLManager() {
        this.host = plugin.getConfig().getString("MySQL.host");
        this.port = plugin.getConfig().getString("MySQL.port");
        this.database = plugin.getConfig().getString("MySQL.database");
        this.username = plugin.getConfig().getString("MySQL.username");
        this.password = plugin.getConfig().getString("MySQL.password");
    }

    public static void initialize(Plugin pluginInstance) {
        plugin = pluginInstance;
    }

    public static synchronized MySQLManager getInstance() {
        if (instance == null) {
            synchronized (MySQLManager.class) {
                if (instance == null) {
                    instance = new MySQLManager();
                }
            }
        }
        return instance;
    }

    public boolean connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return false;
            }
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
            connection = DriverManager.getConnection(url, username, password);
            plugin.getLogger().log(Level.INFO, "Connected to MySQL database.");
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not connect to MySQL database!", e);
            plugin.getLogger().log(Level.SEVERE, "Host: " + host);
            plugin.getLogger().log(Level.SEVERE, "Port: " + port);
            plugin.getLogger().log(Level.SEVERE, "Database: " + database);
            plugin.getLogger().log(Level.SEVERE, "Username: " + username);
            return false;
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().log(Level.INFO, "MySQL Disconnected!");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not disconnect from MySQL database!", e);
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not reconnect to MySQL database!", e);
        }
        return connection;
    }

    public ResultSet executeQuery(String query, Object... parameters) throws SQLException {
        validateConnection();
        PreparedStatement ps = connection.prepareStatement(query);
        setParameters(ps, parameters);
        return ps.executeQuery();
    }

    public int executeUpdate(String query, Object... parameters) throws SQLException {
        validateConnection();
        PreparedStatement ps = connection.prepareStatement(query);
        setParameters(ps, parameters);
        return ps.executeUpdate();
    }

    private void validateConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
    }

    private void setParameters(PreparedStatement ps, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            ps.setObject(i + 1, parameters[i]);
        }
    }

    public void createTables(String tableCreationQuery) {
        try {
            executeUpdate(tableCreationQuery);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create table!", e);
        }
    }
}

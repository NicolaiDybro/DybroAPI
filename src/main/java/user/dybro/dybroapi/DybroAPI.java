package user.dybro.dybroapi;

import org.bukkit.plugin.java.JavaPlugin;
import user.dybro.dybroapi.Database.MySQLConnectionManager;

public final class DybroAPI extends JavaPlugin {
    private static DybroAPI instance;
    private MySQLConnectionManager connectionManager;

    @Override
    public void onEnable() {
        getLogger().info("DybroAPI is starting...");
        instance = this;

        // Create config
        getLogger().info("Saving default config...");
        this.saveDefaultConfig();

        // Get everything from config
        getLogger().info("Reading configuration...");
        final String host = this.getConfig().getString("mysql.host");
        final int port = this.getConfig().getInt("mysql.port");
        final String database = this.getConfig().getString("mysql.database");
        final String username = this.getConfig().getString("mysql.username");
        final String password = this.getConfig().getString("mysql.password");
        final boolean useSSL = this.getConfig().getBoolean("mysql.useSSL");

        // Initialize connection manager
        getLogger().info("Initializing connection manager...");
        this.connectionManager = MySQLConnectionManager.getInstance(host, port, database, username, password, useSSL);
        this.connectionManager.connect();

        getLogger().info("§aConnected to MySQL database");
        getLogger().info("§aDybroAPI is enabled successfully");
        getLogger().info("DybroAPI instance: " + instance);
    }

    @Override
    public void onDisable() {
        getLogger().info("DybroAPI is shutting down...");
        instance = null;
        getLogger().info("DybroAPI is disabled successfully");
    }

    public static DybroAPI getInstance() {
        return instance;
    }

    // Getters
    public MySQLConnectionManager getConnectionManager() {
        return connectionManager;
    }
}

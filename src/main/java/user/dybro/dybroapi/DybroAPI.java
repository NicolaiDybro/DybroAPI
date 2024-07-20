package user.dybro.dybroapi;

import org.bukkit.plugin.java.JavaPlugin;
import user.dybro.dybroapi.Database.MySQLConnectionManager;

public final class DybroAPI extends JavaPlugin {
    private static DybroAPI instance;
    private MySQLConnectionManager connectionManager;
    private MySQLConnectionManager sqlManager;
    @Override
    public void onEnable() {
        instance = this;

        // Create config
        this.saveDefaultConfig();

        // Get everything from config
        final String host = this.getConfig().getString("mysql.host");
        final int port = this.getConfig().getInt("mysql.port");
        final String database = this.getConfig().getString("mysql.database");
        final String username = this.getConfig().getString("mysql.username");
        final String password = this.getConfig().getString("mysql.password");
        final boolean useSSL = this.getConfig().getBoolean("mysql.useSSL");

        // Initialize connection manager
        this.connectionManager = MySQLConnectionManager.getInstance(host, port, database, username, password, useSSL);
        this.connectionManager.connect();
        this.getLogger().info("§aConnected to MySQL database");

        // Plugin startup logic
        this.getLogger().info("§aDybroAPI is enabled");
    }

    @Override
    public void onDisable() {
        instance = null;
        // Plugin shutdown logic
        this.getLogger().info("§cDybroAPI is disabled");
    }

    public static DybroAPI getInstance() {
        return instance;
    }

    // Getters
    public MySQLConnectionManager getConnectionManager() {
        return connectionManager;
    }

    // Getters
    public MySQLConnectionManager getSqlManager() {
        return sqlManager;
    }

}

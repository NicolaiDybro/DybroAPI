package user.dybro.dybroapi;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import user.dybro.dybroapi.Database.MySQLConnectionManager;
import user.dybro.dybroapi.SpiGUI.SpiGUI;

import java.util.Objects;

public final class DybroAPI extends JavaPlugin {
    private static DybroAPI instance;
    public static SpiGUI spiGUI;
    private MySQLConnectionManager connectionManager;

    @Override
    public void onLoad() {
        if (instance != null) throw new RuntimeException();
        instance = this;
    }


    @Override
    public void onEnable() {

        //
        spiGUI = new SpiGUI(this);

        getLogger().info("DybroAPI is starting...");

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

    public static @NotNull DybroAPI getInstance() {
        return Objects.requireNonNull(instance);
    }

    public static SpiGUI getSpiGUI() {
        return spiGUI;
    }

    // Getters
    public MySQLConnectionManager getConnectionManager() {
        return connectionManager;
    }
}

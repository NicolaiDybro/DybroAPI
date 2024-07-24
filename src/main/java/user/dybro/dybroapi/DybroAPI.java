package user.dybro.dybroapi;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import user.dybro.dybroapi.Database.MySQLConnectionManager;
import user.dybro.dybroapi.SpiGUI.SpiGUI;
import user.dybro.dybroapi.SpiGUI.buttons.SGButton;
import user.dybro.dybroapi.SpiGUI.item.ItemBuilder;
import user.dybro.dybroapi.SpiGUI.menu.SGMenu;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

public final class DybroAPI extends JavaPlugin {
    private static DybroAPI instance;
    private static Economy econ = null;
    public static SpiGUI spiGUI;
    private MySQLConnectionManager connectionManager;
    private static final String KINGDOM = "§8[§x§f§8§a§0§1§c§lK§x§f§1§f§1§6§f§lI§8]";

    @Override
    public void onLoad() {
        if (instance != null) throw new RuntimeException();
        instance = this;
    }


    @Override
    public void onEnable() {
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


        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("DybroAPI is shutting down...");
        instance = null;

        // Disconnect
        try {
            connectionManager.disconnect();
            getLogger().info("Disconnected from MySQL database");
        } catch (SQLException e) {
            getLogger().severe("Failed to disconnect from MySQL database");
            getLogger().log(Level.SEVERE, "Disconnection error:", e);
        }

        getLogger().info("DybroAPI is disabled successfully");
    }

    public static @NotNull DybroAPI getInstance() {
        return Objects.requireNonNull(instance);
    }

    public static SpiGUI getSpiGUI() {
        return spiGUI;
    }

    // Glass border
    public void addBorder(SGMenu menu, Material material) {
        ItemStack glassPane = new ItemBuilder(material).name(" ").build();
        for (int i = 0; i < 9; i++) {
            menu.setButton(i, new SGButton(glassPane));
            menu.setButton(45 + i, new SGButton(glassPane));
        }
        for (int i = 1; i < 5; i++) {
            menu.setButton(i * 9, new SGButton(glassPane));
            menu.setButton(i * 9 + 8, new SGButton(glassPane));
        }
    }

    // Getters
    public MySQLConnectionManager getConnectionManager() {
        return connectionManager;
    }

    // Vault economy setup
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }
    public static Economy getEconomy() {
        return econ;
    }

    public static String getKingdom() {
        return KINGDOM;
    }
}

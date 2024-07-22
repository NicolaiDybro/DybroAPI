package user.dybro.dybroapi.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;
import user.dybro.dybroapi.DybroAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLConnectionManager {
    private Plugin plugin = DybroAPI.getInstance();
    private static MySQLConnectionManager instance;
    private HikariDataSource hikari;
    private boolean initializedSuccessfully;

    // Constructor for MySQLConnectionManager class
    private MySQLConnectionManager(String host, int port, String database, String username, String password, boolean useSSL) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(3);
        config.setConnectionTestQuery("SELECT 1");
        config.setIdleTimeout(60000);  // 60 seconds
        config.setMaxLifetime(1800000); // 30 minutes
        config.setConnectionTimeout(30000); // 30 seconds
        config.setLeakDetectionThreshold(2000); // 2 seconds

        // Initialize HikariDataSource object
        try {
            plugin.getLogger().info("§aInitializing HikariDataSource...");
            this.hikari = new HikariDataSource(config);
            this.initializedSuccessfully = true;
            plugin.getLogger().info("§aHikariDataSource initialized successfully.");
        } catch (Exception ex) {
            this.initializedSuccessfully = false;
            plugin.getLogger().severe("§cFailed to initialize HikariDataSource. Please check your configuration.");
            ex.printStackTrace();
        }
    }

    // Get instance of MySQLConnectionManager class
    public static MySQLConnectionManager getInstance(String host, int port, String database, String username, String password, boolean useSSL) {
        if (instance == null) {
            instance = new MySQLConnectionManager(host, port, database, username, password, useSSL);
        }
        return instance;
    }

    // Connect to MySQL database and get connection object
    public void connect() {
        if (!initializedSuccessfully) {
            plugin.getLogger().severe("§cFailed to initialize HikariDataSource. Please check your configuration.");
            return;
        }
        try (Connection connection = this.hikari.getConnection()) {
            plugin.getLogger().info("§aConnected to MySQL database successfully.");
        } catch (SQLException ex) {
            plugin.getLogger().severe("§cFailed to connect to MySQL database. Please check your configuration.");
            ex.printStackTrace();
        }
    }

    // Disconnect from MySQL database and close connection
    public void disconnect() throws SQLException {
        if (hikari != null && !hikari.isClosed()) {
            hikari.close();
        }
    }

    // Get connection object
    public Connection getConnection() throws SQLException {
        return hikari.getConnection();
    }

    // Execute query and return result
    public void executeUpdate(String query) throws SQLException {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        }
    }
}

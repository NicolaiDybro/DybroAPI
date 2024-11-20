package user.dybro.dybroapi.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.bukkit.plugin.Plugin;
import user.dybro.dybroapi.DybroAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MySQLConnectionManager {
    private Plugin plugin = DybroAPI.getInstance();
    private static volatile MySQLConnectionManager instance;
    private HikariDataSource hikari;
    private boolean initializedSuccessfully;

    // Constructor for MySQLConnectionManager class
    private MySQLConnectionManager(String host, int port, String database, String username, String password, boolean useSSL) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL);
        config.setUsername(username);
        config.setPassword(password);
        config.setConnectionTestQuery("SELECT 1");

        // Additional configurations
        config.setConnectionTimeout(30000); // 30 seconds
        config.setIdleTimeout(600000); // 10 minutes
        config.setMaxLifetime(1800000); // 30 minutes
        config.setMaximumPoolSize(50); // Increase pool size as needed
        config.setLeakDetectionThreshold(2000); // 2 seconds to detect connection leaks
        config.setPoolName("DybroAPI-HikariCP");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        try {
            plugin.getLogger().info("§aInitializing HikariDataSource...");
            this.hikari = new HikariDataSource(config);
            this.initializedSuccessfully = true;
            plugin.getLogger().info("§aHikariDataSource initialized successfully.");
            startLoggingPoolMetrics();
        } catch (Exception ex) {
            this.initializedSuccessfully = false;
            plugin.getLogger().severe("§cFailed to initialize HikariDataSource. Please check your configuration.");
            ex.printStackTrace();
        }
    }

    // Get instance of MySQLConnectionManager class
    public static MySQLConnectionManager getInstance(String host, int port, String database, String username, String password, boolean useSSL) {
        if (instance == null) {
            synchronized (MySQLConnectionManager.class) {
                if (instance == null) {
                    instance = new MySQLConnectionManager(host, port, database, username, password, useSSL);
                }
            }
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
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        }
    }

    // Log pool metrics periodically
    private void startLoggingPoolMetrics() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::logCurrentPoolState, 1, 1, TimeUnit.MINUTES);
    }

    // Log the current state of the connection pool
    private void logCurrentPoolState() {
        if (hikari != null && hikari.isRunning()) {
            HikariPoolMXBean poolMXBean = hikari.getHikariPoolMXBean();
            int totalConnections = poolMXBean.getTotalConnections();
            int activeConnections = poolMXBean.getActiveConnections();
            int idleConnections = poolMXBean.getIdleConnections();
            int threadsAwaitingConnection = poolMXBean.getThreadsAwaitingConnection();

            plugin.getLogger().info("Total connections: " + totalConnections);
            plugin.getLogger().info("Active connections: " + activeConnections);
            plugin.getLogger().info("Idle connections: " + idleConnections);
            plugin.getLogger().info("Threads awaiting connection: " + threadsAwaitingConnection);
        } else {
            plugin.getLogger().severe("HikariDataSource is not initialized or not running.");
        }
    }
}

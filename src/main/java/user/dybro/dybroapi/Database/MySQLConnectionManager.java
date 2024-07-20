package user.dybro.dybroapi.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;
import user.dybro.dybroapi.DybroAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


// MySQLConnectionManager class
// This class is responsible for managing the connection to the MySQL database
public class MySQLConnectionManager {
    private Plugin plugin = DybroAPI.getInstance();
    private static MySQLConnectionManager instance;
    private Connection connection;
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

        // Initialize HikariDataSource object
        try {
            plugin.getLogger().info("§aInitializing HikariDataSource...");
            this.hikari = new HikariDataSource(config);
            this.initializedSuccessfully = true;
            plugin.getLogger().info("§aHikariDataSource initialized successfully.");
        } catch (Exception ex) {
            this.initializedSuccessfully = false;
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
        try {
            this.connection = this.hikari.getConnection();
        } catch (SQLException ex) {
            plugin.getLogger().severe("§cFailed to connect to MySQL database. Please check your configuration.");
            ex.printStackTrace();
        }
    }

    // Disconnect from MySQL database and close connection
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Get connection object
    public Connection getConnection() {
        return connection;
    }

    // Execute query and return result
    public void executeUpdate(String query) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        }
    }
}
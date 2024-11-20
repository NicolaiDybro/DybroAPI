package user.dybro.dybroapi.Database;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.spi.Stoppable;
import user.dybro.dybroapi.DybroAPI;

import java.sql.Connection;
import java.sql.SQLException;

public class DybroAPIConnectionProvider implements ConnectionProvider, Stoppable {

    @Override
    public Connection getConnection() throws SQLException {
        return DybroAPI.getInstance().getConnectionManager().getConnection();
    }

    @Override
    public void closeConnection(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public void stop() {
        // Luk forbindelser, hvis nødvendigt
    }

    @Override
    public boolean isUnwrappableAs(Class<?> aClass) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return null;
    }
}

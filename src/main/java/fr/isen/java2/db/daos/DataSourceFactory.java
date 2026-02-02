package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class DataSourceFactory {

    private static final String URL = "jdbc:sqlite:sqlite.db";

    private DataSourceFactory() {
        throw new IllegalStateException("This is a static class");
    }

    /**
     * Pour le Bonus 1 : On crée une implémentation légère de DataSource 
     * qui utilise DriverManager, sans aucune classe spécifique à SQLite.
     */
    public static DataSource getDataSource() {
        return new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                return DataSourceFactory.getConnection();
            }
            @Override public Connection getConnection(String u, String p) throws SQLException { return null; }
            @Override public PrintWriter getLogWriter() throws SQLException { return null; }
            @Override public void setLogWriter(PrintWriter out) throws SQLException {}
            @Override public void setLoginTimeout(int seconds) throws SQLException {}
            @Override public int getLoginTimeout() throws SQLException { return 0; }
            @Override public Logger getParentLogger() { return null; }
            @Override public <T> T unwrap(Class<T> iface) throws SQLException { return null; }
            @Override public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
        };
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
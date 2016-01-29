package org.sentinel.instrumentationserver;

import java.sql.*;

/**
 * Base class for all data access objects for the server.
 */
public abstract class DAOBase {

    /**
     * The Connection to the database.
     */
    protected Connection databaseConnection = null;


    /**
     * Initialize the database.
     */
    public void initializeDatabase() {
        Statement statement;
        connectToDatabase();
        System.out.println("Opened database successfully");

        try {
            statement = databaseConnection.createStatement();

            statement.executeUpdate(QueryBuilder.SQL_STATEMENT_CREATE_TABLE_APKS_IF_NOT_EXISTS);
            statement.executeUpdate(QueryBuilder.SQL_STATEMENT_CREATE_TABLE_METADATA_IF_NOT_EXISTS);
            statement.close();
            databaseConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set up database connection to interact with the database.
     */
    public void connectToDatabase() {
        try {
            if (databaseConnection == null || databaseConnection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                databaseConnection = DriverManager.getConnection("jdbc:sqlite:instrumentation-server-database.db");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * End an interaction with the database.
     */
    public void disconnectFromDatabase() {
        try {
            databaseConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get the dataset ID in the database from the SHA 512 hash of the uninstrumented version of an APK.
     */
    protected long getApkId(String sha512Hash) {
        connectToDatabase();

        String sqlStatementGetApkIdFromHash = QueryBuilder.getQueryToGetApkIdFromHash();
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(sqlStatementGetApkIdFromHash);
            preparedStatement.setString(1, sha512Hash);
            ResultSet resultSet = preparedStatement.executeQuery();
            long id = resultSet.getLong(1);
            resultSet.close();
            preparedStatement.close();
            databaseConnection.close();
            return id;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}

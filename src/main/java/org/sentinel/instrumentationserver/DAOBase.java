package org.sentinel.instrumentationserver;

import java.sql.*;

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

        // TODO make this parameterized.
        try {
            statement = databaseConnection.createStatement();

            // Used to test with fresh database.
            //statement.executeUpdate(QueryBuilder.SQL_STATEMENT_DROP_TABLE_APKS);
            // Used to test with fresh database.
            //statement.executeUpdate(QueryBuilder.SQL_STATEMENT_DROP_TABLE_METADATA);
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

    public void disconnectFromDatabase() {
        try {
            databaseConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected long getApkId(String sha512Hash) {
        connectToDatabase();

        String sqlStatementGetApkIdFromHash = QueryBuilder.getQueryGetApkIdFromHash();
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

        //TODO handle this better
        return -1;
    }
}

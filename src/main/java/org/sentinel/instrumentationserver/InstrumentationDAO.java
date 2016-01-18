package org.sentinel.instrumentationserver;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** This class manages the database connections.*/
public class InstrumentationDAO {

    /** The database manager.*/
    private static InstrumentationDAO InstrumentationDAO;

    /** The Connection to the database.*/
    private Connection databaseConnection = null;

    protected InstrumentationDAO() {

    }

    /** Singleton pattern.*/
    public static InstrumentationDAO getInstance() {
        if (InstrumentationDAO == null) {
            InstrumentationDAO = new InstrumentationDAO();
        }

        return InstrumentationDAO;
    }

    /** Retrieve the hashes of all instrumented APKs in the database.*/
    public List<String> getAllInstrumentedApkHashes() {
        connectToDatabase();
        List<String> instrumentedApkHashes = new ArrayList<String>();
        try {
            Statement statement;
            statement = databaseConnection.createStatement();
            String sqlStatementGetAllApkPackageNamesAndHashes = "SELECT HASH " +
                    "FROM APKS;";

            ResultSet resultSet = statement.executeQuery(sqlStatementGetAllApkPackageNamesAndHashes);

            while (resultSet.next()) {
                instrumentedApkHashes.add(resultSet.getString("HASH"));
            }
            resultSet.close();
            statement.close();
            databaseConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(instrumentedApkHashes);

        return instrumentedApkHashes;
    }

    /** Store the APK and its hash in the database.*/
    public void saveInstrumentedApkToDatabase(String instrumentedApkPath, String sha512Hash) {
        connectToDatabase();

        try {
            String sqlStatementGetAllApkPackageNamesAndHashes = QueryBuilder.getQueryToInsertInstrumentedApkIntoDatabase();
            InputStream inputstream = new FileInputStream((new File(instrumentedApkPath)));
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                    sqlStatementGetAllApkPackageNamesAndHashes);

            preparedStatement.setString(1, sha512Hash);
            preparedStatement.setBytes(2, IOUtils.toByteArray(inputstream));
            preparedStatement.execute();

            preparedStatement.close();
            databaseConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /** Retrieves the binary blob of an APK by its hash.*/
    public byte[] retrieveInstrumentedApkFromDatabase(String apkHash) {
        connectToDatabase();

        try {
            String sqlStatementGetApkFromHash = QueryBuilder.getQueryToRetrieveApkFile();
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(sqlStatementGetApkFromHash);
            preparedStatement.setString(1, apkHash);

            ResultSet resultSet = preparedStatement.executeQuery();

            byte[] apk = resultSet.getBytes(1);
            resultSet.close();
            preparedStatement.close();
            databaseConnection.close();
            return apk;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Checks if the APK has already been instrumented and saved to the database. If not, an instrumentation
    * will be run.*/
    public boolean checkIfApkAlreadyInstrumented(String sha512Hash) {
        boolean alreadyInstrumented = false;
        connectToDatabase();
        String sqlStatementCheckIfApkAlreadyInstrumented = QueryBuilder.getQueryToCheckIfApkAlreadyInstrumented();
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(sqlStatementCheckIfApkAlreadyInstrumented);
            preparedStatement.setString(1, sha512Hash);

            ResultSet resultSet = preparedStatement.executeQuery();

            //Check if the ResultSet contains results first by calling next()
            if (resultSet.next() && resultSet.getString(1).equals(sha512Hash)) {
                alreadyInstrumented = true;
            }
            resultSet.close();
            preparedStatement.close();
            databaseConnection.close();
            return alreadyInstrumented;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alreadyInstrumented;
    }

    /** Initialize the database.*/
    public void initializeDatabase() {
        Statement statement;
        connectToDatabase();
        System.out.println("Opened database successfully");

        // TODO make this parameterized.
        try {
            statement = databaseConnection.createStatement();

            // Used to test with fresh database.
            //statement.executeUpdate(QueryBuilder.SQL_STATEMENT_DROP_TABLE);
            statement.executeUpdate(QueryBuilder.SQL_STATEMENT_CREATE_TABLE_IF_NOT_EXISTS);
            statement.close();
            databaseConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Set up database connection to interact with the database. */
    public void connectToDatabase() {
        try {
            if (databaseConnection == null || databaseConnection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                databaseConnection = DriverManager.getConnection("jdbc:sqlite:test.db");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
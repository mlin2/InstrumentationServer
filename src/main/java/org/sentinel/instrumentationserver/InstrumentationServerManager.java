package org.sentinel.instrumentationserver;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstrumentationServerManager {

    private static InstrumentationServerManager instrumentationServerManager;
    private Connection databaseConnection = null;
    public static final String APK_URL = "localhost:8080/myapp/";

    protected InstrumentationServerManager() {

    }

    public static InstrumentationServerManager getInstance() {
        if (instrumentationServerManager == null) {
            instrumentationServerManager = new InstrumentationServerManager();
        }

        return instrumentationServerManager;
    }

    public List<String> getAllInstrumentedApkHashes() {
        setDatabaseConnection();
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

    public void setupDatabase() {
        Statement statement = null;

        setDatabaseConnection();

        System.out.println("Opened database successfully");

        // TODO make this parameterized.
        try {
            statement = databaseConnection.createStatement();

            statement.executeUpdate(DAO.SQL_STATEMENT_DROP_TABLE);
            statement.executeUpdate(DAO.SQL_STATEMENT_CREATE_TABLE);
            statement.close();
            databaseConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void setDatabaseConnection() {
        try {
            if (databaseConnection == null || databaseConnection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                databaseConnection = DriverManager.getConnection("jdbc:sqlite:test.db");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean saveApk(String instrumentedApkPath, String sha512Hash) {

        try {
            saveInstrumentedApkToDatabase(instrumentedApkPath, sha512Hash);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public void saveInstrumentedApkToDatabase(String instrumentedApkPath, String sha512Hash) {

        setDatabaseConnection();

        try {
            String sqlStatementGetAllApkPackageNamesAndHashes = DAO.getQueryToInsertInstrumentedApkIntoDatabase(sha512Hash);

            InputStream inputstream = new FileInputStream( (new File(instrumentedApkPath)));

            PreparedStatement preparedStatement = databaseConnection.prepareStatement(sqlStatementGetAllApkPackageNamesAndHashes);
            preparedStatement.setBytes(1, IOUtils.toByteArray(inputstream));
            preparedStatement.execute();

            preparedStatement.close();
            databaseConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public byte[] retrieveInstrumentedApkFromDatabase(String apkHash) {
        setDatabaseConnection();

        try {
            String sqlStatementGetApkFromHash = DAO.getQueryToRetrieveApkFile(apkHash);
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
}
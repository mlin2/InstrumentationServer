package org.sentinel.instrumentationserver;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import java.io.InputStream;
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
            statement.executeUpdate(DAO.SQL_STATEMENT_INSERT_APK);
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

    public boolean handleMultipartPost(InputStream sourceFile, InputStream sinkFile, InputStream easyTaintWrapperSource, InputStream apkFile) {
        InstrumentationRunner instrumentationRunner = new InstrumentationRunner();
/*            if (!isMimeMultipartOK(mimeMultipart)) {
                return false;
            }*/

            instrumentationRunner.run(sourceFile,
                    sinkFile, easyTaintWrapperSource,
                    apkFile);

        return true;
    }

    //TODO make this more smart
    private boolean isMimeMultipartOK(MimeMultipart mimeMultipart) {
        try {
            if (mimeMultipart.getCount() != 4) {
                return false;
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return true;
    }

}
package org.sentinel.instrumentationserver.instrumentation;

import org.sentinel.instrumentationserver.DAOBase;
import org.sentinel.instrumentationserver.QueryBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the database connections.
 */
public class InstrumentationDAO extends DAOBase {

    /**
     * The database manager.
     */
    private static InstrumentationDAO InstrumentationDAO;

    protected InstrumentationDAO() {
    }


    /**
     * Singleton pattern.
     */
    public static InstrumentationDAO getInstance() {
        if (InstrumentationDAO == null) {
            InstrumentationDAO = new InstrumentationDAO();
        }

        return InstrumentationDAO;
    }

    /**
     * Store the APK and its hash in the database.
     */
    public void saveInstrumentedApkToDatabase(byte[] apkBytes, String sha512Hash, String sha256hash) {
        connectToDatabase();

        try {
            String sqlStatementGetAllApkPackageNamesAndHashes = QueryBuilder.getQueryToInsertInstrumentedApk();
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                    sqlStatementGetAllApkPackageNamesAndHashes);

            preparedStatement.setString(1, sha512Hash);
            preparedStatement.setBytes(2, apkBytes);
            preparedStatement.execute();
            preparedStatement.close();

            databaseConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the binary blob of an APK by its hash.
     */
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

    /**
     * Checks if the APK has already been instrumented and saved to the database. If not, an instrumentation
     * will be run.
     */
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
            disconnectFromDatabase();
            return alreadyInstrumented;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alreadyInstrumented;
    }


    /**
     * Retrieve the hashes of all instrumented APKs in the database.
     */
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
            disconnectFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return instrumentedApkHashes;
    }
}
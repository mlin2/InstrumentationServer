package org.sentinel.instrumentationserver;

import org.apache.commons.io.IOUtils;
import org.sentinel.instrumentationserver.generated.model.MetadataList;
import org.sentinel.instrumentationserver.generated.model.Metadatum;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the database connections.
 */
public class InstrumentationDAO {

    /**
     * The database manager.
     */
    private static InstrumentationDAO InstrumentationDAO;

    /**
     * The Connection to the database.
     */
    private Connection databaseConnection = null;

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
            databaseConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(instrumentedApkHashes);

        return instrumentedApkHashes;
    }

    /**
     * Store the APK and its hash in the database.
     */
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

    public void saveMetadataForInstrumentedApk(byte[] logo, String appName, String packageName, String sha512Hash) {
        connectToDatabase();
        long apkId = getApkId(sha512Hash);
        String sqlStatementSaveMetadataForInstrumentedApk = QueryBuilder.getQuerySaveMetadataForInstrumentedApk();

        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(sqlStatementSaveMetadataForInstrumentedApk);
            preparedStatement.setBytes(1, logo);
            preparedStatement.setString(2, appName);
            preparedStatement.setString(3, packageName);
            preparedStatement.setLong(4, apkId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private long getApkId(String sha512Hash) {
        connectToDatabase();

        String sqlStatementGetApkIdFromHash = QueryBuilder.getQueryGetApkIdFromHash();
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(sqlStatementGetApkIdFromHash);
            preparedStatement.setString(1, sha512Hash);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.getLong(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //TODO handle this better
        return -1;
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
            databaseConnection.close();
            return alreadyInstrumented;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alreadyInstrumented;
    }

    /**
     * Get the metadata of all instrumented apps saved on the server.
     */
    public MetadataList getAllMetadata() {
        connectToDatabase();
        //List<Long> metadataIdList = getAllDatabaseMetadataIds();

        String sqlStatementGetAllMetadata = QueryBuilder.getQueryGetAllMetadata();
        try {

            Statement statement = databaseConnection.createStatement();

            List<Metadatum> metadataList = new ArrayList<Metadatum>();
            ResultSet resultSet = statement.executeQuery(sqlStatementGetAllMetadata);

            while (resultSet.next()) {
                //TODO make URL parameterized
                Metadatum metadatum = new Metadatum().withLogoUrl("localhost:8080/metadata/logo/" + resultSet.getString("HASH")).
                        withAppName(resultSet.getString("APPNAME")).withPackageName(resultSet.getString("PACKAGENAME"))
                        .withAppUrl(resultSet.getString("APPURL")).withHash(resultSet.getString("HASH"))
                        .withSummary(resultSet.getString("SUMMARY")).withDescription(resultSet.getString("DESCRIPTION"))
                        .withLicense(resultSet.getString("LICENSE")).withAppCategory(resultSet.getString("APPCATEGORY"))
                        .withWebLink(resultSet.getString("WEBLINK")).withSourceCodeLink(resultSet.getString("SOURCECODELINK"))
                        .withMarketVersion(resultSet.getString("MARKETVERSION")).withSha256hash(resultSet.getString("SHA256HASH"))
                        .withSizeInBytes(resultSet.getDouble("SIZEINBYTES")).withSdkVersion(resultSet.getString("SDKVERSION"))
                        .withPermissions(resultSet.getString("PERMISSIONS")).withFeatures(resultSet.getString("FEATURES"));
                metadataList.add(metadatum);
            }

            return new MetadataList().withMetadata(metadataList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

/*    private List<Long> getAllDatabaseMetadataIds() {
        connectToDatabase();
        String sqlQueryListAllMetadataIds = QueryBuilder.getQueryListAllMetadataIds();

        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement();
            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.get


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }*/


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
                databaseConnection = DriverManager.getConnection("jdbc:sqlite:test.db");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
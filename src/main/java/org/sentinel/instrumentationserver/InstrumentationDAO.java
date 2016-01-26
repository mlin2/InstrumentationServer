package org.sentinel.instrumentationserver;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.*;
import org.sentinel.instrumentationserver.generated.model.MetadataList;
import org.sentinel.instrumentationserver.generated.model.Metadatum;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
                String sha512Hash = getSha512Hash(resultSet.getDouble("APKID"));
                Metadatum metadatum = new Metadatum().withLogoUrl(Main.FORWARDED_URI + "metadata/logo/" + sha512Hash + ".png").
                        withAppName(resultSet.getString("APPNAME")).withPackageName(resultSet.getString("PACKAGENAME"))
                        .withAppUrl(resultSet.getString("APPURL")).withHash(sha512Hash)
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

    private String getSha512Hash(double apkid) {
        connectToDatabase();

        String sqlStatementGetSha512HashFromApkId = QueryBuilder.getQueryGetSha512HashFromApkId();

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = databaseConnection.prepareStatement(sqlStatementGetSha512HashFromApkId);
            preparedStatement.setDouble(1, apkid);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.getString("HASH");

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
                databaseConnection = DriverManager.getConnection("jdbc:sqlite:instrumentation-server-database.db");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] retrieveLogoFromDatabase(String apkHash) {
        connectToDatabase();

        try {
            String sqlStatementGetLogoFromHash = QueryBuilder.getQueryToRetrieveLogoFile();
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(sqlStatementGetLogoFromHash);
            preparedStatement.setString(1, apkHash);

            ResultSet resultSet = preparedStatement.executeQuery();

            byte[] logo = resultSet.getBytes(1);
            resultSet.close();
            preparedStatement.close();
            databaseConnection.close();
            return logo;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveMetadataFromXml(Node applicationNode) {
        String LOGO_BASE_URI = "https://f-droid.org/repo/icons/";
        String APP_BASE_URI = "https://f-droid.org/repo/";
        connectToDatabase();
        String sqlStatementGetMetadataFromXml = QueryBuilder.getQueryToSaveMetadataFromXml();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = databaseConnection.prepareStatement(sqlStatementGetMetadataFromXml);

            Element applicationNodeElement = null;
            if (applicationNode instanceof Element) {
                applicationNodeElement = (Element) applicationNode;
            }

            String logo = null;
            String appName = null;
            String packageName = null;
            String appUrl = null;
            String summary = null;
            String description = null;
            String license = null;
            String appcategory = null;
            String webLink = null;
            String sourceCodeLink = null;
            String marketVersion = null;
            String sha256Hash = null;
            double sizeInBytes = 0;
            String sdkVersion = null;
            String permissions = null;
            String features = null;

            if (applicationNodeElement.getElementsByTagName("icon").item(0) != null) {
                logo = LOGO_BASE_URI + applicationNodeElement.getElementsByTagName("icon").item(0).getTextContent();
            }
            if (applicationNodeElement.getElementsByTagName("name").item(0) != null) {
                appName = applicationNodeElement.getElementsByTagName("name").item(0).getTextContent();
            }
            if (applicationNodeElement.getElementsByTagName("apkname").item(0) != null) {
                packageName = applicationNodeElement.getElementsByTagName("apkname").item(0).getTextContent();
            }
            if (applicationNodeElement.getElementsByTagName("apkname").item(0) != null) {
                appUrl = APP_BASE_URI + applicationNodeElement.getElementsByTagName("apkname").item(0).getTextContent();
            }
            if (applicationNodeElement.getElementsByTagName("summary").item(0) != null) {
                summary = applicationNodeElement.getElementsByTagName("summary").item(0).getTextContent();
            }
            if (applicationNodeElement.getElementsByTagName("desc").item(0) != null) {
                description = applicationNodeElement.getElementsByTagName("desc").item(0).getTextContent();
            }
            if (applicationNodeElement.getElementsByTagName("license").item(0) != null) {
                license = applicationNodeElement.getElementsByTagName("license").item(0).getTextContent();
            }
            if (applicationNodeElement.getElementsByTagName("category").item(0) != null) {
                appcategory = applicationNodeElement.getElementsByTagName("category").item(0).getTextContent();
            }
            if (applicationNodeElement.getElementsByTagName("web").item(0) != null) {
                webLink = applicationNodeElement.getElementsByTagName("web").item(0).getTextContent();
            }
            if (applicationNodeElement.getElementsByTagName("source").item(0) != null) {
                sourceCodeLink = applicationNodeElement.getElementsByTagName("source").item(0).getTextContent();
            }
            if (applicationNodeElement.getElementsByTagName("marketversion").item(0) != null) {
                marketVersion = applicationNodeElement.getElementsByTagName("marketversion").item(0).getTextContent();
            }
            if (applicationNodeElement.getElementsByTagName("hash").item(0) != null) {
                sha256Hash = applicationNodeElement.getElementsByTagName("hash").item(0).getTextContent();
            }
            if (applicationNodeElement.getElementsByTagName("size").item(0) != null) {
                sizeInBytes = Double.parseDouble(applicationNodeElement.getElementsByTagName("size").item(0).getTextContent());
            }
            if (applicationNodeElement.getElementsByTagName("sdkver").item(0) != null) {
                sdkVersion = applicationNodeElement.getElementsByTagName("sdkver").item(0).getTextContent();
            }
            if (applicationNodeElement.getElementsByTagName("permissions").item(0) != null) {
                permissions = applicationNodeElement.getElementsByTagName("permissions").item(0).getTextContent();
            }
            if (applicationNodeElement.getElementsByTagName("features").item(0) != null) {
                features = applicationNodeElement.getElementsByTagName("features").item(0).getTextContent();
            }

            byte[] logoBytes = fetchLogo(logo);


            preparedStatement.setBytes(1, logoBytes);
            preparedStatement.setString(2, appName);
            preparedStatement.setString(3, packageName);
            preparedStatement.setString(4, appUrl);
            preparedStatement.setString(5, summary);
            preparedStatement.setString(6, description);
            preparedStatement.setString(7, license);
            preparedStatement.setString(8, appcategory);
            preparedStatement.setString(9, webLink);
            preparedStatement.setString(10, sourceCodeLink);
            preparedStatement.setString(11, marketVersion);
            preparedStatement.setString(12, sha256Hash);
            preparedStatement.setDouble(13, sizeInBytes);
            preparedStatement.setString(14, sdkVersion);
            preparedStatement.setString(15, permissions);
            preparedStatement.setString(16, features);


            preparedStatement.execute();

            preparedStatement.close();
            databaseConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private byte[] fetchLogo(String logoUrl) {
        try {
            if(logoUrl != null) {
                URL url = new URL(logoUrl);
                InputStream inputStream = new BufferedInputStream(url.openStream());
                return IOUtils.toByteArray(inputStream);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }
}
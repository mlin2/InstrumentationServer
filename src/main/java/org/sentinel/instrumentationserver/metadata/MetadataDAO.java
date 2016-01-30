package org.sentinel.instrumentationserver.metadata;

import org.apache.commons.io.IOUtils;
import org.sentinel.instrumentationserver.DAOBase;
import org.sentinel.instrumentationserver.Main;
import org.sentinel.instrumentationserver.QueryBuilder;
import org.sentinel.instrumentationserver.generated.model.MetadataList;
import org.sentinel.instrumentationserver.generated.model.Metadatum;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * The data access object for metadata
 */
public class MetadataDAO extends DAOBase {

    /**
     * The database manager.
     */
    private static MetadataDAO MetadataDAO;

    protected MetadataDAO() {
    }


    /**
     * Singleton pattern.
     */
    public static MetadataDAO getInstance() {
        if (MetadataDAO == null) {
            MetadataDAO = new MetadataDAO();
        }

        return MetadataDAO;
    }


    /**
     * Get the metadata of all apps saved on the server.
     */
    public MetadataList getAllMetadata() {
        connectToDatabase();
        String sqlStatementGetAllMetadata = QueryBuilder.getQueryToGetAllMetadata();
        try {

            Statement statement = databaseConnection.createStatement();

            List<Metadatum> metadataList = new ArrayList<Metadatum>();
            ResultSet resultSet = statement.executeQuery(sqlStatementGetAllMetadata);

            while (resultSet.next()) {
                String sha512Hash = resultSet.getString("HASH");
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
            resultSet.close();
            databaseConnection.close();
            return new MetadataList().withMetadata(metadataList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Save the metadata for an app that was instrumented and is specified to be made public.
     */
    public void saveMetadataForInstrumentedApk(byte[] logo, String appName, String packageName, String sha512Hash, String sha256hash) {
        connectToDatabase();
        long apkId = getApkId(sha512Hash);
        String sqlStatementSaveMetadataForInstrumentedApk = QueryBuilder.getQueryToSaveMetadataForInstrumentedApk();

        try {
            if (getMetadataId(sha256hash) != -1) {
                String sqlStatementSaveInstrumentedApkIdForExistingMetadata = QueryBuilder.getQueryToLinkApkToMetadata();
                PreparedStatement preparedStatement = databaseConnection.prepareStatement(sqlStatementSaveInstrumentedApkIdForExistingMetadata);
                preparedStatement.setLong(1, apkId);
                preparedStatement.setString(2, sha512Hash);
                preparedStatement.setString(3, sha256hash);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                databaseConnection.close();
            } else {
                PreparedStatement preparedStatement = databaseConnection.prepareStatement(sqlStatementSaveMetadataForInstrumentedApk);
                preparedStatement.setBytes(1, logo);
                preparedStatement.setString(2, appName);
                preparedStatement.setString(3, packageName);
                preparedStatement.setLong(4, apkId);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                databaseConnection.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetch the logo specified in the URL.
     */
    private byte[] fetchLogo(String logoUrl) {
        try {
            if (logoUrl != null) {
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

    /**
     * Get the ID of the data set of metadata for a SHA 256 hash of an APK.
     */
    private long getMetadataId(String sha256Hash) {
        connectToDatabase();

        String sqlStatementGetMetadataIdFromHash = QueryBuilder.getQueryToGetMetadataIdFromSha256Hash();
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(sqlStatementGetMetadataIdFromHash);
            preparedStatement.setString(1, sha256Hash);
            ResultSet resultSet = preparedStatement.executeQuery();
            long id = -1;
            if (!resultSet.isClosed()) {
                id = resultSet.getLong(1);
                resultSet.close();
            }
            preparedStatement.close();
            return id;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //TODO handle this better
        return -1;
    }

    /**
     * Save the metadata from one XML element in the database.
     */
    public void saveMetadataFromXmlElement(Node applicationNode) {
        String LOGO_BASE_URI = "https://f-droid.org/repo/icons/";
        String APP_BASE_URI = "https://f-droid.org/repo/";
        String sqlStatementGetMetadataFromXml = QueryBuilder.getQueryToSaveMetadataFromXmlElement();
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
            if (applicationNodeElement.getAttribute("id") != null) {
                packageName = applicationNodeElement.getAttribute("id");
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
            preparedStatement.setString(17, sha256Hash);

            preparedStatement.execute();
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve the logo from the database.
     */
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
            disconnectFromDatabase();
            return logo;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the metadata of all instrumented apps saved on the server.
     */
    public MetadataList getInstrumentedMetadata() {
        connectToDatabase();

        String sqlStatementGetInstrumentedMetadata = QueryBuilder.getQueryToGetInstrumentedMetadata();
        try {

            Statement statement = databaseConnection.createStatement();

            List<Metadatum> metadataList = new ArrayList<Metadatum>();
            ResultSet resultSet = statement.executeQuery(sqlStatementGetInstrumentedMetadata);

            while (resultSet.next()) {
                String sha512Hash = resultSet.getString("HASH");
                Metadatum metadatum = new Metadatum().withDownloadUrl(Main.FORWARDED_URI + "instrument/" + sha512Hash)
                        .withLogoUrl(Main.FORWARDED_URI + "metadata/logo/" + sha512Hash + ".png").
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
            resultSet.close();
            disconnectFromDatabase();

            return new MetadataList().withMetadata(metadataList).withSize(metadataList.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all the links to APKs from the metadata table.
     */
    public List<String> getAllRepositoryApkLinks() {

        connectToDatabase();

        String sqlStatementGetAllRepositoryApkLinks = QueryBuilder.getQueryToGetAllRepositoryApkLinks();
        try {

            Statement statement = databaseConnection.createStatement();

            List<String> repositoryApkLinkList = new ArrayList<String>();
            ResultSet resultSet = statement.executeQuery(sqlStatementGetAllRepositoryApkLinks);

            while (resultSet.next()) {
                String link = resultSet.getString("APPURL");
                repositoryApkLinkList.add(link);
            }
            resultSet.close();
            disconnectFromDatabase();
            return repositoryApkLinkList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}

package org.sentinel.instrumentationserver;

/**
 * This class builds the queries needed by the instrumentation server both for instrumentation and metadata saving and
 * retrieval.
 */
public class QueryBuilder {

    /**
     * Create the table for the storage of APKs
     */
    public static final String SQL_STATEMENT_CREATE_TABLE_APKS_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS APKS" +
            "(ID                    INTEGER     PRIMARY KEY     AUTOINCREMENT   NOT NULL," +
            "HASH                   TEXT        UNIQUE                          NOT NULL," +
            "INSTRUMENTEDAPK        BLOB                                        NOT NULL)";

    /**
     * Create the table for the storage of metadata of APKs
     */
    public static final String SQL_STATEMENT_CREATE_TABLE_METADATA_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS METADATA" +
            "(ID                    INTEGER     PRIMARY KEY     AUTOINCREMENT   NOT NULL," +
            "LOGO                   BLOB                                                ," +
            "APPNAME                TEXT                                                ," +
            "PACKAGENAME            TEXT                                                ," +
            "APPURL                 TEXT                                                ," +
            "HASH                   TEXT                                                ," +
            "SUMMARY                TEXT                                                ," +
            "DESCRIPTION            TEXT                                                ," +
            "LICENSE                TEXT                                                ," +
            "APPCATEGORY            TEXT                                                ," +
            "WEBLINK                TEXT                                                ," +
            "SOURCECODELINK         TEXT                                                ," +
            "MARKETVERSION          TEXT                                                ," +
            "SHA256HASH             TEXT        UNIQUE                                  ," +
            "SIZEINBYTES            INTEGER                                             ," +
            "SDKVERSION             TEXT                                                ," +
            "PERMISSIONS            TEXT                                                ," +
            "FEATURES               TEXT                                                ," +
            "APKID                  INTEGER                                             ," +
            "FOREIGN KEY(APKID)     REFERENCES  APKS(ID)                                )";

    /**
     * Insert the APK into the APKS table.
     */
    public static String getQueryToInsertInstrumentedApk() {
        return "INSERT INTO APKS(HASH, INSTRUMENTEDAPK)" +
                "VALUES(?, ?)";
    }

    /**
     * Return an APK file for a GET request to /instrumentWithMetadata/sha512sum.
     */
    public static String getQueryToRetrieveApkFile() {
        return "SELECT INSTRUMENTEDAPK FROM APKS WHERE HASH=?";
    }

    /**
     * Check if an APK is already saved in the database.
     */
    public static String getQueryToCheckIfApkAlreadyInstrumented() {
        return "SELECT HASH FROM APKS WHERE HASH=?";
    }

    /**
     * Save metadata for an APK that is already instrumented by linking to the ID of the APK in the APKS table.
     */
    public static String getQueryToSaveMetadataForInstrumentedApk() {
        return "INSERT INTO METADATA(LOGO, APPNAME, PACKAGENAME, APKID) VALUES(?, ?, ?, ?)";
    }

    /**
     * Get the APKID from the SHA 512 hash.
     */
    public static String getQueryToGetApkIdFromHash() {
        return "SELECT ID FROM APKS WHERE HASH=?";
    }

    /**
     * Get all the metadata saved in the METADATA table.
     */
    public static String getQueryToGetAllMetadata() {
        return "SELECT * FROM METADATA";
    }

    /**
     * Get the logo binary dump for for a SHA 512 hash.
     */
    public static String getQueryToRetrieveLogoFile() {
        return "SELECT m.LOGO FROM METADATA m INNER JOIN APKS a ON (m.APKID = a.ID) WHERE a.HASH=?";
    }

    /**
     * Save metadata for an APK.
     */
    public static String getQueryToSaveMetadataFromXmlElement() {
        return "INSERT INTO METADATA(LOGO, APPNAME, PACKAGENAME, APPURL, SUMMARY, DESCRIPTION, LICENSE, " +
                "APPCATEGORY, WEBLINK, SOURCECODELINK, MARKETVERSION, SHA256HASH, SIZEINBYTES, SDKVERSION," +
                "PERMISSIONS, FEATURES) SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?  " +
                "WHERE NOT EXISTS (SELECT 1 FROM METADATA WHERE SHA256HASH = ?)"
                ;
    }

    /**
     * Link an APK by linking the APKID column to the ID column of the APKS table and saving the SHA 512 hash and
     * SHA 256 hash of the APK.
     */
    public static String getQueryToLinkApkToMetadata() {
        return "UPDATE METADATA SET APKID=?, HASH=? WHERE SHA256HASH=?";
    }

    /**
     * Get the ID from the METADATA table for a SHA 256 hash.
     */
    public static String getQueryToGetMetadataIdFromSha256Hash() {
        return "SELECT ID FROM METADATA m WHERE m.SHA256HASH = ?";
    }

    /**
     * Get the links to all APKs that are not yet instrumented but have metadata in the METADATA table.
     */
    public static String getQueryToGetAllRepositoryApkLinks() {
        return "SELECT APPURL FROM METADATA m WHERE m.APKID IS NULL";
    }

    /**
     * Get the metadata of all instrumented APKs on the server that should be made public.
     */
    public static String getQueryToGetInstrumentedMetadata() {
        return "SELECT * FROM METADATA m WHERE m.APKID IS NOT NULL";
    }
}

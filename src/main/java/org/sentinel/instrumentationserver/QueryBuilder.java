package org.sentinel.instrumentationserver;

/**
 * A Data Access Object for storage and retrieval of instrumentation server data.
 */
public class QueryBuilder {

    /**
     * Create the table for storage of APKs
     */
    public static final String SQL_STATEMENT_CREATE_TABLE_APKS_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS APKS" +
            "(ID                    INTEGER     PRIMARY KEY     AUTOINCREMENT   NOT NULL," +
            "HASH                   TEXT        UNIQUE                          NOT NULL," +
            "INSTRUMENTEDAPK        BLOB                                        NOT NULL)";

    /**
     * Create the table for storage of Metadata for instrumented APKs
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
            "SHA256HASH             TEXT                                                ," +
            "SIZEINBYTES            INTEGER                                             ," +
            "SDKVERSION             TEXT                                                ," +
            "PERMISSIONS            TEXT                                                ," +
            "FEATURES               TEXT                                                ," +
            "APKID                  INTEGER                                             ," +
            "FOREIGN KEY(APKID)     REFERENCES  APKS(ID)                                )";

    /**
     * Get a fresh database without the APKS table to test instrumentation.
     */
    public static final String SQL_STATEMENT_DROP_TABLE_APKS = "DROP TABLE IF EXISTS APKS;";

    /**
     * Get a database without the METADATA table to test instrumentation.
     */
    public static final String SQL_STATEMENT_DROP_TABLE_METADATA = "DROP TABLE IF EXISTS METADATA;";

    public static String getQueryToInsertInstrumentedApkIntoDatabase() {
        return "INSERT INTO APKS(HASH, INSTRUMENTEDAPK)" +
                "VALUES(?, ?)";

    }

    /**
     * Get parameterized query to return an APK file for a GET request to /instrument/sha512sum.
     */
    public static String getQueryToRetrieveApkFile() {
        return "SELECT INSTRUMENTEDAPK FROM APKS WHERE HASH=?";
    }

    /**
     * Get parameterized query to check if an APK is already saved in the database.
     */
    public static String getQueryToCheckIfApkAlreadyInstrumented() {
        return "SELECT HASH FROM APKS WHERE HASH=?";
    }

    public static String getQuerySaveMetadataForInstrumentedApk() {
        return "INSERT INTO METADATA(LOGO, APPNAME, PACKAGENAME, APKID) VALUES(?, ?, ?, ?)";
    }

    public static String getQueryGetApkIdFromHash() {
        return "SELECT ID FROM APKS WHERE HASH=?";
    }

    public static String getQueryGetAllMetadata() {
        return "SELECT * FROM METADATA";
    }

    public static String getQueryListAllMetadataIds() {
        return "SELECT ID FROM METADATA";
    }

    public static String getQueryGetSha512HashFromApkId() {
        return "SELECT HASH FROM APKS WHERE ID=?";
    }

    public static String getQueryToRetrieveLogoFile() {
        return "SELECT m.LOGO FROM METADATA m INNER JOIN APKS a ON (m.APKID = a.ID) WHERE a.HASH=?";
    }

    public static String getQueryToSaveMetadataFromXml() {
        return "INSERT INTO METADATA(LOGO, APPNAME, PACKAGENAME, APPURL, SUMMARY, DESCRIPTION, LICENSE, " +
                "APPCATEGORY, WEBLINK, SOURCECODELINK, MARKETVERSION, SHA256HASH, SIZEINBYTES, SDKVERSION," +
                "PERMISSIONS, FEATURES) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
}

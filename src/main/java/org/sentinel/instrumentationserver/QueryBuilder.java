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
            "APKID                  INTEGER     UNIQUE                          NOT NULL," +
            "FOREIGN KEY(APKID)     REFERENCES  APKS(ID)                                )";

    /**
     * Get a fresh database to test instrumentation.
     */
    public static final String SQL_STATEMENT_DROP_TABLE = "DROP TABLE IF EXISTS APKS;";

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
}

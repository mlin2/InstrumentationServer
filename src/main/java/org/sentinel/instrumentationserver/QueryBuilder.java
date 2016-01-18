package org.sentinel.instrumentationserver;

/**
 * A Data Access Object for storage and retrieval of instrumentation server data.
 */
public class QueryBuilder {

    /**
     * Create the table for storage of APKs
     */
    public static final String SQL_STATEMENT_CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS APKS" +
            "(ID                    INTEGER     PRIMARY KEY     AUTOINCREMENT   NOT NULL," +
            "HASH                   TEXT        UNIQUE                          NOT NULL," +
            "INSTRUMENTEDAPK        BLOB                                        NOT NULL)";

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
}

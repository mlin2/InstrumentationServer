package org.sentinel.instrumentationserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.PreparedStatement;

/**
 * Created by sebastian on 1/12/16.
 */
public class DAO {
    public static final String SQL_STATEMENT_DROP_TABLE = "DROP TABLE IF EXISTS APKS;";

    public static final String SQL_STATEMENT_CREATE_TABLE = "CREATE TABLE APKS" +
            "(ID                    INTEGER     PRIMARY KEY     AUTOINCREMENT   NOT NULL," +
            "HASH                   TEXT                                        NOT NULL," +
            "INSTRUMENTEDAPK        BLOB                                        NOT NULL)";

    public static String getQueryToInsertInstrumentedApkIntoDatabase(String sha512Hash) {
            return "INSERT INTO APKS(HASH, INSTRUMENTEDAPK)" +
                    "VALUES('" + sha512Hash + "', ?)";

    }

    public static String getQueryToRetrieveApkFile() {
        return "SELECT INSTRUMENTEDAPK FROM APKS WHERE HASH=?";
    }

    public static String getQueryToCheckIfApkAlreadyInstrumented() {
        return "SELECT HASH FROM APKS WHERE HASH=?";
    }
}

package org.sentinel.instrumentationserver;

/**
 * Created by sebastian on 1/12/16.
 */
public class DAO {
    public static final String SQL_STATEMENT_DROP_TABLE = "DROP TABLE IF EXISTS APKS;";

    public static final String SQL_STATEMENT_CREATE_TABLE = "CREATE TABLE APKS" +
            "(ID                    INTEGER     PRIMARY KEY     NOT NULL," +
            "PACKAGENAME            TEXT                        NOT NULL," +
            "HASH                   TEXT                        NOT NULL," +
            "INSTRUMENTEDAPK        BLOB)";


    public static final String SQL_STATEMENT_INSERT_APK = "INSERT INTO APKS(ID, PACKAGENAME, HASH, INSTRUMENTEDAPK)" +
            "VALUES (1, 'PolicyTester', '16ac6ca7e19f2836f238a5f46609244c4e11864e60ad8d16e58e43524b42381417708152af9ce90bc0f934e4ae2f041cb90f9729e92b0223bee252ce0342fe16', " +
            "'PolicyTester.apk');";

}

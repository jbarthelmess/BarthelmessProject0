package utils;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class ConnectionUtil {
    static Logger logger = Logger.getLogger(ConnectionUtil.class.getName());

    public static Connection createConnection() {
        try {
            // "jdbc:postgresql://35.203.25.121:5432/BankAPI?user=user&password=password"
            // Not storing the login credentials in the code
            return DriverManager.getConnection(System.getenv("P0_DB_ACCESS"));
        }
        catch (SQLException s) {
            s.printStackTrace();
            logger.fatal("Connection could not be established");
            return null;
        }
    }
}

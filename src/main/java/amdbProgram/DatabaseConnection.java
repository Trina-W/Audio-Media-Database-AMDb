package amdbProgram;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection is a utility class providing a method to create and establish the database connection to
 * allow database queries to be executed.
 */
public class DatabaseConnection {
    public static Connection getDBConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Retrieve database credentials from environment variables
            //String dbUrl = System.getenv("DB_URL");
            //String dbUser = System.getenv("DB_USER");
            //String dbPassword = System.getenv("DB_PASSWORD");

            String dbUrl = "jdbc:mysql://localhost:3307/AMDb";
            String dbUser = "user";
            String dbPassword = "password";

            // Check if any of the environment variables are not set
            if (dbUrl == null || dbUser == null || dbPassword == null) {
                throw new IllegalStateException("Database credentials are not set in the environment variables.");
            }

            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (ClassNotFoundException e) {
            // Handle exception related to the JDBC driver
            System.err.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            // Handle SQL related exceptions
            System.err.println("SQLException: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            System.err.println("Exception: " + e.getMessage());
        }
        return connection;
    }
}
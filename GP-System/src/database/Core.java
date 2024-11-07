package database;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class Core {
    public String database = "comp5590_a2";
    public String username = "comp5590";
    public String password = "a2";

    public String DB_URL = "jdbc:mysql://localhost/";
    private Connection connection;
    private Statement statement;
    public ResultSet resultSet;

    public Core() {
        Map<String, String> env = System.getenv();

        if (env.containsKey("DB_URL")) {
            DB_URL = "jdbc:" + env.get("DB_URL");
        } else {
            DB_URL = DB_URL + database + "?user=" + username + "&password=" + password;
        }

        try {
            establishConnection();
        } catch (SQLException e) {
            System.err.println("[database] failed to connect to database");
            System.exit(1);
        }
    }

    private void establishConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL);
        } catch (ClassNotFoundException e) {
            throw new SQLException("[database] Error loading MySQL JDBC driver: " + e.getMessage());
        }
    }

    public void executePreparedStatement(PreparedStatement statement) {
        var statementString = statement.toString().split(": ")[1];

        if (statementString.contains("VALUES")) {
            var values = statementString.split("VALUES")[1];
            var redacted = statementString.split("VALUES")[0] + "VALUES (";

            for (var i = 0; i < values.split(",").length; i++) {
                redacted += "?,";
            }

            redacted = redacted.substring(0, redacted.length() - 1) + ")";

            statementString = redacted;
        }

        System.out.println("[database] executing prepared statement: " + statementString);

        try {
            statement.execute();

            resultSet = statement.getResultSet();
        } catch (SQLException e) {
            System.err.println("[database] Prepared Statement: SQL Exception: " + e.getMessage());
        }
    }

    public void executeGenericUpdate(String sql) {
        System.out.println("[database] executing generic update: " + sql);

        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);

            resultSet = statement.getResultSet();
        } catch (SQLException e) {
            System.err.println("[database] Generic Update: SQL Exception: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    public PreparedStatement getPreparedStatement(String sql) throws SQLException {
        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        return (PreparedStatement) statement;
    }

    public void closeResources() {
        try {
            if (resultSet != null)
                resultSet.close();
            if (statement != null)
                statement.close();
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }

}

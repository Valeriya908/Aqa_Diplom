package ru.netology.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseHelper {
    private static String dbUrl = System.getProperty("dbUrl");
    private static String dbUser = System.getProperty("dbUser");
    private static String dbPass = System.getProperty("dbPass");

    @SneakyThrows
    public static void clearDB() {
        var cleanCreditRequest = "DELETE FROM credit_request_entity;";
        var cleanOrder = "DELETE FROM order_entity;";
        var cleanPayment = "DELETE FROM payment_entity;";
        var runner = new QueryRunner();
        try (var conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            runner.update(conn, cleanCreditRequest);
            runner.update(conn, cleanOrder);
            runner.update(conn, cleanPayment);
        }
    }

    public static String getTransactionStatusDebitCard() {
        var sqlQuery = "SELECT status FROM payment_entity WHERE id IS NOT NULL;";
        try {
            var connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            var countStmt = connection.createStatement();
            var rs = countStmt.executeQuery(sqlQuery);
            if (rs.next()) {
                var status = rs.getString("status");
                return status;
            }
        } catch (SQLException msg) {
            System.out.println("SQLException message:" + msg.getMessage());
        }
        return null;
    }

    public static String getTransactionTypeDebitCard() {
        var sqlQuery = "SELECT payment_id FROM order_entity WHERE id IS NOT NULL;";
        try {
            var connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            var countStmt = connection.createStatement();
            var result = countStmt.executeQuery(sqlQuery);

            if (result.next()) {
                var paymentId = result.getString("payment_id");
                return paymentId;
            }
        } catch (SQLException ex) {
            System.out.println("SQLException message:" + ex.getMessage());
        }
        return null;
    }

    public static String getTransactionStatusCreditCard() {
        var sqlQuery = "SELECT status FROM credit_request_entity WHERE id IS NOT NULL;";
        try {
            var connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            var countStmt = connection.createStatement();
            var rs = countStmt.executeQuery(sqlQuery);
            if (rs.next()) {
                var status = rs.getString("status");
                return status;
            }
        } catch (SQLException msg) {
            System.out.println("SQLException message:" + msg.getMessage());
        }
        return null;
    }

    public static String getTransactionTypeCreditCard() {
        var sqlQuery = "SELECT credit_id FROM order_entity WHERE id IS NOT NULL;";
        try {
            var connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            var countStmt = connection.createStatement();
            var result = countStmt.executeQuery(sqlQuery);

            if (result.next()) {
                var creditId = result.getString("credit_id");
                return creditId;
            }
        } catch (SQLException ex) {
            System.out.println("SQLException message:" + ex.getMessage());
        }
        return null;
    }
}

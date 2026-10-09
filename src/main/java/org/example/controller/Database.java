package org.example.controller;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Database {
    private static Database instance;
    private String url = System.getenv("URL");
    private String user = System.getenv("USER");
    private String password = System.getenv("PASSWORD");
    private Database() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontró el Driver: " + e.getMessage());
        }
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConexion() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
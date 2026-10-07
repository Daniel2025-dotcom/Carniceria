package org.example.controller;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Database {
    private static Database instance;
    private Connection connection;
    private Database() {
        try {
            Class.forName("org.postgresql.Driver");
            String url = System.getenv("URL");
            String user = System.getenv("USER");
            String password = System.getenv("PASSWORD");
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("fail to connect: " + e.getMessage());
        }
        
    }
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
      public Connection getConexion() {
        return connection;
    }
}

package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.SwingUtilities;
import org.example.controller.Database;
import org.example.gui.SistemaCarniceriaGUI;

public class Main {

    public static void main(String[] args) {
        initDataBase();
        SwingUtilities.invokeLater(() -> {
            new SistemaCarniceriaGUI().setVisible(true);
        });
    }

    private static void initDataBase() {
        // Credenciales correctas de PostgreSQL que configuraste
        String user = "postgres";
        String password = "admin";

        String urlPostgresDefault = "jdbc:postgresql://localhost:5432/postgres";
        String urlCarniceria = "jdbc:postgresql://localhost:5432/carniceria";
        String nombreBD = "carniceria";

        // 1. Conectar a la BD 'postgres' para verificar/crear la BD 'carniceria'
        try (Connection conDefault = DriverManager.getConnection(urlPostgresDefault, user, password);
             Statement st = conDefault.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + nombreBD + "'");
            if (!rs.next()) {
                st.executeUpdate("CREATE DATABASE " + nombreBD);
                System.out.println("Base de datos creada exitosamente.");
            } else {
                System.out.println("La base de datos '" + nombreBD + "' ya existe. Verificando tablas...");
            }
        } catch (Exception e) {
            System.err.println("Error al crear la base de datos: " + e.getMessage());
            return;
        }

        // 2. Conectar directamente a la BD 'carniceria' recién creada o existente para las tablas
        try (Connection conCarniceria = DriverManager.getConnection(urlCarniceria, user, password);
             Statement stTablas = conCarniceria.createStatement()) {

            String sqlTablas = """
                CREATE TABLE IF NOT EXISTS producto (
                    codigo_barras VARCHAR(50) PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    descripcion TEXT,
                    precio_base NUMERIC(10, 2) NOT NULL,
                    stock_actual NUMERIC(10, 3) NOT NULL DEFAULT 0.000,
                    tipo_venta VARCHAR(20) NOT NULL CHECK (tipo_venta IN ('UNIDAD', 'PESO'))
                );

                CREATE TABLE IF NOT EXISTS venta (
                    id_venta SERIAL PRIMARY KEY,
                    fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    metodo_pago VARCHAR(50) NOT NULL,
                    total NUMERIC(10, 2) NOT NULL
                );

                CREATE TABLE IF NOT EXISTS detalle_venta (
                    id_detalle SERIAL PRIMARY KEY,
                    id_venta INT NOT NULL,
                    codigo_barras VARCHAR(50) NOT NULL,
                    cantidad NUMERIC(10, 3) NOT NULL,
                    precio_unitario NUMERIC(10, 2) NOT NULL,
                    subtotal NUMERIC(10, 2) NOT NULL,
                    CONSTRAINT fk_venta FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE,
                    CONSTRAINT fk_producto FOREIGN KEY (codigo_barras) REFERENCES producto(codigo_barras)
                );
                """;

            stTablas.executeUpdate(sqlTablas);
            System.out.println("Estructura de tablas verificada/creada correctamente.");

        } catch (Exception e) {
            System.err.println("Error al crear las tablas: " + e.getMessage());
        }
    }
}
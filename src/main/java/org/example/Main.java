package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
    }
}

/* 

-- 1. Crear la base de datos (Ejecutá esto primero o creala desde tu gestor, ej: DBeaver / pgAdmin)
-- CREATE DATABASE carniceria_don_gerbacio;

-- Conectate a la base de datos creada y ejecutá lo siguiente:

-- Tabla de Productos (Catálogo / Stock)
CREATE TABLE producto (
    codigo_barras VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio_base NUMERIC(10, 2) NOT NULL,
    stock_actual NUMERIC(10, 3) NOT NULL DEFAULT 0.000, -- Permite decimales para kilos (ej: 12.500 kg) o enteros para unidades
    tipo_venta VARCHAR(20) NOT NULL CHECK (tipo_venta IN ('UNIDAD', 'PESO')) -- Controla si se vende por unidad o por peso
);

-- Tabla de Ventas (Cabecera del ticket)
CREATE TABLE venta (
    id_venta SERIAL PRIMARY KEY,
    fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metodo_pago VARCHAR(50) NOT NULL, -- Efectivo, Transferencia, Tarjeta, etc.
    total NUMERIC(10, 2) NOT NULL
);

-- Tabla de Detalle de Venta (Ítems / Renglones del ticket)
CREATE TABLE detalle_venta (
    id_detalle SERIAL PRIMARY KEY,
    id_venta INT NOT NULL,
    codigo_barras VARCHAR(50) NOT NULL,
    cantidad NUMERIC(10, 3) NOT NULL, -- Cantidad vendida (ej: 2 unidades o 1.250 kg)
    precio_unitario NUMERIC(10, 2) NOT NULL, -- Precio al momento de la venta
    subtotal NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_venta FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE,
    CONSTRAINT fk_producto FOREIGN KEY (codigo_barras) REFERENCES producto(codigo_barras)
);

-- Opcional: Insertar un par de productos de prueba para arrancar a probar tu app
INSERT INTO producto (codigo_barras, nombre, descripcion, precio_base, stock_actual, tipo_venta) 
VALUES 
('2000001', 'Asado de Novillo', 'Corte fresco de primera', 6500.00, 45.000, 'PESO'),
('77912345678', 'Gaseosa Cola 2.25L', 'Retornable', 2300.00, 24.000, 'UNIDAD');

*/
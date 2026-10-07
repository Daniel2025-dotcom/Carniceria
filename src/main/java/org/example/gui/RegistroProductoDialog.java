package org.example.gui;

import org.example.controller.dao.ProductDAOImpl;
import org.example.models.Product;

import javax.swing.*;
import java.awt.*;

public class RegistroProductoDialog extends JDialog {
    private ProductDAOImpl productDAO = new ProductDAOImpl();
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtPrecioBase;
    private JTextField txtStock;
    private JComboBox<String> cmbTipoVenta;

    public RegistroProductoDialog(JFrame parent) {
        super(parent, "Registrar Nuevo Producto", true); // true = Modal (bloquea la ventana de atrás)
        setSize(400, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setResizable(false);

        // Panel principal con padding
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 10, 15));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelFormulario.setBackground(Color.WHITE);

        Font fuenteLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fuenteInput = new Font("Segoe UI", Font.PLAIN, 14);

        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        txtDescripcion = new JTextField();
        txtPrecioBase = new JTextField();
        txtStock = new JTextField();
        cmbTipoVenta = new JComboBox<>(new String[]{"UNIDAD","PESO"});

        // Aplicar fuentes
        JTextField[] inputs = {txtCodigo, txtNombre, txtDescripcion, txtPrecioBase, txtStock};
        for (JTextField txt : inputs) {
            txt.setFont(fuenteInput);
        }
        cmbTipoVenta.setFont(fuenteInput);

        panelFormulario.add(crearLabel("Código de Barras:", fuenteLabel));
        panelFormulario.add(txtCodigo);

        panelFormulario.add(crearLabel("Nombre:", fuenteLabel));
        panelFormulario.add(txtNombre);

        panelFormulario.add(crearLabel("Precio Base ($):", fuenteLabel));
        panelFormulario.add(txtPrecioBase);

        panelFormulario.add(crearLabel("Stock Inicial:", fuenteLabel));
        panelFormulario.add(txtStock);

        panelFormulario.add(crearLabel("Tipo de Venta:", fuenteLabel));
        panelFormulario.add(cmbTipoVenta);
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dispose()); // Cierra la ventana
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnGuardar.setBackground(new Color(0, 120, 215));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardarProducto());
        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        add(panelFormulario, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JLabel crearLabel(String texto, Font fuente) {
        JLabel label = new JLabel(texto);
        label.setFont(fuente);
        return label;
    }

    private void guardarProducto() {
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        String desc = "Sin descripcion de momento";
        String precio = txtPrecioBase.getText();
        String stock = txtStock.getText();
        String tipoVenta = (String) cmbTipoVenta.getSelectedItem();

        if (codigo.isEmpty() || nombre.isEmpty() || precio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Código, Nombre y Precio son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        try {
            float precioVal = Float.parseFloat(precio);
            float stockVal = Float.parseFloat(stock);
            productDAO.create(new Product(nombre,desc,precioVal,stockVal,tipoVenta,codigo));

            JOptionPane.showMessageDialog(this, "Producto guardado exitosamente.");
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio y el stock deben ser valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
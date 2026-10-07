package org.example.gui;

import org.example.controller.dao.DealDAOImpl;
import org.example.controller.dao.ProductDAOImpl;
import org.example.models.Deal;
import org.example.models.Item;
import org.example.models.PaymentMethod;
import org.example.models.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SistemaCarniceriaGUI extends JFrame {
    private ProductDAOImpl productDAO = new ProductDAOImpl();
    private DealDAOImpl dealDAO = new DealDAOImpl();
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // Componentes globales de la vista de Ventas
    private JTextField txtScanner;
    private DefaultTableModel modeloTablaVentas;
    private JLabel lblTotal;
    private float totalVentaActual = 0f;

    // Componentes globales de la vista de Caja
    private DefaultTableModel modeloTablaCaja;

    public SistemaCarniceriaGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Punto de Venta - Carniceria Don Gerbacio");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- PANEL LATERAL (Menú) ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(201, 50, 0));
        sidebar.setPreferredSize(new Dimension(250, 0));

        try {
            java.net.URL imgUrl = getClass().getResource("/assets/transparente.png");
            if(imgUrl != null) {
                ImageIcon iconoOriginal = new ImageIcon(imgUrl);
                Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(230, 180, Image.SCALE_SMOOTH);
                ImageIcon iconoLogo = new ImageIcon(imagenEscalada);
                JLabel logoLabel = new JLabel("<html><div style='text-align: center; color: white;'>" +
                        "<h3>Carniceria Don Gerbacio</h3></div></html>", iconoLogo, JLabel.CENTER);
                logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
                logoLabel.setHorizontalTextPosition(JLabel.CENTER);
                logoLabel.setIconTextGap(15);
                logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                logoLabel.setBorder(BorderFactory.createEmptyBorder(30, 10, 40, 10));
                sidebar.add(logoLabel);
            }
        } catch (Exception ignored) {}

        JButton btnVentas = createSidebarButton("Nueva Venta (F2)");
        JButton btnStock = createSidebarButton("Control de Stock (F3)");
        JButton btnCaja = createSidebarButton("Cierre de Caja (F4)");

        sidebar.add(btnVentas);
        sidebar.add(btnStock);
        sidebar.add(btnCaja);
        sidebar.add(Box.createVerticalGlue());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Agregamos las vistas al CardLayout
        cardPanel.add(crearPanelVentas(), "VENTAS");
        cardPanel.add(crearPanelStock(), "STOCK");
        cardPanel.add(crearPanelCaja(), "CAJA");

        // Eventos de botones laterales
        btnVentas.addActionListener(e -> cardLayout.show(cardPanel, "VENTAS"));
        btnStock.addActionListener(e -> cardLayout.show(cardPanel, "STOCK"));
        btnCaja.addActionListener(e -> cardLayout.show(cardPanel, "CAJA"));

        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                txtScanner.requestFocusInWindow();
            }
        });
    }

    private JPanel crearPanelVentas() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBackground(Color.WHITE);

        JLabel lblScanner = new JLabel("Lector de Código:");
        lblScanner.setFont(new Font("Segoe UI", Font.BOLD, 16));

        txtScanner = new JTextField();
        txtScanner.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        txtScanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        txtScanner.addActionListener(e -> processScan());

        topPanel.add(lblScanner, BorderLayout.WEST);
        topPanel.add(txtScanner, BorderLayout.CENTER);

        String[] columnas = {"Código", "Descripción", "Precio", "Cantidad / Peso", "Subtotal"};
        modeloTablaVentas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tablaVentas = new JTable(modeloTablaVentas);
        tablaVentas.setRowHeight(35);
        tablaVentas.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tablaVentas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaVentas.getTableHeader().setBackground(new Color(245, 245, 245));
        tablaVentas.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        tablaVentas.setShowGrid(true);
        tablaVentas.setGridColor(new Color(225, 225, 225));
        tablaVentas.setBorder(null);

        JScrollPane scrollTabla = new JScrollPane(tablaVentas);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        lblTotal = new JLabel("TOTAL: $ 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTotal.setForeground(new Color(40, 167, 69));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelBotones.setBackground(Color.WHITE);

        JButton btnImprimir = createActionButton("Imprimir Ticket", new Color(108, 117, 125));
        JButton btnCobrar = createActionButton("Cobrar", new Color(0, 120, 215));
        btnCobrar.addActionListener(e->mostrarDialogoCobro());

        panelBotones.add(btnImprimir);
        panelBotones.add(btnCobrar);

        bottomPanel.add(lblTotal, BorderLayout.WEST);
        bottomPanel.add(panelBotones, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollTabla, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                txtScanner.requestFocusInWindow();
            }
        });

        return panel;
    }

    private void processScan() {
        String code = txtScanner.getText().trim();
        if (code.isEmpty()) return;

        Product p = productDAO.getByCode(code);

        if (p != null) {
            boolean existe = false;
            String sufijo = (p.getTipoVenta() != null && p.getTipoVenta().equals("PESO")) ? " Kg" : " Un";

            for (int i = 0; i < modeloTablaVentas.getRowCount(); i++) {
                if (modeloTablaVentas.getValueAt(i, 0).equals(code)) {
                    String cantString = modeloTablaVentas.getValueAt(i, 3).toString().split(" ")[0];
                    float nuevaCant = Float.parseFloat(cantString) + 1f;
                    float nuevoSubtotal = nuevaCant * p.getPrice();

                    modeloTablaVentas.setValueAt(String.format(Locale.US, "%.2f", nuevaCant) + sufijo, i, 3);
                    modeloTablaVentas.setValueAt(String.format(Locale.US, "$ %.2f", nuevoSubtotal), i, 4);
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                modeloTablaVentas.addRow(new Object[]{
                        p.getCode(),
                        p.getName(),
                        String.format(Locale.US, "$ %.2f", p.getPrice()),
                        "1.00" + sufijo,
                        String.format(Locale.US, "$ %.2f", p.getPrice())
                });
            }

            totalVentaActual += p.getPrice();
            lblTotal.setText(String.format(Locale.US, "TOTAL: $ %.2f", totalVentaActual));
            txtScanner.setText("");

        } else {
            JOptionPane.showMessageDialog(this, "El producto con código " + code + " no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            txtScanner.selectAll();
        }
    }

    private JPanel crearPanelStock() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JTextField txtBuscar = new JTextField(30);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        JButton btnAgregar = createActionButton("+ Nuevo Producto", new Color(0, 120, 215));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(Color.WHITE);
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(lblBuscar);
        searchPanel.add(txtBuscar);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(btnAgregar, BorderLayout.EAST);

        String[] columnas = {"Código", "Descripción", "Stock / Kg", "Precio Base"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tablaStock = new JTable(modeloTabla);

        tablaStock.setRowHeight(35);
        tablaStock.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tablaStock.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaStock.getTableHeader().setBackground(new Color(245, 245, 245));
        tablaStock.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        tablaStock.setShowGrid(true);
        tablaStock.setGridColor(new Color(225, 225, 225));
        tablaStock.setBorder(null);
        fillTablaProduct(modeloTabla);

        JScrollPane scrollTabla = new JScrollPane(tablaStock);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollTabla, BorderLayout.CENTER);

        return panel;
    }


    private JPanel crearPanelCaja() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top: Buscador / Filtro de fecha
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        topPanel.setBackground(Color.WHITE);

        JLabel lblFiltro = new JLabel("Filtrar por fecha / turno:");
        lblFiltro.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JTextField txtFiltro = new JTextField(15);
        txtFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtFiltro.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        JButton btnFiltrar = createActionButton("Filtrar", new Color(108, 117, 125));
        btnFiltrar.addActionListener(e -> {
            // TODO: Acá en el futuro pedís las ventas de esa fecha al DAO y llenas la tabla
            JOptionPane.showMessageDialog(this, "Filtro en construcción.");
        });

        topPanel.add(lblFiltro);
        topPanel.add(txtFiltro);
        topPanel.add(btnFiltrar);

        // Center: Tabla de Ventas
        String[] columnas = {"ID Venta", "Fecha / Hora", "Medio de Pago", "Total"};
        modeloTablaCaja = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tablaCaja = new JTable(modeloTablaCaja);
        tablaCaja.setRowHeight(35);
        tablaCaja.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tablaCaja.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaCaja.getTableHeader().setBackground(new Color(245, 245, 245));
        tablaCaja.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        tablaCaja.setShowGrid(true);
        tablaCaja.setGridColor(new Color(225, 225, 225));
        tablaCaja.setBorder(null);


        JScrollPane scrollTabla = new JScrollPane(tablaCaja);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Bottom: Total General
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        // Luego este valor se calculará sumando la columna de totales de la tabla
        JLabel lblTotalCaja = new JLabel("TOTAL RECAUDADO: $ 0,00");
        fillTablaCaja(modeloTablaCaja,lblTotalCaja);
        lblTotalCaja.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotalCaja.setForeground(new Color(201, 50, 0)); // Resalta en el color del negocio
        lblTotalCaja.setHorizontalAlignment(SwingConstants.RIGHT);

        bottomPanel.add(lblTotalCaja, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollTabla, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setPreferredSize(new Dimension(0, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(40, 44, 52));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createActionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void fillTablaProduct(DefaultTableModel modeltable){
        for (Product product : productDAO.getAll()){
            modeltable.addRow(new Object[]{product.getCode(), product.getName(), product.getStockActual(), product.getPrice()});
        }
    }

    private void mostrarDialogoCobro() {
        if (modeloTablaVentas.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "El ticket está vacío. Escanee un producto primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Seleccionar Medio de Pago", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JLabel lblMonto = new JLabel(String.format(Locale.US, "Total a Cobrar: $ %.2f", totalVentaActual));
        lblMonto.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblMonto.setHorizontalAlignment(SwingConstants.CENTER);
        lblMonto.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));
        dialog.add(lblMonto, BorderLayout.NORTH);

        JPanel panelPagos = new JPanel(new GridLayout(3, 2, 15, 15));
        panelPagos.setBackground(Color.WHITE);
        panelPagos.setBorder(BorderFactory.createEmptyBorder(10, 25, 25, 25));

        String[] metodos = {"EFECTIVO", "TARJETA DEBITO", "TARJETA CREDITO", "TRANSFERENCIA", "APP PAGO"};

        for (String metodo : metodos) {
            JButton btnMetodo = new JButton(metodo);
            btnMetodo.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnMetodo.setBackground(new Color(40, 44, 52));
            btnMetodo.setForeground(Color.WHITE);
            btnMetodo.setFocusPainted(false);
            btnMetodo.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnMetodo.addActionListener(e -> {
                procesarPagoConfirmado(metodo);
                dialog.dispose();
            });
            panelPagos.add(btnMetodo);
        }

        JButton btnCancelar = new JButton("CANCELAR");
        btnCancelar.setBackground(new Color(201, 50, 0));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dialog.dispose());

        panelPagos.add(btnCancelar);
        dialog.add(panelPagos, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void procesarPagoConfirmado(String metodoPago) {
        List<Item> list = new ArrayList<>();
        int totalRow = modeloTablaVentas.getRowCount();

        for (int i = 0; i < totalRow; i++) {
            String code = modeloTablaVentas.getValueAt(i, 0).toString();
            Product product = productDAO.getByCode(code);

            String cantidadStr = modeloTablaVentas.getValueAt(i, 3).toString()
                    .replace(" Kg", "")
                    .replace(" Un", "")
                    .trim();
            float cantidad = Float.parseFloat(cantidadStr);

            list.add(new Item(product, cantidad));
        }

        Deal deal = new Deal(list, PaymentMethod.valueOf(metodoPago.replace(" ", "_")));
        dealDAO.registerDeal(deal);

        JOptionPane.showMessageDialog(this,
                "Venta registrada exitosamente con: " + metodoPago,
                "Operación Exitosa",
                JOptionPane.INFORMATION_MESSAGE);

        modeloTablaVentas.setRowCount(0);
        totalVentaActual = 0f;
        lblTotal.setText("TOTAL: $ 0.00");
        txtScanner.requestFocusInWindow();
    }
    private void fillTablaCaja(DefaultTableModel modeloTabla,JLabel lblTotalCaja) {
        modeloTabla.setRowCount(0);
        float totalRecaudado = 0f;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Deal deal : dealDAO.getAllDeals()) {
            modeloTabla.addRow(new Object[]{
                    deal.getId(),
                    deal.getDate().format(formatter),
                    deal.getPaymentMethod().name(),
                    String.format(Locale.US, "$ %.2f", deal.getTotalPrice())
            });
            totalRecaudado += deal.getTotalPrice();
        }

        lblTotalCaja.setText(String.format(Locale.US, "TOTAL RECAUDADO: $ %.2f", totalRecaudado));
    }
}
package org.example.gui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SistemaCarniceriaGUI extends JFrame {

    private JPanel cardPanel;
    private CardLayout cardLayout;

    public SistemaCarniceriaGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Punto de Venta - Carniceria Don Gerbacio");
        setSize(1280, 720); // Tamaño generoso por defecto
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- PANEL LATERAL (Menú) ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(201, 50, 0)); // Color oscuro corporativo
        sidebar.setPreferredSize(new Dimension(250, 0));

        // 1. Cargar la imagen (Cambiá "logo.png" por el nombre exacto de tu archivo) //recordar que esto es absoluto luego va a cambiarse a relativo
        java.net.URL imgUrl = getClass().getResource("/assets/transparente.png");
        ImageIcon iconoOriginal = new ImageIcon(imgUrl);

        // 2. Escalar la imagen para que no desborde el panel (ejemplo: 150x150 píxeles)
        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(230, 180, Image.SCALE_SMOOTH);
        ImageIcon iconoLogo = new ImageIcon(imagenEscalada);

        // 3. Crear el JLabel pasando el texto y el ícono
        JLabel logoLabel = new JLabel("<html><div style='text-align: center; color: white;'>" +
                "<h3>Carniceria Don Gerbacio</h3></div></html>", iconoLogo, JLabel.CENTER);
        
        // 4. Alinear para que el texto quede exactamente debajo de la imagen
        logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
        logoLabel.setHorizontalTextPosition(JLabel.CENTER);
        // Separación entre imagen y texto
        logoLabel.setIconTextGap(15); 

        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(30, 10, 40, 10));
        sidebar.add(logoLabel);

        // Botones del menú
        JButton btnVentas = createSidebarButton("Nueva Venta (F2)");
        JButton btnStock = createSidebarButton("Control de Stock (F3)");
        JButton btnCaja = createSidebarButton("Cierre de Caja (F4)");

        sidebar.add(btnVentas);
        sidebar.add(btnStock);
        sidebar.add(btnCaja);
        sidebar.add(Box.createVerticalGlue());

        // --- PANEL CENTRAL (Vistas dinámicas) ---
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Crear las vistas
        cardPanel.add(crearPanelVentas(), "VENTAS");
        cardPanel.add(crearPanelStock(), "STOCK");

        // Eventos de navegación
        btnVentas.addActionListener(e -> cardLayout.show(cardPanel, "VENTAS"));
        btnStock.addActionListener(e -> cardLayout.show(cardPanel, "STOCK"));

        // Ensamblar
        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel crearPanelVentas() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top: Lector de Código de Barras
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBackground(Color.WHITE);

        JLabel lblScanner = new JLabel("Lector de Código:");
        lblScanner.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JTextField txtScanner = new JTextField();
        txtScanner.setFont(new Font("Segoe UI", Font.PLAIN, 20)); // Grande para fácil lectura
        txtScanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        // Idealmente: txtScanner.requestFocus() se llama cada vez que se cobra para volver a escanear

        topPanel.add(lblScanner, BorderLayout.WEST);
        topPanel.add(txtScanner, BorderLayout.CENTER);

        // Center: Tabla de productos
        String[] columnas = {"Código", "Descripción", "Precio x Kg / Un", "Cantidad / Peso", "Subtotal"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tablaVentas = new JTable(modeloTabla);

        // Estilos de tabla lineales y planos
        tablaVentas.setRowHeight(35);
        tablaVentas.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tablaVentas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaVentas.getTableHeader().setBackground(new Color(245, 245, 245));
        tablaVentas.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        tablaVentas.setShowGrid(true);
        tablaVentas.setGridColor(new Color(225, 225, 225));
        tablaVentas.setBorder(null);

        // Datos de prueba (combinando unidad y peso)
        modeloTabla.addRow(new Object[]{"2000001", "Asado de Novillo", "$ 6.500,00", "1.250 Kg", "$ 8.125,00"});
        modeloTabla.addRow(new Object[]{"77912345678", "Gaseosa Cola 2.25L", "$ 2.300,00", "1 Un", "$ 2.300,00"});

        JScrollPane scrollTabla = new JScrollPane(tablaVentas);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Bottom: Totales y Botones
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        JLabel lblTotal = new JLabel("TOTAL: $ 10.425,00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTotal.setForeground(new Color(40, 167, 69)); // Verde para el total

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelBotones.setBackground(Color.WHITE);

        JButton btnImprimir = createActionButton("Imprimir Ticket", new Color(108, 117, 125)); // Gris
        JButton btnCobrar = createActionButton("Cobrar", new Color(0, 120, 215)); // Azul

        panelBotones.add(btnImprimir);
        panelBotones.add(btnCobrar);

        bottomPanel.add(lblTotal, BorderLayout.WEST);
        bottomPanel.add(panelBotones, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollTabla, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelStock() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top: Búsqueda y Agregar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JTextField txtBuscar = new JTextField(30);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        // txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar producto..."); // FlatLaf feature

        JButton btnAgregar = createActionButton("+ Nuevo Producto", new Color(0, 120, 215));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(Color.WHITE);
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(lblBuscar);
        searchPanel.add(txtBuscar);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(btnAgregar, BorderLayout.EAST);

        // Center: Tabla Stock
        String[] columnas = {"Código", "Descripción", "Categoría", "Stock / Kg", "Precio Base"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tablaStock = new JTable(modeloTabla);

        tablaStock.setRowHeight(35);
        tablaStock.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tablaStock.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaStock.getTableHeader().setBackground(new Color(245, 245, 245));

        modeloTabla.addRow(new Object[]{"2000001", "Asado de Novillo", "Carnicería", "45.00 Kg", "$ 6.500,00"});
        modeloTabla.addRow(new Object[]{"77912345678", "Gaseosa Cola 2.25L", "Kiosco", "24 Un", "$ 2.300,00"});

        JScrollPane scrollTabla = new JScrollPane(tablaStock);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollTabla, BorderLayout.CENTER);

        return panel;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        
        // Hacemos que el ancho sea flexible al máximo para que ocupe todo el sidebar (altura fija de 50)
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setPreferredSize(new Dimension(0, 50)); 
        
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        
        // Opcional: Te puse el mismo color naranja/rojo del sidebar 
        // para que queden integrados, o podés dejarle otro tono si preferís contraste.
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
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25)); // Botones rectos y amplios
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SistemaCarniceriaGUI().setVisible(true);
        });
    }
}

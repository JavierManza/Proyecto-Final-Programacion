package view;

import dao.*;
import model.*;
import dto.PrestamoDTO;
import service.OpenLibraryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class Principal extends JFrame {
    // Usuario que ha iniciado sesión actualmente
    private Usuario usuarioActual;
    // Paneles principales: contenido central y panel lateral de operaciones
    private JPanel pnlContent, pnlOperations;
    // Tabla y modelo para mostrar los datos de la base de datos
    private JTable table;
    private DefaultTableModel tableModel;

    // Instancias de los DAOs para acceder a los datos
    private LibroDAO libroDAO = new LibroDAOImpl();
    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private PrestamoDAO prestamoDAO = new PrestamoDAOImpl();

    // Campos de texto para el formulario de Libros (Panel Adaptativo)
    private JTextField txtLibroIsbn, txtLibroTitulo, txtLibroAutor, txtLibroStock;

    public Principal(Usuario user) {
        this.usuarioActual = user;
        initUI(); // Inicializa la interfaz gráfica
        verificarRetrasos(); // Comprueba si hay préstamos fuera de plazo al entrar
        // Cargar el módulo por defecto según el rol
        switchModulo(esAdmin() ? "Libros" : "Mis Préstamos");
    }

    /**
     * Configura la estructura principal de la ventana (JFrame)
     */
    private void initUI() {
        setTitle("Biblioteca Municipal - Dashboard [" + usuarioActual.getNombre() + " | " + usuarioActual.getRol()
                + "]");
        setSize(1150, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Menú Superior
        setJMenuBar(createMenuBar());

        // Sidebar Premium
        add(createSidebar(), BorderLayout.WEST);

        // Contenido Central
        pnlContent = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pnlContent.add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel de Operaciones Adaptativo (Derecha)
        pnlOperations = new JPanel();
        pnlOperations.setPreferredSize(new Dimension(300, 0));
        pnlOperations.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        pnlContent.add(pnlOperations, BorderLayout.EAST);

        add(pnlContent, BorderLayout.CENTER);
    }

    /**
     * Busca en la base de datos préstamos cuya fecha de devolución ya haya pasado
     */
    private void verificarRetrasos() {
        List<PrestamoDTO> prestamos = prestamoDAO.listarPrestamosActivos();
        long retrasos = prestamos.stream()
                .filter(p -> p.getFechaDevolucionPrevista().isBefore(LocalDate.now()))
                .count();

        if (retrasos > 0) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ ATENCIÓN: Hay " + retrasos + " préstamos con retraso.",
                    "Alerta de Sistema", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Crea la barra de menú superior con opciones de sesión y temas
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // --- Menú de Sesión ---
        JMenu menuSesion = new JMenu("Sesión");

        JMenuItem miPassword = new JMenuItem("Cambiar Contraseña");
        miPassword.addActionListener(e -> mostrarDialogoPassword());

        JMenuItem miLogout = new JMenuItem("Cerrar Sesión");
        miLogout.addActionListener(e -> {
            new Login().setVisible(true); // Vuelve al login
            dispose(); // Cierra el dashboard
        });

        menuSesion.add(miPassword);
        menuSesion.add(miLogout);

        // --- Menú de Preferencias (Temas) ---
        JMenu menuTema = new JMenu("Preferencias");
        JMenuItem miClaro = new JMenuItem("Modo Claro ☀️");
        JMenuItem miOscuro = new JMenuItem("Modo Oscuro 🌙");
        // Cambian el LookAndFeel en tiempo real
        miClaro.addActionListener(e -> cambiarTema("com.formdev.flatlaf.FlatLightLaf"));
        miOscuro.addActionListener(e -> cambiarTema("com.formdev.flatlaf.FlatDarkLaf"));

        menuTema.add(miClaro);
        menuTema.add(miOscuro);

        // --- Menú de Herramientas (Extras) ---
        JMenu menuExtra = new JMenu("Herramientas");
        JMenuItem miExport = new JMenuItem("Exportar Tabla a CSV");
        miExport.addActionListener(e -> exportarCSV());
        menuExtra.add(miExport);

        // Añadir todas las secciones a la barra principal
        menuBar.add(menuSesion);
        menuBar.add(menuTema);
        menuBar.add(menuExtra);
        return menuBar;
    }

    /**
     * Muestra un cuadro de diálogo para que el usuario cambie su contraseña.
     * Solo actualiza si el campo no está vacío.
     */
    private void mostrarDialogoPassword() {
        String pass = JOptionPane.showInputDialog(this, "Introduce la nueva contraseña:");
        if (pass != null && !pass.trim().isEmpty()) {
            // Llama al DAO para actualizar la contraseña en la DB
            if (usuarioDAO.cambiarPassword(usuarioActual.getId(), pass)) {
                JOptionPane.showMessageDialog(this, "Contraseña actualizada con éxito.");
            }
        }
    }

    /**
     * Cambia el Look & Feel (tema visual) de la aplicación en tiempo real.
     * Se llama desde el menú Preferencias -> Modo Claro / Modo Oscuro.
     * 
     * @param themeClassName Nombre completo de la clase FlatLaf a aplicar
     */
    private void cambiarTema(String themeClassName) {
        try {
            UIManager.setLookAndFeel(themeClassName); // Aplica el nuevo tema
            SwingUtilities.updateComponentTreeUI(this); // Refresca todos los componentes abiertos
        } catch (Exception ex) {
            ex.printStackTrace(); // Si FlatLaf no está instalado, se muestra el error
        }
    }

    /**
     * Exporta el contenido actual de la JTable a un archivo CSV.
     * El archivo se guarda en la raíz del proyecto.
     * Esta función cumple la extensión opcional de Exportación de Datos.
     */
    private void exportarCSV() {
        try (FileWriter fw = new FileWriter("exportacion_biblioteca.csv")) {
            // 1. Escribir la fila de cabeceras (nombres de columna)
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                fw.write(tableModel.getColumnName(i) + (i == tableModel.getColumnCount() - 1 ? "" : ","));
            }
            fw.write("\n");
            // 2. Escribir cada fila de datos
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    fw.write(
                            tableModel.getValueAt(i, j).toString() + (j == tableModel.getColumnCount() - 1 ? "" : ","));
                }
                fw.write("\n");
            }
            JOptionPane.showMessageDialog(this, "Archivo 'exportacion_biblioteca.csv' generado con éxito.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage());
        }
    }

    /**
     * Crea el panel lateral izquierdo con los botones de navegación.
     * Los módulos que se muestran dependen del rol del usuario:
     * - ADMIN: acceso total a Libros, Usuarios, Préstamos, Historial y Reservas.
     * - SOCIO: solo puede ver sus propios préstamos y sus reservas.
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(10, 1, 0, 0));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(25, 25, 25)); // Color oscuro premium

        // Determinar qué módulos mostrar según el rol
        String[] modulosAdmin = { "Libros", "Usuarios", "Préstamos", "Historial", "Reservas" };
        String[] modulosSocio = { "Mis Préstamos", "Mis Reservas" };
        String[] modulos = esAdmin() ? modulosAdmin : modulosSocio;

        // Añadir una etiqueta de rol en la parte superior del sidebar
        JLabel lblRol = new JLabel(esAdmin() ? " 👤 ADMIN" : " 👤 SOCIO", JLabel.LEFT);
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRol.setForeground(esAdmin() ? new Color(255, 193, 7) : new Color(100, 220, 255)); // Amarillo para admin,
                                                                                             // azul para socio
        lblRol.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        sidebar.add(lblRol);

        for (String m : modulos) {
            JButton btn = new JButton(m);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(25, 25, 25));
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            btn.setFocusPainted(false);
            btn.addActionListener(e -> switchModulo(m)); // Cambia el módulo al hacer click
            sidebar.add(btn);
        }
        return sidebar;
    }

    /**
     * Devuelve true si el usuario actual tiene rol de ADMIN (Bibliotecario).
     */
    private boolean esAdmin() {
        return "ADMIN".equals(usuarioActual.getRol());
    }

    /**
     * Gestiona el cambio entre los diferentes módulos de la aplicación.
     * Los socios solo tienen acceso a sus propios módulos.
     */
    private void switchModulo(String modulo) {
        pnlOperations.removeAll();
        switch (modulo) {
            case "Libros" -> configModuloLibros();
            case "Usuarios" -> configModuloUsuarios();
            case "Préstamos" -> configModuloPrestamos();
            case "Historial" -> configModuloHistorial();
            case "Reservas" -> configModuloReservas();
            // Módulos exclusivos del Socio
            case "Mis Préstamos" -> configModuloMisPrestamos();
            case "Mis Reservas" -> configModuloReservas();
        }
        pnlOperations.revalidate();
        pnlOperations.repaint();
    }

    /**
     * Módulo de Libros: Configura la tabla y el formulario lateral CRUD
     */
    private void configModuloLibros() {
        // 1. Configurar columnas de la tabla
        String[] cols = { "ID", "ISBN", "Título", "Autor", "Stock" };
        tableModel.setDataVector(null, cols);
        // 2. Llenar la tabla con datos del DAO
        libroDAO.listarTodos().forEach(l -> tableModel.addRow(
                new Object[] { l.getId(), l.getIsbn(), l.getTitulo(), l.getAutor(), l.getEjemplaresDisponibles() }));

        // 3. Crear el Panel Adaptativo (Formulario lateral)
        pnlOperations.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;

        pnlOperations.add(new JLabel("ISBN:"), gbc);
        gbc.gridy++;
        txtLibroIsbn = new JTextField();
        pnlOperations.add(txtLibroIsbn, gbc);

        JButton btnFetch = new JButton("Buscar en API");
        gbc.gridy++;
        pnlOperations.add(btnFetch, gbc);

        gbc.gridy++;
        pnlOperations.add(new JLabel("Título:"), gbc);
        gbc.gridy++;
        txtLibroTitulo = new JTextField();
        pnlOperations.add(txtLibroTitulo, gbc);

        gbc.gridy++;
        pnlOperations.add(new JLabel("Autor:"), gbc);
        gbc.gridy++;
        txtLibroAutor = new JTextField();
        pnlOperations.add(txtLibroAutor, gbc);

        gbc.gridy++;
        pnlOperations.add(new JLabel("Stock:"), gbc);
        gbc.gridy++;
        txtLibroStock = new JTextField();
        pnlOperations.add(txtLibroStock, gbc);

        JButton btnGuardar = new JButton("Guardar Libro");
        if (UIManager.getLookAndFeel().getName().contains("FlatLaf")) {
            btnGuardar.setBackground(new Color(63, 81, 181));
            btnGuardar.setForeground(Color.WHITE);
        }
        gbc.gridy++;
        gbc.insets = new Insets(20, 5, 5, 5);
        pnlOperations.add(btnGuardar, gbc);

        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        gbc.gridy++;
        gbc.insets = new Insets(5, 5, 5, 5);
        pnlOperations.add(btnEliminar, gbc);

        // Acción del botón "Buscar en API": llama a Open Library con el ISBN
        btnFetch.addActionListener(e -> {
            OpenLibraryService.BookInfo info = OpenLibraryService.fetchByIsbn(txtLibroIsbn.getText());
            if (info != null) {
                txtLibroTitulo.setText(info.title); // Rellena el campo título automáticamente
                JOptionPane.showMessageDialog(this, "Datos obtenidos de Open Library");
            }
        });

        // Acción del botón "Guardar Libro": crea el objeto Libro y lo inserta en la DB
        btnGuardar.addActionListener(e -> {
            try {
                Libro l = new Libro();
                l.setIsbn(txtLibroIsbn.getText());
                l.setTitulo(txtLibroTitulo.getText());
                l.setAutor(txtLibroAutor.getText());
                l.setEjemplaresTotales(Integer.parseInt(txtLibroStock.getText())); // Convierte String a número
                l.setEjemplaresDisponibles(l.getEjemplaresTotales()); // Disponibles = Total al crear
                if (libroDAO.insertar(l)) {
                    JOptionPane.showMessageDialog(this, "Libro guardado");
                    configModuloLibros(); // Recarga la tabla para mostrar el nuevo libro
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: Verifique los datos numéricos.");
            }
        });

        // Acción del botón "Eliminar": obtiene el ID de la fila seleccionada y lo borra
        btnEliminar.addActionListener(e -> {
            int row = table.getSelectedRow(); // Fila que el usuario ha marcado en la tabla
            if (row != -1) {
                int id = (int) tableModel.getValueAt(row, 0); // El ID siempre está en la columna 0
                if (libroDAO.eliminar(id)) {
                    JOptionPane.showMessageDialog(this, "Libro eliminado");
                    configModuloLibros(); // Recarga la tabla
                }
            }
        });
    }

    /**
     * Módulo de Usuarios: Muestra el listado de todos los usuarios del sistema.
     * Útil para que el Bibliotecario vea qué socios están registrados.
     */
    private void configModuloUsuarios() {
        // Columnas de la tabla de usuarios
        String[] cols = { "ID", "Username", "Nombre", "Rol" };
        tableModel.setDataVector(null, cols);
        // Carga todos los usuarios desde la base de datos
        usuarioDAO.listarTodos().forEach(
                u -> tableModel.addRow(new Object[] { u.getId(), u.getUsername(), u.getNombre(), u.getRol() }));
        pnlOperations.add(new JLabel("Módulo de Consulta de Usuarios"));
        pnlOperations.add(new JLabel("Rol actual: " + usuarioActual.getRol()));
    }

    /**
     * Módulo de Préstamos: Gestión de libros prestados a socios
     */
    private void configModuloPrestamos() {
        String[] cols = { "ID", "Socio", "Libro", "Vence", "Estado" };
        tableModel.setDataVector(null, cols);
        // Cargar préstamos activos usando un DTO (Data Transfer Object)
        prestamoDAO.listarPrestamosActivos().forEach(p -> tableModel.addRow(new Object[] { p.getId(),
                p.getNombreSocio(), p.getTituloLibro(), p.getFechaDevolucionPrevista(), p.getEstado() }));

        pnlOperations.setLayout(new GridLayout(10, 1, 5, 5));
        JButton btnDevolver = new JButton("Registrar Devolución");
        if (UIManager.getLookAndFeel().getName().contains("FlatLaf")) {
            btnDevolver.setBackground(new Color(244, 67, 54)); // Rojo para devoluciones
            btnDevolver.setForeground(Color.WHITE);
        }
        pnlOperations.add(btnDevolver);

        btnDevolver.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) tableModel.getValueAt(row, 0);
                if (prestamoDAO.registrarDevolucion(id)) {
                    JOptionPane.showMessageDialog(this, "Libro devuelto al inventario");
                    configModuloPrestamos();
                }
            }
        });
    }

    /**
     * Módulo de Historial: Consulta de todos los préstamos pasados y presentes
     */
    private void configModuloHistorial() {
        String[] cols = { "ID", "Socio", "Libro", "Vence", "Estado" };
        tableModel.setDataVector(null, cols);
        // Obtiene el historial completo (incluyendo libros ya devueltos)
        prestamoDAO.listarHistorial().forEach(p -> tableModel.addRow(new Object[] { p.getId(), p.getNombreSocio(),
                p.getTituloLibro(), p.getFechaDevolucionPrevista(), p.getEstado() }));
    }

    /**
     * Módulo de Reservas: Relación N:M entre Usuarios y Libros
     */
    private void configModuloReservas() {
        String[] cols = { "ID", "Estado", "Fecha" };
        tableModel.setDataVector(null, cols);
        pnlOperations.add(new JLabel("Gestión de Reservas"));
        pnlOperations.add(new JLabel("Base de datos configurada para relaciones N:M."));
    }

    /**
     * Módulo exclusivo del Socio: muestra únicamente los préstamos
     * que pertenecen al usuario que ha iniciado sesión.
     * No puede ver los préstamos de otros socios.
     */
    private void configModuloMisPrestamos() {
        String[] cols = { "ID", "Libro", "Fecha Vencimiento", "Estado" };
        tableModel.setDataVector(null, cols);
        // Filtra los préstamos activos para mostrar solo los del socio actual
        prestamoDAO.listarPrestamosActivos().stream()
                .filter(p -> p.getSocioId() == usuarioActual.getId())
                .forEach(p -> tableModel.addRow(
                        new Object[] { p.getId(), p.getTituloLibro(), p.getFechaDevolucionPrevista(), p.getEstado() }));
        // Panel lateral con información personalizada para el socio
        pnlOperations.setLayout(new GridLayout(5, 1, 5, 5));
        JLabel info = new JLabel("<html><b>Hola, " + usuarioActual.getNombre()
                + "!</b><br>Aquí puedes ver tus préstamos activos.</html>");
        info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlOperations.add(info);
    }
}

package view;

import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import model.Bibliotecario;
import model.Socio;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class Registro extends JFrame {
    private JTextField txtUsername, txtEmail, txtNombre, txtApellidos, txtDni;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRol;
    private JPanel pnlDinamico;

    // Campos Socio
    private JTextField txtDireccion, txtTelefono;
    // Campos Bibliotecario
    private JTextField txtNumEmpleado;
    private JComboBox<String> cbTurno;

    private UsuarioDAO usuarioDAO;

    public Registro() {
        usuarioDAO = new UsuarioDAOImpl();
        initUI(); // Construye la ventana de registro
    }

    /**
     * Construye el formulario de registro.
     * Usa CardLayout para el panel dinámico: muestra campos de Socio o
     * Bibliotecario
     * según el rol que seleccione el usuario en el JComboBox.
     */
    private void initUI() {
        setTitle("Biblioteca Municipal - Registro");
        setSize(500, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // No cierra la app, solo esta ventana
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Campos comunes a todos los usuarios ---
        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        JLabel lblTitle = new JLabel("REGISTRO DE USUARIO", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        pnlForm.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = row;
        gbc.gridx = 0;
        pnlForm.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField();
        pnlForm.add(txtUsername, gbc);

        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        pnlForm.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField();
        pnlForm.add(txtPassword, gbc);

        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        pnlForm.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField();
        pnlForm.add(txtEmail, gbc);

        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        pnlForm.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField();
        pnlForm.add(txtNombre, gbc);

        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        pnlForm.add(new JLabel("Apellidos:"), gbc);
        gbc.gridx = 1;
        txtApellidos = new JTextField();
        pnlForm.add(txtApellidos, gbc);

        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        pnlForm.add(new JLabel("DNI:"), gbc);
        gbc.gridx = 1;
        txtDni = new JTextField();
        pnlForm.add(txtDni, gbc);

        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        pnlForm.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1;
        cbRol = new JComboBox<>(new String[] { "SOCIO", "BIBLIOTECARIO" });
        pnlForm.add(cbRol, gbc);

        // --- Panel Dinámico con CardLayout ---
        // CardLayout muestra solo uno de sus paneles a la vez.
        // Cuando el usuario cambia el rol, se intercambia automáticamente.
        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        pnlDinamico = new JPanel(new CardLayout());
        pnlDinamico.setBorder(BorderFactory.createTitledBorder("Datos Específicos"));

        // Tarjeta 1: Campos exclusivos de un Socio
        JPanel pnlSocio = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlSocio.add(new JLabel("Dirección:"));
        txtDireccion = new JTextField();
        pnlSocio.add(txtDireccion);
        pnlSocio.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        pnlSocio.add(txtTelefono);

        // Tarjeta 2: Campos exclusivos de un Bibliotecario
        JPanel pnlBiblio = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlBiblio.add(new JLabel("Nº Empleado:"));
        txtNumEmpleado = new JTextField();
        pnlBiblio.add(txtNumEmpleado);
        pnlBiblio.add(new JLabel("Turno:"));
        cbTurno = new JComboBox<>(new String[] { "MAÑANA", "TARDE", "NOCHE" });
        pnlBiblio.add(cbTurno);

        // Se añaden las dos tarjetas al panel, identificadas por el nombre del rol
        pnlDinamico.add(pnlSocio, "SOCIO");
        pnlDinamico.add(pnlBiblio, "BIBLIOTECARIO");
        pnlForm.add(pnlDinamico, gbc);

        // Evento: Cambiar tarjeta al cambiar el rol en el JComboBox
        cbRol.addActionListener(e -> {
            CardLayout cl = (CardLayout) pnlDinamico.getLayout();
            cl.show(pnlDinamico, (String) cbRol.getSelectedItem()); // Muestra la tarjeta del rol elegido
        });

        // Botón Guardar
        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton btnRegistrar = new JButton("Registrar Cuenta");
        if (UIManager.getLookAndFeel().getName().contains("FlatLaf")) {
            btnRegistrar.setBackground(new Color(76, 175, 80)); // Verde para confirmar acción positiva
            btnRegistrar.setForeground(Color.WHITE);
        }
        pnlForm.add(btnRegistrar, gbc);

        add(pnlForm, BorderLayout.CENTER);

        btnRegistrar.addActionListener(e -> handleRegistro());
    }

    /**
     * Recoge los datos del formulario y realiza el registro atómico (Usuario +
     * Detalle)
     */
    private void handleRegistro() {
        // 1. Crear el objeto base Usuario
        Usuario u = new Usuario();
        u.setUsername(txtUsername.getText());
        u.setPassword(new String(txtPassword.getPassword()));
        u.setEmail(txtEmail.getText());
        u.setNombre(txtNombre.getText());
        u.setApellidos(txtApellidos.getText());
        u.setDni(txtDni.getText());
        u.setRol((String) cbRol.getSelectedItem());

        // 2. Crear el objeto detalle según el rol (Herencia de tablas)
        Object detalle = null;
        if (u.getRol().equals("SOCIO")) {
            Socio s = new Socio();
            s.setDireccion(txtDireccion.getText());
            s.setTelefono(txtTelefono.getText());
            detalle = s;
        } else {
            Bibliotecario b = new Bibliotecario();
            b.setNumEmpleado(txtNumEmpleado.getText());
            b.setTurno((String) cbTurno.getSelectedItem());
            detalle = b;
        }

        // 3. Ejecutar el registro a través del DAO (Maneja la transacción SQL)
        if (usuarioDAO.registrar(u, detalle)) {
            JOptionPane.showMessageDialog(this, "Usuario registrado con éxito");
            new Login().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar. Verifique los datos.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}


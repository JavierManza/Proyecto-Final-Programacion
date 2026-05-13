package view;

import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {
    // Campos de entrada de datos
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblRegister;
    // Referencia al DAO para validar usuarios
    private UsuarioDAO usuarioDAO;

    public Login() {
        usuarioDAO = new UsuarioDAOImpl();
        initUI(); // Inicializa la interfaz gráfica
    }

    /**
     * Construye la interfaz gráfica del Login con GridBagLayout.
     * GridBagLayout permite colocar componentes en una cuadrícula flexible.
     */
    private void initUI() {
        setTitle("Biblioteca Municipal - Login");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Cierra la app al cerrar esta ventana
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Márgenes entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Los campos se estiran horizontalmente

        // Título centrado en la parte superior
        JLabel lblTitle = new JLabel("INICIO DE SESIÓN", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Ocupa 2 columnas
        add(lblTitle, gbc);

        // Fila 1: Campo de usuario
        gbc.gridwidth = 1; // Vuelve a 1 columna
        gbc.gridy = 1;
        add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        add(txtUsername, gbc);

        // Fila 2: Campo de contraseña (JPasswordField oculta los caracteres con *)
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        add(txtPassword, gbc);

        // Fila 3: Botón de acceso
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        btnLogin = new JButton("Entrar");
        // Estilo premium solo si carga el tema
        if (UIManager.getLookAndFeel().getName().contains("FlatLaf")) {
            btnLogin.setBackground(new Color(63, 81, 181));
            btnLogin.setForeground(Color.WHITE);
        }
        add(btnLogin, gbc);

        // Fila 4: Enlace al formulario de Registro
        gbc.gridy = 4;
        lblRegister = new JLabel("<html>¿No tienes cuenta? <font color='blue'>Regístrate aquí</font></html>");
        lblRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cambia el cursor al pasar por encima
        add(lblRegister, gbc);

        // Eventos: Botón Entrar y clic en el enlace de registro
        btnLogin.addActionListener(e -> handleLogin());
        lblRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new Registro().setVisible(true); // Abre el formulario de registro
                dispose(); // Cierra esta ventana
            }
        });
    }

    /**
     * Gestiona el proceso de inicio de sesión
     */
    private void handleLogin() {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword()); // Obtiene la clave del JPasswordField

        // Llama al DAO para comprobar las credenciales en la DB
        Usuario u = usuarioDAO.validar(user, pass);
        if (u != null) {
            JOptionPane.showMessageDialog(this, "Bienvenido " + u.getNombre());
            new Principal(u).setVisible(true); // Abre el Dashboard principal
            dispose(); // Cierra la ventana de Login
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Intentar cargar FlatLaf (si está disponible)
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception ex) {
            System.err.println("FlatLaf no encontrado, usando tema por defecto.");
        }
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}

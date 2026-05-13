import view.Login;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Configuración de Look and Feel Premium (FlatLaf)
        try {
            // Intentar cargar tema oscuro por defecto para el efecto "WOW"
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
        } catch (Exception ex) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Iniciar aplicación
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}

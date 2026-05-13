package dao;

import model.Usuario;
import java.util.List;

public interface UsuarioDAO {
    Usuario validar(String username, String password);

    boolean registrar(Usuario usuario, Object detalle); // Detalle puede ser Socio o Bibliotecario

    List<Usuario> listarTodos();

    boolean actualizar(Usuario usuario);

    boolean cambiarPassword(int id, String newPassword);

    boolean eliminar(int id);
}

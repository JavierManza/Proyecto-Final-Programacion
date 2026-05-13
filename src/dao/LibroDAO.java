package dao;

import model.Libro;
import java.util.List;

public interface LibroDAO {
    boolean insertar(Libro libro);

    boolean actualizar(Libro libro);

    boolean eliminar(int id);

    Libro buscarPorId(int id);

    Libro buscarPorIsbn(String isbn);

    List<Libro> listarTodos();
}

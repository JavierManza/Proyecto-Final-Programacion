package dao;

import model.Prestamo;
import dto.PrestamoDTO;
import java.util.List;

public interface PrestamoDAO {
    boolean registrarPrestamo(Prestamo prestamo);

    boolean registrarDevolucion(int prestamoId);

    List<PrestamoDTO> listarPrestamosActivos();

    List<PrestamoDTO> listarHistorial();

    boolean eliminar(int id);
}

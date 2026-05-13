package dao;

import model.Reserva;
import java.util.List;

public interface ReservaDAO {
    boolean insertar(Reserva reserva);
    List<Reserva> listarTodas();
    List<Reserva> listarPorSocio(int socioId);
    boolean actualizarEstado(int id, String nuevoEstado);
    boolean eliminar(int id);
}
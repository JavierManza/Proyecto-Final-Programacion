package dao;

import model.Reserva;
import java.util.List;

public interface ReservaDAO {
    boolean insertar(Reserva reserva);
    List<Reserva> listarTodas();
    List<Reserva> listarPorSocio(int socioId);
    boolean eliminar(int id);
}
package dao;

import db.ConexionDB;
import model.Reserva;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAOImpl implements ReservaDAO {

    @Override
    public boolean insertar(Reserva reserva) {
        String sql = "INSERT INTO reservas (socio_id, libro_id, fecha_reserva, estado) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reserva.getSocioId());
            ps.setInt(2, reserva.getLibroId());
            ps.setDate(3, Date.valueOf(reserva.getFechaReserva()));
            ps.setString(4, reserva.getEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Reserva> listarTodas() {
        return listarConSQL("SELECT r.*, u.nombre as socio, l.titulo as libro " +
                          "FROM reservas r " +
                          "JOIN usuarios u ON r.socio_id = u.id " +
                          "JOIN libros l ON r.libro_id = l.id");
    }

    @Override
    public List<Reserva> listarPorSocio(int socioId) {
        return listarConSQL("SELECT r.*, u.nombre as socio, l.titulo as libro " +
                          "FROM reservas r " +
                          "JOIN usuarios u ON r.socio_id = u.id " +
                          "JOIN libros l ON r.libro_id = l.id " +
                          "WHERE r.socio_id = " + socioId);
    }

    private List<Reserva> listarConSQL(String sql) {
        List<Reserva> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Reserva r = new Reserva();
                r.setId(rs.getInt("id"));
                r.setSocioId(rs.getInt("socio_id"));
                r.setLibroId(rs.getInt("libro_id"));
                r.setFechaReserva(rs.getDate("fecha_reserva").toLocalDate());
                r.setEstado(rs.getString("estado"));
                r.setNombreSocio(rs.getString("socio"));
                r.setTituloLibro(rs.getString("libro"));
                lista.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean actualizarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE reservas SET estado = ? WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM reservas WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

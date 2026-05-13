package dao;

import db.ConexionDB;
import model.Prestamo;
import dto.PrestamoDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAOImpl implements PrestamoDAO {

    @Override
    public boolean registrarPrestamo(Prestamo prestamo) {
        String sql = "INSERT INTO prestamos (socio_id, libro_id, fecha_devolucion_prevista) VALUES (?, ?, ?)";
        String updateLibro = "UPDATE libros SET ejemplares_disponibles = ejemplares_disponibles - 1 WHERE id = ? AND ejemplares_disponibles > 0";

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(updateLibro)) {
                ps.setInt(1, prestamo.getLibroId());
                int affected = ps.executeUpdate();
                if (affected == 0)
                    throw new SQLException("No hay ejemplares disponibles");
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, prestamo.getSocioId());
                ps.setInt(2, prestamo.getLibroId());
                ps.setDate(3, Date.valueOf(prestamo.getFechaDevolucionPrevista()));
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            e.printStackTrace();
        } finally {
            if (conn != null)
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return false;
    }

    @Override
    public boolean registrarDevolucion(int prestamoId) {
        String sql = "UPDATE prestamos SET fecha_devolucion_real = CURRENT_DATE, estado = 'DEVUELTO' WHERE id = ?";
        String getLibroId = "SELECT libro_id FROM prestamos WHERE id = ?";
        String updateLibro = "UPDATE libros SET ejemplares_disponibles = ejemplares_disponibles + 1 WHERE id = ?";

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            int libroId = -1;
            try (PreparedStatement ps = conn.prepareStatement(getLibroId)) {
                ps.setInt(1, prestamoId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        libroId = rs.getInt("libro_id");
                }
            }

            if (libroId != -1) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, prestamoId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(updateLibro)) {
                    ps.setInt(1, libroId);
                    ps.executeUpdate();
                }
                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            if (conn != null)
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            e.printStackTrace();
        } finally {
            if (conn != null)
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return false;
    }

    @Override
    public List<PrestamoDTO> listarPrestamosActivos() {
        return listarConSQL(
                "SELECT p.id, p.socio_id, u.nombre as socio, l.titulo as libro, p.fecha_prestamo, p.fecha_devolucion_prevista, p.estado "
                        +
                        "FROM prestamos p " +
                        "JOIN usuarios u ON p.socio_id = u.id " +
                        "JOIN libros l ON p.libro_id = l.id " +
                        "WHERE p.estado != 'DEVUELTO'");
    }

    @Override
    public List<PrestamoDTO> listarHistorial() {
        return listarConSQL(
                "SELECT p.id, p.socio_id, u.nombre as socio, l.titulo as libro, p.fecha_prestamo, p.fecha_devolucion_prevista, p.estado "
                        +
                        "FROM prestamos p " +
                        "JOIN usuarios u ON p.socio_id = u.id " +
                        "JOIN libros l ON p.libro_id = l.id");
    }

    private List<PrestamoDTO> listarConSQL(String sql) {
        List<PrestamoDTO> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new PrestamoDTO(
                        rs.getInt("id"),
                        rs.getInt("socio_id"), // Incluimos el ID del socio para filtrar por usuario
                        rs.getString("socio"),
                        rs.getString("libro"),
                        rs.getDate("fecha_prestamo").toLocalDate(),
                        rs.getDate("fecha_devolucion_prevista").toLocalDate(),
                        rs.getString("estado")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM prestamos WHERE id = ?";
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

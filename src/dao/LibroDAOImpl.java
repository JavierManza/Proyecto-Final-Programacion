package dao;

import db.ConexionDB;
import model.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAOImpl implements LibroDAO {

    @Override
    public boolean insertar(Libro libro) {
        String sql = "INSERT INTO libros (isbn, titulo, autor, sinopsis, portada_url, ejemplares_totales, ejemplares_disponibles) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, libro.getIsbn());
            ps.setString(2, libro.getTitulo());
            ps.setString(3, libro.getAutor());
            ps.setString(4, libro.getSinopsis());
            ps.setString(5, libro.getPortadaUrl());
            ps.setInt(6, libro.getEjemplaresTotales());
            ps.setInt(7, libro.getEjemplaresDisponibles());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean actualizar(Libro libro) {
        String sql = "UPDATE libros SET isbn=?, titulo=?, autor=?, sinopsis=?, portada_url=?, ejemplares_totales=?, ejemplares_disponibles=? WHERE id=?";
        try (Connection conn = ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, libro.getIsbn());
            ps.setString(2, libro.getTitulo());
            ps.setString(3, libro.getAutor());
            ps.setString(4, libro.getSinopsis());
            ps.setString(5, libro.getPortadaUrl());
            ps.setInt(6, libro.getEjemplaresTotales());
            ps.setInt(7, libro.getEjemplaresDisponibles());
            ps.setInt(8, libro.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM libros WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Libro buscarPorId(int id) {
        String sql = "SELECT * FROM libros WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLibro(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Libro buscarPorIsbn(String isbn) {
        String sql = "SELECT * FROM libros WHERE isbn = ?";
        try (Connection conn = ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLibro(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Libro> listarTodos() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros";
        try (Connection conn = ConexionDB.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                libros.add(mapResultSetToLibro(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return libros;
    }

    private Libro mapResultSetToLibro(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("id"),
                rs.getString("isbn"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("sinopsis"),
                rs.getString("portada_url"),
                rs.getInt("ejemplares_totales"),
                rs.getInt("ejemplares_disponibles"));
    }
}

package dao;

import db.ConexionDB;
import model.Usuario;
import model.Socio;
import model.Bibliotecario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario validar(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        try (Connection conn = ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("nombre"),
                            rs.getString("apellidos"),
                            rs.getString("dni"),
                            rs.getString("rol"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean registrar(Usuario usuario, Object detalle) {
        String sqlUser = "INSERT INTO usuarios (username, password, email, nombre, apellidos, dni, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false); // Inicio de transacción manual

            int userId = -1;
            try (PreparedStatement ps = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, usuario.getUsername());
                ps.setString(2, usuario.getPassword());
                ps.setString(3, usuario.getEmail());
                ps.setString(4, usuario.getNombre());
                ps.setString(5, usuario.getApellidos());
                ps.setString(6, usuario.getDni());
                ps.setString(7, usuario.getRol());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next())
                        userId = rs.getInt(1);
                }
            }

            if (userId != -1) {
                if (detalle instanceof Socio) {
                    Socio s = (Socio) detalle;
                    String sqlSocio = "INSERT INTO socios (usuario_id, direccion, telefono) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sqlSocio)) {
                        ps.setInt(1, userId);
                        ps.setString(2, s.getDireccion());
                        ps.setString(3, s.getTelefono());
                        ps.executeUpdate();
                    }
                } else if (detalle instanceof Bibliotecario) {
                    Bibliotecario b = (Bibliotecario) detalle;
                    String sqlBiblio = "INSERT INTO bibliotecarios (usuario_id, num_empleado, turno) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sqlBiblio)) {
                        ps.setInt(1, userId);
                        ps.setString(2, b.getNumEmpleado());
                        ps.setString(3, b.getTurno());
                        ps.executeUpdate();
                    }
                }
                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection conn = ConexionDB.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("dni"),
                        rs.getString("rol")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    @Override
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET email=?, nombre=?, apellidos=?, dni=? WHERE id=?";
        try (Connection conn = ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getEmail());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellidos());
            ps.setString(4, usuario.getDni());
            ps.setInt(5, usuario.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean cambiarPassword(int id, String newPassword) {
        String sql = "UPDATE usuarios SET password=? WHERE id=?";
        try (Connection conn = ConexionDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
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

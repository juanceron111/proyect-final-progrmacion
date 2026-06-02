package DAO;

import Modelo.Usuario;
import conexion.ConexionBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Login
    public Usuario login(String email, String password) {
        String sql = "SELECT * FROM usuarios WHERE email=? AND password=?";
        try {
            PreparedStatement ps = ConexionBase.getConnection()
                    .prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("rol")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    // Listar usuarios
    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try {
            Statement st = ConexionBase.getConnection().createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("rol")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return lista;
    }

    // Registrar usuario
    public boolean registrarUsuario(Usuario u) {
        String sql = "INSERT INTO usuarios VALUES (?,?,?,?,?)";
        try {
            PreparedStatement ps = ConexionBase.getConnection()
                    .prepareStatement(sql);
            ps.setString(1, u.getId());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getRol());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    // Eliminar usuario
    public boolean eliminarUsuario(String id) {
        String sql = "DELETE FROM usuarios WHERE id=?";
        try {
            PreparedStatement ps = ConexionBase.getConnection()
                    .prepareStatement(sql);
            ps.setString(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}
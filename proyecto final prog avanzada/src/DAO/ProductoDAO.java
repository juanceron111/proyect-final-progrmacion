package DAO;

import Modelo.Producto;
import conexion.ConexionBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // Listar todos los productos
    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        try {
            Statement st = ConexionBase.getConnection().createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Producto(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("marca"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getString("id_categoria")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return lista;
    }

    // Registrar producto
    public boolean registrarProducto(Producto p) {
        String sql = "INSERT INTO productos VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement ps = ConexionBase.getConnection()
                    .prepareStatement(sql);
            ps.setString(1, p.getId());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getMarca());
            ps.setDouble(4, p.getPrecio());
            ps.setInt(5, p.getStock());
            ps.setString(6, p.getIdCategoria());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    // Modificar producto
    public boolean modificarProducto(Producto p) {
        String sql = "UPDATE productos SET nombre=?, marca=?, " +
                "precio=?, stock=?, id_categoria=? WHERE id=?";
        try {
            PreparedStatement ps = ConexionBase.getConnection()
                    .prepareStatement(sql);
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getMarca());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getIdCategoria());
            ps.setString(6, p.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    // Eliminar producto
    public boolean eliminarProducto(String id) {
        String sql = "DELETE FROM productos WHERE id=?";
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

    // Buscar producto por id
    public Producto buscarProducto(String id) {
        String sql = "SELECT * FROM productos WHERE id=?";
        try {
            PreparedStatement ps = ConexionBase.getConnection()
                    .prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Producto(
                        rs.getString("id"),
                        rs.getString("nombre"),
                        rs.getString("marca"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getString("id_categoria")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }
}
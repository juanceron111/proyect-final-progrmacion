package DAO;

import Modelo.Venta;
import conexion.ConexionBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    // Registrar venta
    public boolean registrarVenta(Venta v) {
        String sql = "INSERT INTO ventas VALUES (?,?,?,?,?)";
        try {
            PreparedStatement ps = ConexionBase.getConnection()
                    .prepareStatement(sql);
            ps.setString(1, v.getId());
            ps.setDate(2, new java.sql.Date(v.getFecha().getTime()));
            ps.setDouble(3, v.getTotal());
            ps.setString(4, v.getIdUsuario());
            ps.setString(5, v.getTipo());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    // Listar ventas
    public List<Venta> listarVentas() {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT * FROM ventas";
        try {
            Statement st = ConexionBase.getConnection().createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Venta(
                        rs.getString("id"),
                        rs.getDate("fecha"),
                        rs.getDouble("total"),
                        rs.getString("id_usuario"),
                        rs.getString("tipo")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return lista;
    }
}
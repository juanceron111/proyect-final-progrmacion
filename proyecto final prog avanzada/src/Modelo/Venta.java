package Modelo;

import java.util.Date;

public class Venta {
    private String id;
    private Date fecha;
    private double total;
    private String idUsuario;
    private String tipo;

    public Venta(String id, Date fecha, double total,
                 String idUsuario, String tipo) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
        this.idUsuario = idUsuario;
        this.tipo = tipo;
    }

    public String getId() { return id; }
    public Date getFecha() { return fecha; }
    public double getTotal() { return total; }
    public String getIdUsuario() { return idUsuario; }
    public String getTipo() { return tipo; }

    public void setId(String id) { this.id = id; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public void setTotal(double total) { this.total = total; }
    public void setIdUsuario(String id) { this.idUsuario = id; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        return "Venta " + id + " - Total: $" + total + " - " + tipo;
    }
}

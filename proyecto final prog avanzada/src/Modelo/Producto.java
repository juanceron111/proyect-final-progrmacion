package Modelo;

public class Producto {
    private String id;
    private String nombre;
    private String marca;
    private double precio;
    private int stock;
    private String idCategoria;

    public Producto(String id, String nombre, String marca,
                    double precio, int stock, String idCategoria) {
        this.id = id;
        this.nombre = nombre;
        this.marca = marca;
        this.precio = precio;
        this.stock = stock;
        this.idCategoria = idCategoria;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getMarca() { return marca; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    public String getIdCategoria() { return idCategoria; }

    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setMarca(String marca) { this.marca = marca; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setStock(int stock) { this.stock = stock; }
    public void setIdCategoria(String id) { this.idCategoria = id; }

    // Para mostrar en consola
    @Override
    public String toString() {
        return nombre + " - " + marca + " - $" + precio + " (stock: " + stock + ")";
    }
}
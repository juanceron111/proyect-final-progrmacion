package Servidor;

import DAO.ProductoDAO;
import DAO.UsuarioDAO;
import DAO.VentaDAO;
import Modelo.Producto;
import Modelo.Usuario;
import Modelo.Venta;

import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.List;

public class ManejoCliente implements Runnable {

    private Socket cliente;

    public ManejoCliente(Socket cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        try {
            // FLUJOS — leer y escribir datos entre cliente y servidor
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(cliente.getInputStream()));

            OutputStream salidaStream = cliente.getOutputStream();
            PrintWriter salida = new PrintWriter(salidaStream, true);

            // Leer primera línea de la petición HTTP
            String lineaPrincipal = entrada.readLine();
            if (lineaPrincipal == null) {
                cliente.close();
                return;
            }

            System.out.println("[" + Thread.currentThread().getName() +
                    "] Peticion: " + lineaPrincipal);

            // Leer y descartar el resto del header HTTP
            String linea;
            String body = "";
            int contentLength = 0;

            while (!(linea = entrada.readLine()).isEmpty()) {
                if (linea.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(linea.split(":")[1].trim());
                }
            }

            // Leer body si existe (para POST)
            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                entrada.read(bodyChars, 0, contentLength);
                body = new String(bodyChars);
            }

            // ROUTING — decidir qué responder según la URL
            if (lineaPrincipal.contains("GET /productos")) {
                responderProductos(salida);

            } else if (lineaPrincipal.contains("POST /login")) {
                responderLogin(salida, body);

            } else if (lineaPrincipal.contains("POST /venta")) {
                responderVenta(salida, body);

            } else if (lineaPrincipal.contains("GET /")) {
                responderBienvenida(salida);

            } else {
                responder404(salida);
            }

            cliente.close();

        } catch (IOException e) {
            System.out.println("Error con cliente: " + e.getMessage());
        }
    }

    // ── ENDPOINTS ────────────────────────────────────────

    // GET /productos — devuelve lista de productos en JSON
    private void responderProductos(PrintWriter salida) {
        ProductoDAO dao = new ProductoDAO();
        List<Producto> productos = dao.listarProductos();

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            json.append("{")
                    .append("\"id\":\"").append(p.getId()).append("\",")
                    .append("\"nombre\":\"").append(escapar(p.getNombre())).append("\",")
                    .append("\"marca\":\"").append(escapar(p.getMarca())).append("\",")
                    .append("\"precio\":").append(p.getPrecio()).append(",")
                    .append("\"stock\":").append(p.getStock()).append(",")
                    .append("\"categoria\":\"").append(p.getIdCategoria()).append("\"")
                    .append("}");
            if (i < productos.size() - 1) json.append(",");
        }
        json.append("]");

        enviarRespuesta(salida, "200 OK", "application/json", json.toString());
        System.out.println("Productos enviados: " + productos.size());
    }

    // POST /login — valida usuario y contraseña
    private void responderLogin(PrintWriter salida, String body) {
        // body viene como: email=xxx&password=yyy
        String email = "";
        String password = "";

        for (String param : body.split("&")) {
            String[] par = param.split("=");
            if (par.length == 2) {
                if (par[0].equals("email")) email = par[1];
                if (par[0].equals("password")) password = par[1];
            }
        }

        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = dao.login(email, password);

        String json;
        if (usuario != null) {
            json = "{\"status\":\"ok\"," +
                    "\"nombre\":\"" + escapar(usuario.getNombre()) + "\"," +
                    "\"rol\":\"" + usuario.getRol() + "\"}";
            System.out.println("Login exitoso: " + usuario.getEmail());
        } else {
            json = "{\"status\":\"error\"," +
                    "\"mensaje\":\"Credenciales incorrectas\"}";
            System.out.println("Login fallido para: " + email);
        }

        enviarRespuesta(salida, "200 OK", "application/json", json);
    }

    // POST /venta — registra una venta
    private void responderVenta(PrintWriter salida, String body) {
        // body: idUsuario=xxx&total=yyy&tipo=online
        String idUsuario = "";
        double total = 0;
        String tipo = "online";

        for (String param : body.split("&")) {
            String[] par = param.split("=");
            if (par.length == 2) {
                if (par[0].equals("idUsuario")) idUsuario = par[1];
                if (par[0].equals("total")) total = Double.parseDouble(par[1]);
                if (par[0].equals("tipo")) tipo = par[1];
            }
        }

        String idVenta = "V" + System.currentTimeMillis();
        Venta venta = new Venta(idVenta, new Date(), total, idUsuario, tipo);

        VentaDAO dao = new VentaDAO();
        boolean ok = dao.registrarVenta(venta);

        String json = ok
                ? "{\"status\":\"ok\",\"idVenta\":\"" + idVenta + "\"}"
                : "{\"status\":\"error\",\"mensaje\":\"No se pudo registrar\"}";

        enviarRespuesta(salida, "200 OK", "application/json", json);
        System.out.println("Venta registrada: " + idVenta + " - $" + total);
    }

    // GET / — bienvenida
    private void responderBienvenida(PrintWriter salida) {
        String html = "<h1>Harmonic Sound Store API</h1>" +
                "<p>Servidor corriendo en puerto 8080</p>" +
                "<ul>" +
                "<li>GET /productos</li>" +
                "<li>POST /login</li>" +
                "<li>POST /venta</li>" +
                "</ul>";
        enviarRespuesta(salida, "200 OK", "text/html", html);
    }

    // 404
    private void responder404(PrintWriter salida) {
        enviarRespuesta(salida, "404 Not Found",
                "application/json", "{\"error\":\"Ruta no encontrada\"}");
    }

    // ── UTILIDADES ───────────────────────────────────────

    // Enviar respuesta HTTP con cabeceras CORS
    private void enviarRespuesta(PrintWriter salida, String status,
                                 String contentType, String body) {
        salida.println("HTTP/1.1 " + status);
        salida.println("Content-Type: " + contentType + "; charset=UTF-8");
        salida.println("Access-Control-Allow-Origin: *");
        salida.println("Access-Control-Allow-Methods: GET, POST, OPTIONS");
        salida.println("Access-Control-Allow-Headers: Content-Type");
        salida.println("Connection: close");
        salida.println("");
        salida.println(body);
    }

    // Escapar caracteres especiales para JSON
    private String escapar(String texto) {
        if (texto == null) return "";
        return texto.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}

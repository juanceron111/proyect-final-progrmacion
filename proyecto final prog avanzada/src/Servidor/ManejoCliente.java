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
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(cliente.getInputStream()));

            OutputStream salidaStream = cliente.getOutputStream();
            PrintWriter salida = new PrintWriter(salidaStream, true);

            String lineaPrincipal = entrada.readLine();
            if (lineaPrincipal == null) {
                cliente.close();
                return;
            }

            System.out.println("[" + Thread.currentThread().getName() +
                    "] Peticion: " + lineaPrincipal);

            String linea;
            String body = "";
            int contentLength = 0;

            while ((linea = entrada.readLine()) != null && !linea.isEmpty()) {
                if (linea.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(linea.split(":")[1].trim());
                }
            }

            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                entrada.read(bodyChars, 0, contentLength);
                body = new String(bodyChars);
            }

            // ROUTING
            if (lineaPrincipal.contains("OPTIONS")) {
                enviarRespuesta(salida, "200 OK", "text/plain", "");

            } else if (lineaPrincipal.contains("GET /productos")) {
                responderProductos(salida);

            } else if (lineaPrincipal.contains("GET /usuarios")) {
                responderUsuarios(salida);

            } else if (lineaPrincipal.contains("GET /ventas")) {
                responderVentas(salida);

            } else if (lineaPrincipal.contains("POST /login")) {
                responderLogin(salida, body);

            } else if (lineaPrincipal.contains("POST /venta")) {
                responderVenta(salida, body);

            } else if (lineaPrincipal.contains("POST /producto/eliminar")) {
                responderEliminarProducto(salida, body);

            } else if (lineaPrincipal.contains("POST /producto")) {
                responderCrearProducto(salida, body);

            } else if (lineaPrincipal.contains("POST /usuario/eliminar")) {
                responderEliminarUsuario(salida, body);

            } else if (lineaPrincipal.contains("POST /usuario")) {
                responderCrearUsuario(salida, body);

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

    private void responderUsuarios(PrintWriter salida) {
        UsuarioDAO dao = new UsuarioDAO();
        List<Usuario> usuarios = dao.listarUsuarios();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.get(i);
            json.append("{")
                    .append("\"id\":\"").append(u.getId()).append("\",")
                    .append("\"nombre\":\"").append(escapar(u.getNombre())).append("\",")
                    .append("\"email\":\"").append(escapar(u.getEmail())).append("\",")
                    .append("\"rol\":\"").append(u.getRol()).append("\"")
                    .append("}");
            if (i < usuarios.size() - 1) json.append(",");
        }
        json.append("]");
        enviarRespuesta(salida, "200 OK", "application/json", json.toString());
        System.out.println("Usuarios enviados: " + usuarios.size());
    }

    private void responderVentas(PrintWriter salida) {
        VentaDAO dao = new VentaDAO();
        List<Venta> ventas = dao.listarVentas();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < ventas.size(); i++) {
            Venta v = ventas.get(i);
            json.append("{")
                    .append("\"id\":\"").append(v.getId()).append("\",")
                    .append("\"fecha\":\"").append(v.getFecha()).append("\",")
                    .append("\"total\":").append(v.getTotal()).append(",")
                    .append("\"idUsuario\":\"").append(escapar(v.getIdUsuario())).append("\",")
                    .append("\"tipo\":\"").append(v.getTipo()).append("\"")
                    .append("}");
            if (i < ventas.size() - 1) json.append(",");
        }
        json.append("]");
        enviarRespuesta(salida, "200 OK", "application/json", json.toString());
        System.out.println("Ventas enviadas: " + ventas.size());
    }

    private void responderLogin(PrintWriter salida, String body) {
        String email = "", password = "";
        for (String param : body.split("&")) {
            String[] par = param.split("=");
            if (par.length == 2) {
                if (par[0].equals("email"))    email    = decode(par[1]);
                if (par[0].equals("password")) password = decode(par[1]);
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
            json = "{\"status\":\"error\",\"mensaje\":\"Credenciales incorrectas\"}";
            System.out.println("Login fallido para: " + email);
        }
        enviarRespuesta(salida, "200 OK", "application/json", json);
    }

    private void responderVenta(PrintWriter salida, String body) {
        String idUsuario = "", tipo = "online";
        double total = 0;
        for (String param : body.split("&")) {
            String[] par = param.split("=");
            if (par.length == 2) {
                if (par[0].equals("idUsuario")) idUsuario = decode(par[1]);
                if (par[0].equals("total"))     total     = Double.parseDouble(par[1]);
                if (par[0].equals("tipo"))      tipo      = decode(par[1]);
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

    private void responderCrearProducto(PrintWriter salida, String body) {
        String id = "", nombre = "", marca = "", categoria = "";
        double precio = 0;
        int stock = 0;
        for (String param : body.split("&")) {
            String[] par = param.split("=");
            if (par.length == 2) {
                switch (par[0]) {
                    case "id":        id        = decode(par[1]); break;
                    case "nombre":    nombre    = decode(par[1]); break;
                    case "marca":     marca     = decode(par[1]); break;
                    case "precio":    precio    = Double.parseDouble(par[1]); break;
                    case "stock":     stock     = Integer.parseInt(par[1]); break;
                    case "categoria": categoria = decode(par[1]); break;
                }
            }
        }
        ProductoDAO dao = new ProductoDAO();
        boolean ok = dao.registrarProducto(new Producto(id, nombre, marca, precio, stock, categoria));
        enviarRespuesta(salida, "200 OK", "application/json",
                ok ? "{\"status\":\"ok\"}" : "{\"status\":\"error\"}");
    }

    private void responderEliminarProducto(PrintWriter salida, String body) {
        String id = "";
        for (String param : body.split("&")) {
            String[] par = param.split("=");
            if (par.length == 2 && par[0].equals("id")) id = decode(par[1]);
        }
        ProductoDAO dao = new ProductoDAO();
        boolean ok = dao.eliminarProducto(id);
        enviarRespuesta(salida, "200 OK", "application/json",
                ok ? "{\"status\":\"ok\"}" : "{\"status\":\"error\"}");
    }

    private void responderCrearUsuario(PrintWriter salida, String body) {
        String id = "", nombre = "", email = "", pass = "", rol = "";
        for (String param : body.split("&")) {
            String[] par = param.split("=");
            if (par.length == 2) {
                switch (par[0]) {
                    case "id":       id     = decode(par[1]); break;
                    case "nombre":   nombre = decode(par[1]); break;
                    case "email":    email  = decode(par[1]); break;
                    case "password": pass   = decode(par[1]); break;
                    case "rol":      rol    = decode(par[1]); break;
                }
            }
        }
        UsuarioDAO dao = new UsuarioDAO();
        boolean ok = dao.registrarUsuario(new Usuario(id, nombre, email, pass, rol));
        enviarRespuesta(salida, "200 OK", "application/json",
                ok ? "{\"status\":\"ok\"}" : "{\"status\":\"error\",\"mensaje\":\"No se pudo registrar\"}");
    }

    private void responderEliminarUsuario(PrintWriter salida, String body) {
        String id = "";
        for (String param : body.split("&")) {
            String[] par = param.split("=");
            if (par.length == 2 && par[0].equals("id")) id = decode(par[1]);
        }
        UsuarioDAO dao = new UsuarioDAO();
        boolean ok = dao.eliminarUsuario(id);
        enviarRespuesta(salida, "200 OK", "application/json",
                ok ? "{\"status\":\"ok\"}" : "{\"status\":\"error\"}");
    }

    private void responderBienvenida(PrintWriter salida) {
        String html = "<h1>Harmonic Sound Store API</h1>" +
                "<p>Servidor corriendo en puerto 8080</p>" +
                "<ul>" +
                "<li>GET /productos</li>" +
                "<li>GET /usuarios</li>" +
                "<li>GET /ventas</li>" +
                "<li>POST /login</li>" +
                "<li>POST /venta</li>" +
                "<li>POST /producto</li>" +
                "<li>POST /producto/eliminar</li>" +
                "<li>POST /usuario</li>" +
                "<li>POST /usuario/eliminar</li>" +
                "</ul>";
        enviarRespuesta(salida, "200 OK", "text/html", html);
    }

    private void responder404(PrintWriter salida) {
        enviarRespuesta(salida, "404 Not Found",
                "application/json", "{\"error\":\"Ruta no encontrada\"}");
    }

    // ── UTILIDADES ───────────────────────────────────────

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

    private String escapar(String texto) {
        if (texto == null) return "";
        return texto.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private String decode(String s) {
        try {
            return java.net.URLDecoder.decode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }
}

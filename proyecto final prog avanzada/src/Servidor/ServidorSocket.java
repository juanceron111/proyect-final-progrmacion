package Servidor;

import java.net.*;
import java.io.*;

public class ServidorSocket {

    private static final int PUERTO = 8080;

    public static void main(String[] args) {
        System.out.println(" Harmonic Sound Store - Servidor");
        System.out.println("Iniciando servidor en puerto " + PUERTO + "...");

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor listo. Esperando conexiones...");
            System.out.println("Abre: http://localhost:" + PUERTO);
            System.out.println("---------------------------------");

            // Bucle infinito — acepta conexiones constantemente
            while (true) {
                // Espera un cliente
                Socket cliente = servidor.accept();

                System.out.println("Nueva conexion desde: " +
                        cliente.getInetAddress().getHostAddress());

                // HILOS — cada cliente se atiende en su propio hilo
                // para no bloquear el servidor
                Thread hilo = new Thread(new ManejoCliente(cliente));
                hilo.setName("Cliente-" + cliente.getPort());
                hilo.start();

                System.out.println("Hilo iniciado: " + hilo.getName());
            }

        } catch (IOException e) {
            System.out.println("Error en servidor: " + e.getMessage());
        }
    }
}
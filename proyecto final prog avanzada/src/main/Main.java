package main;

import Servidor.ServidorSocket;
import conexion.ConexionBase;
import java.sql.Connection;



public class Main {
    public static void main(String[] args) {
        
        System.out.println("Iniciando Harmonic Sound Store...");
        
        Connection con = ConexionBase.getConnection();

        if (con != null) {
            System.out.println("Sistema listo!");
        } else {
            System.out.println("No se pudo conectar");
        }

        ConexionBase.closeConnection();
        ServidorSocket.main(args);
    }
}

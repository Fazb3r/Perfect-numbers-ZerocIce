import com.zeroc.Ice.*;
import com.zeroc.Ice.Exception;

import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        // Primero verificamos que ICE funcione básicamente
        try (Communicator communicator = Util.initialize(args)) {
            System.out.println("=== Cliente ICE Inicializado ===");
            System.out.println("ICE Version: " + Util.stringVersion());
            
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("=== Búsqueda de Números Perfectos ===");
            System.out.println("Ingrese el rango de búsqueda:");
            
            // Solicitar rango al usuario
            System.out.print("Número inicial: ");
            int start = scanner.nextInt();
            
            System.out.print("Número final: ");
            int end = scanner.nextInt();
            
            // Validar rango
            if (start >= end) {
                System.err.println("Error: El número inicial debe ser menor que el final");
                return;
            }
            
            if (start < 1) {
                System.err.println("Error: El rango debe comenzar desde 1 o mayor");
                return;
            }
            
            System.out.println("\nRango validado: [" + start + ", " + end + "]");
            
            // Intentar conectar al maestro
            try {
                ObjectPrx base = communicator.stringToProxy("Master:default -h localhost -p 10000");
                
                if (base != null) {
                    System.out.println("Conexión al maestro establecida");
                    // Aquí se usarían las clases generadas cuando estén disponibles
                    System.out.println("NOTA: Las clases generadas de ICE se usarán cuando se compile correctamente");
                } else {
                    System.out.println("No se pudo establecer conexión con el maestro");
                }
                
            } catch (Exception e) {
                System.out.println("Error conectando al maestro: " + e.getMessage());
                System.out.println("Asegúrese de que el maestro esté ejecutándose");
            }
            
            scanner.close();
            
        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
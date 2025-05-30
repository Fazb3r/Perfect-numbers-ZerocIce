import NumbersApp.*;
import com.zeroc.Ice.*;
import java.util.Scanner;

public class Cliente {
    
    public static void main(String[] args) {
        Communicator communicator = null;
        
        try {
            // Inicializar el comunicador ICE
            communicator = Util.initialize(args);
            
            // Crear proxy del maestro
            ObjectPrx base = communicator.stringToProxy("Master:default -p 10000");
            MasterPrx maestro = MasterPrx.checkedCast(base);
            
            if (maestro == null) {
                System.err.println("Error: No se pudo conectar con el Maestro");
                return;
            }
            
            System.out.println("=== CLIENTE DE BÚSQUEDA DE NÚMEROS PERFECTOS ===");
            System.out.println("Conectado al Maestro exitosamente");
            
            Scanner scanner = new Scanner(System.in);
            
            while (true) {
                try {
                    // Solicitar rango al usuario
                    System.out.println("\n--- Nueva Búsqueda ---");
                    System.out.print("Ingrese el número inicial del rango (0 para salir): ");
                    int inicio = scanner.nextInt();
                    
                    if (inicio == 0) {
                        System.out.println("Saliendo del cliente...");
                        break;
                    }
                    
                    System.out.print("Ingrese el número final del rango: ");
                    int fin = scanner.nextInt();
                    
                    // Validar rango
                    if (inicio < 1 || fin < inicio) {
                        System.out.println("Error: Rango inválido. El inicio debe ser >= 1 y fin >= inicio");
                        continue;
                    }
                    
                    System.out.println("\nEnviando solicitud al Maestro...");
                    System.out.println("Rango solicitado: [" + inicio + ", " + fin + "]");
                    
                    // Enviar petición al maestro
                    long startTime = System.currentTimeMillis();
                    long resultado = maestro.processLargeRange(inicio, fin);
                    long endTime = System.currentTimeMillis();
                    
                    // Mostrar resultados
                    System.out.println("\n=== RESULTADOS ===");
                    System.out.println("Resultado del procesamiento: " + resultado);
                    System.out.println("Tiempo de ejecución: " + (endTime - startTime) + " ms");
                    
                    // Mostrar estadísticas del sistema
                    try {
                        String stats = maestro.getSystemStats();
                        System.out.println("Estadísticas del sistema: " + stats);
                    } catch (com.zeroc.Ice.Exception e) {
                        System.out.println("No se pudieron obtener estadísticas (Error ICE): " + e.getMessage());
                    } catch (java.lang.Exception e) {
                        System.out.println("No se pudieron obtener estadísticas: " + e.getMessage());
                    }
                    
                } catch (RangeError e) {
                    System.err.println("Error de rango: " + e.reason);
                } catch (com.zeroc.Ice.Exception e) {
                    System.err.println("Error ICE durante el procesamiento: " + e.getMessage());
                } catch (java.lang.Exception e) {
                    System.err.println("Error durante el procesamiento: " + e.getMessage());
                }
            }
            
            scanner.close();
            
        } catch (com.zeroc.Ice.Exception e) {
            System.err.println("Error ICE fatal en el cliente: " + e.getMessage());
            e.printStackTrace();
        } catch (java.lang.Exception e) {
            System.err.println("Error fatal en el cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (communicator != null) {
                try {
                    communicator.destroy();
                } catch (java.lang.Exception e) {
                    System.err.println("Error al cerrar el comunicador: " + e.getMessage());
                }
            }
        }
    }
}
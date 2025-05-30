import NumbersApp.*;
import com.zeroc.Ice.*;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Cliente {
    
    public static void main(String[] args) {
        Communicator communicator = null;
        Scanner scanner = new Scanner(System.in);
        
        try {
            // Inicializar el comunicador ICE
            communicator = Util.initialize(args);
            
            // Crear proxy del maestro
            ObjectPrx base = communicator.stringToProxy("Master:default -p 10000");
            MasterPrx maestro = MasterPrx.checkedCast(base);
            
            if (maestro == null) {
                System.err.println("Error: No se pudo conectar con el Maestro");
                System.err.println("Verifique que el servidor Master este ejecutandose en el puerto 10000");
                return;
            }
            
            // Verificar conexión con una llamada de prueba
            try {
                maestro.ice_ping();
                System.out.println("=== CLIENTE DE BUSQUEDA DE NUMEROS PERFECTOS ===");
                System.out.println("Conectado al Maestro exitosamente");
            } catch (com.zeroc.Ice.Exception e) {
                System.err.println("Error: No se pudo establecer conexion con el Maestro");
                System.err.println("Detalle del error: " + e.getMessage());
                System.err.println("Verifique que el servidor Master este ejecutandose en el puerto 10000");
                return;
            }
            
            while (true) {
                try {
                    // Solicitar rango al usuario
                    System.out.println("\n--- Nueva Busqueda ---");
                    System.out.print("Ingrese el numero inicial del rango (0 para salir): ");
                    
                    int inicio;
                    try {
                        inicio = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println("Error: Debe ingresar un numero entero valido");
                        scanner.nextLine(); // Limpiar buffer
                        continue;
                    } catch (java.util.NoSuchElementException e) {
                        System.out.println("Entrada no disponible. Saliendo del cliente...");
                        break;
                    }
                    
                    if (inicio == 0) {
                        System.out.println("Saliendo del cliente...");
                        break;
                    }
                    
                    System.out.print("Ingrese el numero final del rango: ");
                    int fin;
                    try {
                        fin = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println("Error: Debe ingresar un numero entero valido");
                        scanner.nextLine(); // Limpiar buffer
                        continue;
                    } catch (java.util.NoSuchElementException e) {
                        System.out.println("Entrada no disponible. Saliendo del cliente...");
                        break;
                    }
                    
                    // Validar rango
                    if (inicio < 1 || fin < inicio) {
                        System.out.println("Error: Rango invalido. El inicio debe ser >= 1 y fin >= inicio");
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
                    System.out.println("Tiempo de ejecucion: " + (endTime - startTime) + " ms");
                    
                    // Mostrar estadísticas del sistema
                    try {
                        String stats = maestro.getSystemStats();
                        System.out.println("Estadisticas del sistema: " + stats);
                    } catch (com.zeroc.Ice.Exception e) {
                        System.out.println("No se pudieron obtener estadisticas (Error ICE): " + e.getMessage());
                    } catch (java.lang.Exception e) {
                        System.out.println("No se pudieron obtener estadisticas: " + e.getMessage());
                    }
                    
                } catch (RangeError e) {
                    System.err.println("Error de rango: " + e.reason);
                } catch (com.zeroc.Ice.Exception e) {
                    System.err.println("Error ICE durante el procesamiento: " + e.getMessage());
                    System.err.println("Error: Problema de conexion con el servidor Master");
                    System.err.println("Verifique que el servidor Master este ejecutandose en el puerto 10000");
                    
                    System.out.print("¿Desea intentar nuevamente? (s/n): ");
                    try {
                        scanner.nextLine(); // Limpiar buffer
                        String respuesta = scanner.nextLine();
                        if (!respuesta.toLowerCase().equals("s") && !respuesta.toLowerCase().equals("si")) {
                            System.out.println("Saliendo del cliente...");
                            break;
                        }
                    } catch (java.util.NoSuchElementException ioEx) {
                        System.out.println("Saliendo del cliente...");
                        break;
                    }
                } catch (java.lang.Exception e) {
                    System.err.println("Error durante el procesamiento: " + e.getMessage());
                    if (e.getMessage() == null || e.getMessage().isEmpty()) {
                        System.err.println("Error: Problema de conexion con el servidor Master");
                        System.err.println("Verifique que el servidor Master este ejecutandose en el puerto 10000");
                        
                        System.out.print("¿Desea intentar nuevamente? (s/n): ");
                        try {
                            scanner.nextLine(); // Limpiar buffer
                            String respuesta = scanner.nextLine();
                            if (!respuesta.toLowerCase().equals("s") && !respuesta.toLowerCase().equals("si")) {
                                System.out.println("Saliendo del cliente...");
                                break;
                            }
                        } catch (java.util.NoSuchElementException ioEx) {
                            System.out.println("Saliendo del cliente...");
                            break;
                        }
                    } else {
                        // Para otros errores, continuar normalmente
                        System.out.println("Presione Enter para continuar...");
                        try {
                            scanner.nextLine();
                        } catch (java.util.NoSuchElementException ioEx) {
                            System.out.println("Saliendo del cliente...");
                            break;
                        }
                    }
                }
            }
            
        } catch (com.zeroc.Ice.Exception e) {
            System.err.println("Error ICE fatal en el cliente: " + e.getMessage());
            e.printStackTrace();
        } catch (java.lang.Exception e) {
            System.err.println("Error fatal en el cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerrar Scanner
            if (scanner != null) {
                try {
                    scanner.close();
                } catch (java.lang.Exception e) {
                    // Ignorar errores al cerrar scanner
                }
            }
            
            // Cerrar comunicador ICE
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
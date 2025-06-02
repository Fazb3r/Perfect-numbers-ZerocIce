import NumbersApp.*;
import com.zeroc.Ice.*;
import java.util.ArrayList;
import java.util.List;

public class Trabajador implements Worker {
    
    private int workerId;
    private boolean disponible;
    
    public Trabajador(int id) {
        this.workerId = id;
        this.disponible = true;
        System.out.println("Trabajador " + workerId + " inicializado");
    }
    
    @Override
    public long processRange(int startNum, int endNum, Current current) {
    System.out.println("\n=== TRABAJADOR " + workerId + " RECIBIO TAREA ===");
    System.out.println("Subrango asignado: [" + startNum + ", " + endNum + "]");
    System.out.println("Procesando subrango...");

    // Validar rango
    if (startNum < 1 || endNum < startNum) {
        String error = "Rango invalido: inicio=" + startNum + ", fin=" + endNum;
        System.err.println("Error: " + error);
        throw new RuntimeException(error);
    }

    disponible = false;
    long startTime = System.currentTimeMillis();
    long resultado = 0;

    try {
        List<Integer> numerosPerfectos = buscarNumerosPerfectos(startNum, endNum);

        for (int numeroPerfecto : numerosPerfectos) {
            resultado += numeroPerfecto;
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Trabajador " + workerId + " completo el procesamiento");
        System.out.println("Numeros perfectos encontrados: " + numerosPerfectos);
        System.out.println("Cantidad: " + numerosPerfectos.size());
        System.out.println("Suma total de los numeros perfectos: " + resultado);
        System.out.println("Tiempo de ejecución: " + (endTime - startTime) + " ms");

    } catch (java.lang.Exception e) {
        System.err.println("Error durante el procesamiento: " + e.getMessage());
    } finally {
        disponible = true;
    }

    return resultado;
}


    
    @Override
    public int getWorkerId(Current current) {
        return workerId;
    }
    
    @Override
    public boolean isAvailable(Current current) {
        return disponible;
    }
    
    /**
     * Buscar todos los números perfectos en un rango dado
     */
    private List<Integer> buscarNumerosPerfectos(int inicio, int fin) {
        List<Integer> numerosPerfectos = new ArrayList<>();
        
        System.out.println("Buscando numeros perfectos en el rango [" + inicio + ", " + fin + "]");
        
        for (int i = inicio; i <= fin; i++) {
            if (esNumeroPerfecto(i)) {
                numerosPerfectos.add(i);
                System.out.println("¡Numero perfecto encontrado: " + i + "!");
            }
            
            // para rangos grandes se muetsra el progreso cada mil numeros
            if (i % 1000 == 0 && i > inicio) {
                System.out.println("Progreso: verificando numero " + i + "...");
            }
        }
        
        return numerosPerfectos;
    }
    
    /**
     * Método para verificar si un número es perfecto
     * Un número perfecto es igual a la suma de sus divisores propios
     */
    private boolean esNumeroPerfecto(int numero) {
        if (numero <= 1) return false;
        
        int sumaDivisores = 1; // 1 siempre es divisor propio
        
        // Buscar divisores hasta la raíz cuadrada para eficiencia
        for (int i = 2; i * i <= numero; i++) {
            if (numero % i == 0) {
                sumaDivisores += i;
                
                // Si i es divisor, numero/i también lo es (excepto si i = sqrt(numero))
                if (i != numero / i) {
                    sumaDivisores += numero / i;
                }
            }
        }
        
        return sumaDivisores == numero;
    }
    
    /**
     * Método auxiliar para obtener todos los divisores de un número (para debugging)
     */
    private List<Integer> obtenerDivisores(int numero) {
        List<Integer> divisores = new ArrayList<>();
        if (numero <= 0) return divisores;
        
        divisores.add(1); // 1 siempre es divisor
        
        for (int i = 2; i * i <= numero; i++) {
            if (numero % i == 0) {
                divisores.add(i);
                if (i != numero / i) {
                    divisores.add(numero / i);
                }
            }
        }
        
        divisores.sort(Integer::compareTo);
        return divisores;
    }
    
    /**
     * Método para mostrar información detallada de un número perfecto (para debugging)
     */
    private void mostrarInfoNumeroPerfecto(int numero) {
        if (esNumeroPerfecto(numero)) {
            List<Integer> divisores = obtenerDivisores(numero);
            int suma = divisores.stream().mapToInt(Integer::intValue).sum();
            System.out.println("Numero " + numero + " es perfecto:");
            System.out.println("  Divisores propios: " + divisores);
            System.out.println("  Suma de divisores: " + suma);
        }
    }
    
    public static void main(String[] args) {
        Communicator communicator = null;
        
        try {
            // Obtener ID del trabajador desde argumentos
            int workerId = 1; // Valor por defecto
            if (args.length > 0) {
                try {
                    workerId = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    System.out.println("ID invalido, usando ID por defecto: " + workerId);
                }
            }
            
            // Inicializar el comunicador ICE
            communicator = Util.initialize();
            
            // Crear el adapter para el trabajador
            int puerto = 10001 + workerId; // Puerto único para cada trabajador
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "WorkerAdapter" + workerId, "default -p " + puerto
            );
            
            // Crear e instalar el servant del trabajador
            Trabajador trabajador = new Trabajador(workerId);
            adapter.add(trabajador, Util.stringToIdentity("Worker" + workerId));
            
            // Activar el adapter
            adapter.activate();
            
            System.out.println("=== TRABAJADOR " + workerId + " INICIADO ===");
            System.out.println("Escuchando en el puerto " + puerto);
            System.out.println("Especializado en busqueda de NÚMEROS PERFECTOS");
            
            // Registrarse con el maestro
            try {
                ObjectPrx base = communicator.stringToProxy("Master:default -p 10000");
                MasterPrx maestro = MasterPrx.checkedCast(base);
                
                if (maestro != null) {
                    WorkerPrx workerProxy = WorkerPrx.uncheckedCast(
                        adapter.createProxy(Util.stringToIdentity("Worker" + workerId))
                    );
                    
                    maestro.registerWorker(workerProxy);
                    System.out.println("Registrado exitosamente con el Maestro");
                    
                    // Mostrar algunos números perfectos conocidos para referencia
                    System.out.println("\nNumeros perfectos conocidos:");
                    System.out.println("- 6 (1+2+3=6)");
                    System.out.println("- 28 (1+2+4+7+14=28)");
                    System.out.println("- 496 (suma de divisores propios)");
                    System.out.println("- 8128 (suma de divisores propios)");
                    
                } else {
                    System.err.println("No se pudo conectar con el Maestro");
                }
            } catch (com.zeroc.Ice.Exception e) {
                System.err.println("Ocurrio un error ICE al registrarse con el Maestro: " + e.getMessage());
                System.out.println("El trabajador continuara ejecutandose, pero no estará registrado");
            } catch (java.lang.Exception e) {
                System.err.println("Error general al registrarse con el Maestro: " + e.getMessage());
                System.out.println("El trabajador continuara ejecutandose, pero no estara registrado");
            }
            
            System.out.println("El trabajador esta listo para recibir un rango de busqueda de numeros perfectos...");
            
            // Esperar hasta que se cierre el comunicador
            communicator.waitForShutdown();
            
        } catch (com.zeroc.Ice.Exception e) {
            System.err.println("Error ICE en el trabajador: " + e.getMessage());
            e.printStackTrace();
        } catch (java.lang.Exception e) {
            System.err.println("Error general en el trabajador: " + e.getMessage());
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

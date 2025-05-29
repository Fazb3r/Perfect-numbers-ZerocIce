import NumbersApp.*;
import com.zeroc.Ice.*;

public class Trabajador extends Worker {
    
    private int workerId;
    private boolean disponible;
    
    public Trabajador(int id) {
        this.workerId = id;
        this.disponible = true;
        System.out.println("Trabajador " + workerId + " inicializado");
    }
    
    @Override
    public long processRange(int startNum, int endNum, Current current) throws RangeError {
        System.out.println("\n=== TRABAJADOR " + workerId + " PROCESANDO ===");
        System.out.println("Rango asignado: [" + startNum + ", " + endNum + "]");
        
        // Validar rango
        if (startNum < 1 || endNum < startNum) {
            String error = "Rango inválido: inicio=" + startNum + ", fin=" + endNum;
            System.err.println("Error: " + error);
            throw new RangeError(error);
        }
        
        disponible = false;
        long startTime = System.currentTimeMillis();
        
        try {
            // Por ahora, simular procesamiento básico
            // En una implementación real, aquí iría la lógica de números perfectos
            long resultado = calcularSumaRango(startNum, endNum);
            
            long endTime = System.currentTimeMillis();
            System.out.println("Trabajador " + workerId + " completó el procesamiento");
            System.out.println("Resultado: " + resultado);
            System.out.println("Tiempo: " + (endTime - startTime) + " ms");
            
            return resultado;
            
        } catch (Exception e) {
            System.err.println("Error durante el procesamiento: " + e.getMessage());
            throw new RangeError("Error interno del trabajador: " + e.getMessage());
        } finally {
            disponible = true;
        }
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
     * Método auxiliar para calcular la suma de un rango
     * (Placeholder para la lógica de números perfectos)
     */
    private long calcularSumaRango(int inicio, int fin) {
        long suma = 0;
        
        System.out.println("Calculando suma para rango [" + inicio + ", " + fin + "]");
        
        // Simular trabajo computacional
        for (int i = inicio; i <= fin; i++) {
            suma += i;
            
            // Simular trabajo pesado ocasionalmente
            if (i % 1000 == 0) {
                try {
                    Thread.sleep(1); // Simular carga computacional
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        return suma;
    }
    
    /**
     * Método para verificar si un número es perfecto
     * (Para implementar en funcionalidades futuras)
     */
    private boolean esNumeroPerfecto(int numero) {
        if (numero <= 1) return false;
        
        int sumaDivisores = 1; // 1 siempre es divisor
        
        // Buscar divisores hasta la raíz cuadrada
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
    
    public static void main(String[] args) {
        Communicator communicator = null;
        
        try {
            // Obtener ID del trabajador desde argumentos
            int workerId = 1; // Valor por defecto
            if (args.length > 0) {
                try {
                    workerId = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    System.out.println("ID inválido, usando ID por defecto: " + workerId);
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
            System.out.println("Escuchando en puerto " + puerto);
            
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
                } else {
                    System.err.println("No se pudo conectar con el Maestro");
                }
            } catch (Exception e) {
                System.err.println("Error al registrarse con el Maestro: " + e.getMessage());
                System.out.println("El trabajador continuará ejecutándose, pero no estará registrado");
            }
            
            System.out.println("Trabajador listo para recibir tareas...");
            
            // Esperar hasta que se cierre el comunicador
            communicator.waitForShutdown();
            
        } catch (Exception e) {
            System.err.println("Error en el trabajador: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (communicator != null) {
                try {
                    communicator.destroy();
                } catch (Exception e) {
                    System.err.println("Error al cerrar el comunicador: " + e.getMessage());
                }
            }
        }
    }
}
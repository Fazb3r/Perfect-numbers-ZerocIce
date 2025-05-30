import NumbersApp.*;
import com.zeroc.Ice.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Maestro implements Master {
    
    // Lista de trabajadores registrados
    private Map<Integer, WorkerPrx> trabajadores;
    private int nextWorkerId = 1;
    
    public Maestro() {
        this.trabajadores = new ConcurrentHashMap<>();
        System.out.println("Maestro inicializado - Esperando trabajadores y clientes...");
    }
    
    @Override
    public void registerWorker(WorkerPrx worker, Current current) {
        try {
            int workerId = nextWorkerId++;
            trabajadores.put(workerId, worker);
            
            System.out.println("Trabajador registrado con ID: " + workerId);
            System.out.println("Total de trabajadores activos: " + trabajadores.size());
            
        } catch (Exception e) {
            System.err.println("Error al registrar trabajador: " + e.getMessage());
        }
    }
    
    @Override
    public void unregisterWorker(int workerId, Current current) {
        if (trabajadores.remove(workerId) != null) {
            System.out.println("Trabajador " + workerId + " desregistrado");
            System.out.println("Total de trabajadores activos: " + trabajadores.size());
        } else {
            System.out.println("Trabajador " + workerId + " no estaba registrado");
        }
    }
    
    @Override
    public long processLargeRange(int startNum, int endNum, Current current) throws RangeError {
        System.out.println("\n=== NUEVA SOLICITUD DE PROCESAMIENTO ===");
        System.out.println("Rango recibido: [" + startNum + ", " + endNum + "]");
        
        // Validar rango
        if (startNum < 1 || endNum < startNum) {
            String error = "Rango inválido: inicio=" + startNum + ", fin=" + endNum;
            System.err.println("Error: " + error);
            throw new RangeError(error);
        }
        
        // Verificar que hay trabajadores disponibles
        if (trabajadores.isEmpty()) {
            String error = "No hay trabajadores disponibles para procesar la solicitud";
            System.err.println("Error: " + error);
            throw new RangeError(error);
        }
        
        System.out.println("Dividiendo trabajo entre " + trabajadores.size() + " trabajadores...");
        
        // Dividir el rango entre los trabajadores disponibles
        List<WorkerPrx> trabajadoresActivos = new ArrayList<>(trabajadores.values());
        int numTrabajadores = trabajadoresActivos.size();
        int rangoTotal = endNum - startNum + 1;
        int rangoPorTrabajador = rangoTotal / numTrabajadores;
        int rangoRestante = rangoTotal % numTrabajadores;
        
        long resultadoTotal = 0;
        int inicioActual = startNum;
        
        // Asignar trabajo a cada trabajador
        for (int i = 0; i < numTrabajadores; i++) {
            WorkerPrx trabajador = trabajadoresActivos.get(i);
            
            // Calcular el rango para este trabajador
            int finActual = inicioActual + rangoPorTrabajador - 1;
            
            // Agregar números restantes al último trabajador
            if (i == numTrabajadores - 1) {
                finActual += rangoRestante;
            }
            
            try {
                System.out.println("Asignando rango [" + inicioActual + ", " + finActual + "] al trabajador " + (i + 1));
                
                // Enviar trabajo al trabajador
                long resultadoParcial = trabajador.processRange(inicioActual, finActual);
                resultadoTotal += resultadoParcial;
                
                System.out.println("Trabajador " + (i + 1) + " completó su tarea. Resultado parcial: " + resultadoParcial);
                
            } catch (Exception e) {
                System.err.println("Error al comunicarse con trabajador " + (i + 1) + ": " + e.getMessage());
                // En una implementación más robusta, se podría reasignar el trabajo
            }
            
            inicioActual = finActual + 1;
        }
        
        System.out.println("=== PROCESAMIENTO COMPLETADO ===");
        System.out.println("Resultado total: " + resultadoTotal);
        return resultadoTotal;
    }
    
    @Override
    public String getSystemStats(Current current) {
        StringBuilder stats = new StringBuilder();
        stats.append("Trabajadores activos: ").append(trabajadores.size());
        
        if (!trabajadores.isEmpty()) {
            stats.append(" | IDs: ");
            trabajadores.keySet().forEach(id -> stats.append(id).append(" "));
        }
        
        return stats.toString();
    }
    
    @Override
    public int[] getActiveWorkers(Current current) {
        return trabajadores.keySet().stream().mapToInt(Integer::intValue).toArray();
    }
    
    public static void main(String[] args) {
        Communicator communicator = null;
        
        try {
            // Inicializar el comunicador ICE
            communicator = Util.initialize(args);
            
            // Crear el adapter para el maestro
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "MasterAdapter", "default -p 10000"
            );
            
            // Crear e instalar el servant del maestro
            Maestro maestro = new Maestro();
            adapter.add(maestro, Util.stringToIdentity("Master"));
            
            // Activar el adapter
            adapter.activate();
            
            System.out.println("=== MAESTRO INICIADO ===");
            System.out.println("Escuchando en puerto 10000...");
            System.out.println("Esperando conexiones de trabajadores y clientes...");
            
            // Esperar hasta que se cierre el comunicador
            communicator.waitForShutdown();
            
        } catch (Exception e) {
            System.err.println("Error en el maestro: " + e.getMessage());
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
module NumbersApp
{
    // Excepción para errores de rango
    exception RangeError
    {
        string reason;
    };
    
    // Interfaz del trabajador que procesa números
    interface Worker
    {
        // Procesa un rango de números y retorna resultados parciales
        // startNum: número inicial del rango
        // endNum: número final del rango
        // returns: suma de números en el rango
        long processRange(int startNum, int endNum) throws RangeError;
        
        // Obtiene el ID del trabajador
        int getWorkerId();
        
        // Verifica si el trabajador está disponible
        bool isAvailable();
    };
    
    // Interfaz del maestro que coordina el trabajo
    interface Master
    {
        // Registra un trabajador en el sistema
        void registerWorker(Worker* worker);
        
        // Desregistra un trabajador
        void unregisterWorker(int workerId);
        
        // Procesa un rango grande dividiéndolo entre trabajadores
        long processLargeRange(int startNum, int endNum) throws RangeError;
        
        // Obtiene estadísticas del sistema
        string getSystemStats();
        
        // Obtiene la lista de trabajadores activos
        sequence<int> getActiveWorkers();
    };
    
    // Callback para notificar al cliente sobre el progreso
    interface ProgressCallback
    {
        // Notifica progreso del procesamiento
        void notifyProgress(int percentage, string message);
        
        // Notifica que el trabajo ha terminado
        void notifyCompletion(long result, string stats);
    };
    
    // Interfaz del cliente que solicita procesamiento
    interface Client
    {
        // Solicita procesamiento con callback para progreso
        void requestProcessing(int startNum, int endNum, ProgressCallback* callback);
    };
};
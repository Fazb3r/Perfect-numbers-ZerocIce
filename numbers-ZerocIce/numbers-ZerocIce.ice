module NumbersApp
{
    exception RangeError
    {
        string reason;
    };
    
    interface Worker
    {
        long processRange(int startNum, int endNum);
        int getWorkerId();
        bool isAvailable();
    };
    
    sequence<int> IntSequence;
    
    interface Master
    {
        void registerWorker(Worker* worker);
        void unregisterWorker(int workerId);
        long processLargeRange(int startNum, int endNum) throws RangeError;
        string getSystemStats();
        IntSequence getActiveWorkers();
    };
    
    interface ProgressCallback
    {
        void notifyProgress(int percentage, string message);
        void notifyCompletion(long result, string stats);
    };
    
    interface Client
    {
        void requestProcessing(int startNum, int endNum, ProgressCallback* callback);
    };
};
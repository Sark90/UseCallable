public class Timer {
    private long startTime, endTime;
    public void start() {
        startTime = System.nanoTime();
    }
    public void stop() {
        endTime = System.nanoTime();
    }
    public long getTime() {
        return endTime - startTime;
    }
    public void reset() {
        startTime = endTime = 0;
    }
}


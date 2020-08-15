package schedule;

public interface PoolCfgConstants {
    /**
     * Set the ThreadPoolExecutor's core pool size.
     */
    int corePoolSize = 16;

    int maxPoolSize = Integer.MAX_VALUE;
    /**
     * Set the capacity for the ThreadPoolExecutor's BlockingQueue.
     */
    int queueCapacity = 160;

    int keepAliveSeconds = 60;
}

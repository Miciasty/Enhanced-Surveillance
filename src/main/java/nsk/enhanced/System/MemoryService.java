package nsk.enhanced.System;

import nsk.enhanced.EnhancedSurveillance;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * The {@link MemoryService} class manages a pool of threads and internal services responsible
 * for handling plugin operations that run asynchronously, independent of the server's main thread.
 * It ensures efficient execution of background tasks while monitoring thread and queue utilization.
 * </p>
 *
 * <p>
 * This class is typically initialized during the plugin's startup in {@link EnhancedSurveillance#onEnable()}.
 * Multiple instances of MemoryService can be created to manage different sets of tasks, with each instance
 * having its own thread pool.
 * </p>
 */
public class MemoryService {

    /**
     * A static list that holds all instances of the {@link MemoryService} class.
     * This list is used to keep track of all created {@link MemoryService} instances, allowing
     * for operations such as load balancing, service utilization monitoring, and shutting down
     * all services when necessary.
     */
    private static final List<MemoryService> services = new ArrayList<>();
    private static int serviceCounter = 0;

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private final int id;
    private final ExecutorService service;
    private final ThreadPoolExecutor threadPoolExecutor;
    private int eventCounter = 0;

    /**
     * Creates a new {@link MemoryService} instance with a fixed thread pool of the specified size.
     *
     * @param threads the number of threads in the pool
     */
    public MemoryService(int threads) {
        this.id = serviceCounter++;
        this.service = Executors.newFixedThreadPool(threads);
        this.threadPoolExecutor = (ThreadPoolExecutor) service;

        services.add(this);
    }

    // --- --- --- --- --- -- SERVICE METHODS -- --- --- --- --- --- //

    /**
     * Submits a task for execution in this service's thread pool and logs the event.
     * If the number of logged events reaches 20, the service's utilization is checked and logged.
     *
     * @param task the task to be executed
     */
    public void logEvent(Runnable task) {
        service.submit(task);
        eventCounter++;

        if (eventCounter >= 25) {
            getServiceUtilization();
            eventCounter = 0;
        }
    }

    /**
     * Submits a task for execution in this service's thread pool without additional logging.
     *
     * @param task the task to be executed
     */
    public void submit(Runnable task) {
        service.submit(task);
    }

    /**
     * Returns the unique ID of this service.
     *
     * @return the service ID
     */
    public int getServiceID() {
        return id;
    }

    /**
     * Returns the current size of the task queue.
     *
     * @return the queue size
     */
    public int getQueueSize() {
        return threadPoolExecutor.getQueue().size();
    }

    /**
     * Returns the number of active threads currently executing tasks.
     *
     * @return the number of active threads
     */
    public int getActiveThreadCount() {
        return threadPoolExecutor.getActiveCount();
    }

    /**
     * Calculates the current thread utilization as a percentage of active threads
     * compared to the maximum number of threads in the pool.
     *
     * @return the thread utilization percentage
     */
    private double getThreadUtilization() {
        int activeThreads = threadPoolExecutor.getActiveCount();
        int maxThreads = threadPoolExecutor.getMaximumPoolSize();
        return ((double) activeThreads) / ((double) maxThreads) * 100;
    }

    /**
     * Calculates the current queue utilization as a percentage of the number of tasks
     * in the queue compared to the total capacity of the queue.
     *
     * @return the queue utilization percentage
     */
    private double getQueueUtilization() {
        int queueSize = getQueueSize();
        int queueCapacity = threadPoolExecutor.getQueue().remainingCapacity() + queueSize;
        return (queueCapacity > 0) ? ((double) queueSize) / ((double) queueCapacity) * 100 : 0.0;
    }

    /**
     * Logs the current utilization of the service's task queue.
     * Provides warnings if queue utilization exceeds certain thresholds.
     */
    public void getServiceUtilization() {
        double queueUtilization = getQueueUtilization();

        if (queueUtilization >= 95) {
            EnhancedLogger.log().severe( "Queue of <aqua>{" + id + "} service</aqua> utilization is higher than <gold>95%");
            checkServiceOverload();
        } else if (queueUtilization >= 90) {
            EnhancedLogger.log().warning("Queue of <aqua>{" + id + "} service</aqua> utilization is higher than <red>90%");
            checkServiceOverload();
        } else if (queueUtilization >= 75) {
            EnhancedLogger.log().warning("Queue of <gold>{" + id + "} service</gold> utilization is higher than <red>75%");
        } else if (queueUtilization >= 50) {
            EnhancedLogger.log().warning("Queue of <yellow>{" + id + "} service</yellow> utilization is higher than <yellow>50%");
        } else if (queueUtilization >= 25) {
            EnhancedLogger.log().warning("Queue of <green>{" + id + "} service</green> utilization is higher than <green>25%");
        }

    }

    private void checkServiceOverload() {
        int count = services.size();
        int overloadedServices = 0;

        for (MemoryService service : MemoryService.services) {
            if (service.getQueueUtilization() >= 75) {
                EnhancedLogger.log().info("Service number: <green>{" + service.getServiceID() + "}</green>, utilization: <red>" + service.getQueueUtilization());
                overloadedServices++;
            }
        }

        if (overloadedServices >= count / 2) {
            EnhancedLogger.log().warning("<aqua>Recommendation:</aqua> More than <red>50%</red> of services have queue utilization above <red>75%</red>. <green>Consider adding a new service or increasing threads per service.</green>");
        }

    }

    // --- --- --- --- --- --- STATIC METHODS --- --- --- --- --- --- //

    /**
     * Returns the number of available threads that are not currently occupied.
     *
     * @return the number of available threads
     */
    public static int getAvailableThreads() {

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int activeThreadCount   = ManagementFactory.getThreadMXBean().getThreadCount();

        int estimatedActiveThreads = activeThreadCount - availableProcessors;

        return Math.max(0, estimatedActiveThreads);
    }

    /**
     * Executes the given task using one of {@link MemoryService} services with the least loaded task queue.
     *
     * @param task the task to be executed
     * @throws IllegalStateException if no MemoryService instances are initialized
     */
    public static void execute(Runnable task) {
        MemoryService leastLoadedService = getLeastLoadedService();
        if (leastLoadedService != null) {
            try {
                leastLoadedService.submit(task);
            } catch (Exception e) {
                EnhancedLogger.log().severe(e.getMessage());
            }
        } else {
            throw new IllegalStateException("No MemoryService services are initialized.");
        }
    }

    /**
     * Logs an event asynchronously using one of {@link MemoryService} services with the least loaded task queue.
     *
     * @param task the task to be logged
     * @throws IllegalStateException if no MemoryService instances are initialized
     */
    public static void logEventAsync(Runnable task) {
        MemoryService leastLoadedService = getLeastLoadedService();
        if (leastLoadedService != null) {
            try {
                leastLoadedService.logEvent(task);
            } catch (Exception e) {
                EnhancedLogger.log().severe(e.getMessage());
            }
        } else {
            throw new IllegalStateException("No MemoryService services are initialized.");
        }
    }

    /**
     * Selects the {@link MemoryService} service with the least loaded task queue.
     * This method iterates over all initialized {@link MemoryService} services and returns the one
     * with the smallest queue size.
     *
     * @return the {@link MemoryService} service with the least loaded queue, or null if no services are initialized
     */
    private static MemoryService getLeastLoadedService() {
        if (services.isEmpty()) return null;

        MemoryService leastLoaded = services.get(0);
        for (MemoryService service : services) {
            if (service.getQueueSize() < leastLoaded.getQueueSize()) {
                leastLoaded = service;
            }
        }
        return leastLoaded;
    }

    /**
     * Initializes the specified number of {@link MemoryService} services, each with a fixed number of threads.
     * If the required number of threads exceeds the available threads, fewer services are initialized.
     *
     * @param count the number of {@link MemoryService} services to create
     * @param threadsPerService the number of threads for each service
     */
    public static void initializeServices(int count, int threadsPerService) {

        EnhancedLogger.log().info("Available threads: <gold>" + getAvailableThreads() + " - " + count * threadsPerService + "</gold> = <green>" + (getAvailableThreads() - (count * threadsPerService)) );

        EnhancedLogger.log().info("Initializing <aqua>" + count + "</aqua> services and <aqua>" + threadsPerService * count + "</aqua> threads.");

        if (count <= 0) {
            EnhancedLogger.log().severe("Invalid number of threads: <gold>" + count);
            return;
        }

        if (threadsPerService <= 0) {
            EnhancedLogger.log().severe("You need to specify at least one thread per service.");
            return;
        }

        int availableThreads = getAvailableThreads();
        int totalRequiredThreads = count * threadsPerService;

        while (count > 0 && (availableThreads - 1) < totalRequiredThreads) {
            count--;
            totalRequiredThreads = count * threadsPerService;
        }

        if (count > 0) {
            for (int i=0; i < count; i++) {
                try {
                    new MemoryService(threadsPerService);
                } catch (Exception e) {
                    EnhancedLogger.log().severe(e.getMessage());
                }
            }
            EnhancedLogger.log().fine("MemoryService initialized with: <yellow>" + count + "</yellow> services, each with <yellow>" + threadsPerService + "</yellow> threads.");
        } else {
            EnhancedLogger.log().severe("MemoryService initialization canceled. Insufficient threads available.");
        }
    }

    /**
     * Shuts down all {@link MemoryService} services.
     * This method should is called in {@link EnhancedSurveillance#onDisable()} to ensure a graceful termination of background tasks.
     */
    public static void shutdownAllServices() {

        EnhancedLogger.log().warning("Shutting down all services.");

        for (MemoryService service : services) {
            try {
                service.service.shutdown();
            } catch (Exception e) {
                EnhancedLogger.log().severe(e.getMessage());
            }
        }

        EnhancedLogger.log().fine("Shutting down <gold>MemoryService</gold> is finished!");
    }

}

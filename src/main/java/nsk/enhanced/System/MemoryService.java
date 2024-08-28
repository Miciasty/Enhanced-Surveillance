package nsk.enhanced.System;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MemoryService {

    private static final List<MemoryService> services = new ArrayList<>();
    private static int serviceCounter = 0;

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private final int id;
    private final ExecutorService service;
    private final ThreadPoolExecutor threadPoolExecutor;
    private int eventCounter = 0;

    public MemoryService(int threads) {
        this.id = serviceCounter++;
        this.service = Executors.newFixedThreadPool(threads);
        this.threadPoolExecutor = (ThreadPoolExecutor) service;

        services.add(this);
    }

    // --- --- --- --- --- -- INSTANCE METHODS -- --- --- --- --- --- //

    public void logEvent(Runnable task) {
        service.submit(task);
        eventCounter++;

        if (eventCounter >= 20) {
            getServiceUtilization();
            eventCounter = 0;
        }
    }

    public void submit(Runnable task) {
        service.submit(task);
    }

    public int getServiceID() {
        return id;
    }

    public int getQueueSize() {
        return threadPoolExecutor.getQueue().size();
    }

    public int getActiveThreadCount() {
        return threadPoolExecutor.getActiveCount();
    }

    private double getThreadUtilization() {
        int activeThreads = threadPoolExecutor.getActiveCount();
        int maxThreads = threadPoolExecutor.getMaximumPoolSize();
        return ((double) activeThreads) / ((double) maxThreads) * 100;
    }
    private double getQueueUtilization() {
        int queueSize = getQueueSize();
        int queueCapacity = threadPoolExecutor.getQueue().remainingCapacity() + queueSize;
        return (queueCapacity > 0) ? ((double) queueSize) / ((double) queueCapacity) * 100 : 0.0;
    }

    public void getServiceUtilization() {
        double threadUtilization = getThreadUtilization();
        double queueUtilization = getQueueUtilization();

        if (queueUtilization >= 95) {
            EnhancedLogger.log().severe("Queue " + id + " utilization is higher than <red>95%");
        } else if (queueUtilization >= 75) {
            EnhancedLogger.log().warning("Queue " + id + " utilization is higher than <red>75%");
        } else if (queueUtilization >= 50) {
            EnhancedLogger.log().warning("Queue " + id + " utilization is higher than <gold>50%");
        } else if (queueUtilization >= 25) {
            EnhancedLogger.log().warning("Queue " + id + " utilization is higher than <green>25%");
        }

        if (threadUtilization > 50) {
            EnhancedLogger.log().info(String.format("Service: <gold>(%s)</gold>, Thread utilization: <aqua>%.2f%%</aqua>, Queue utilization: <aqua>%.2f%%</aqua>", id, threadUtilization, queueUtilization));
        }

    }

    // --- --- --- --- --- --- STATIC METHODS --- --- --- --- --- --- //

    public static int getAvailableThreads() {

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int activeThreadCount   = ManagementFactory.getThreadMXBean().getThreadCount();

        int estimatedActiveThreads = activeThreadCount - availableProcessors;

        return Math.max(0, estimatedActiveThreads);
    }

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

package nsk.enhanced.System;

import nsk.enhanced.EnhancedSurveillance;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MemoryService {

    private final static EnhancedSurveillance plugin = ES.getInstance();
    private static final List<MemoryService> services = new ArrayList<MemoryService>();
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

        if (eventCounter >= 50) {
            getServiceUtilization();
            eventCounter = 0;
        }
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
        return ((double) queueSize) / ((double) queueCapacity) * 100;
    }

    public void getServiceUtilization() {
        double threadUtilization = getThreadUtilization();
        double queueUtilization = getQueueUtilization();

        if (queueUtilization >= 95) {
            plugin.getEnhancedLogger().severe("Queue " + id + " utilization is higher than <red>95%");
        } else if (queueUtilization >= 75) {
            plugin.getEnhancedLogger().warning("Queue " + id + " utilization is higher than <red>75%");
        } else if (queueUtilization >= 50) {
            plugin.getEnhancedLogger().warning("Queue " + id + " utilization is higher than <gold>50%");
        } else if (queueUtilization >= 25) {
            plugin.getEnhancedLogger().warning("Queue " + id + " utilization is higher than <green>25%");
        }

        plugin.getEnhancedLogger().info(String.format("Service: <gold>(%s)</gold>, Thread utilization: <aqua>%.2f%%</aqua>, Queue utilization: <aqua>%.2f%%</aqua>", id, threadUtilization, queueUtilization));
    }

    // --- --- --- --- --- --- STATIC METHODS --- --- --- --- --- --- //

    public static int getAvailableThreads() {

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int activeThreadCount   = ManagementFactory.getThreadMXBean().getThreadCount();

        int estimatedActiveThreads = activeThreadCount - availableProcessors;

        return Math.max(0, estimatedActiveThreads);
    }

    public static void logEventAsync(Runnable task) {
        MemoryService leastLoadedService = getLeastLoadedService();
        if (leastLoadedService != null) {
            try {
                leastLoadedService.logEvent(task);
            } catch (Exception e) {
                ES.getInstance().getEnhancedLogger().severe(e.getMessage());
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

        plugin.getEnhancedLogger().info("Available threads: <gold>" + getAvailableThreads() + " - " + count * threadsPerService + "</gold> = <green>" + (getAvailableThreads() - (count * threadsPerService)) );

        plugin.getEnhancedLogger().info("Initializing <aqua>" + count + "</aqua> services and <aqua>" + threadsPerService * count + "</aqua> threads.");

        if (count <= 0) {
            ES.getInstance().getEnhancedLogger().severe("Invalid number of threads: <gold>" + count);
            return;
        }

        if (threadsPerService <= 0) {
            ES.getInstance().getEnhancedLogger().severe("You need to specify at least one thread per service.");
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
                    ES.getInstance().getEnhancedLogger().severe(e.getMessage());
                }
            }
            ES.getInstance().getEnhancedLogger().fine("MemoryService initialized with: <yellow>" + count + "</yellow> services, each with <yellow>" + threadsPerService + "</yellow> threads.");
        } else {
            ES.getInstance().getEnhancedLogger().severe("MemoryService initialization canceled. Insufficient threads available.");
        }
    }

    public static void shutdownAllServices() {

        plugin.getEnhancedLogger().warning("Shutting down all services.");

        for (MemoryService service : services) {
            try {
                service.service.shutdown();
            } catch (Exception e) {
                ES.getInstance().getEnhancedLogger().severe(e.getMessage());
            }
        }

        plugin.getEnhancedLogger().fine("Shutting down <gold>MemoryService</gold> is finished!");
    }

}

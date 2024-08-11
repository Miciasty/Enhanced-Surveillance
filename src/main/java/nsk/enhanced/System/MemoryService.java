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

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private final ExecutorService service;
    private final ThreadPoolExecutor threadPoolExecutor;

    public MemoryService(int threads) {
        this.service = Executors.newFixedThreadPool(threads);
        this.threadPoolExecutor = (ThreadPoolExecutor) service;

        services.add(this);
    }

    // --- --- --- --- --- -- INSTANCE METHODS -- --- --- --- --- --- //

    public void logEvent(Runnable task) {
        service.submit(task);
    }

    public int getQueueSize() {
        return threadPoolExecutor.getQueue().size();
    }

    public int getActiveThreadCount() {
        return threadPoolExecutor.getActiveCount();
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

        if (count <= 0) {
            ES.getInstance().getEnhancedLogger().severe("Invalid number of threads: " + count);
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
            ES.getInstance().getEnhancedLogger().fine("MemoryService initialized with : " + count + " services, each with " + threadsPerService + " threads.");
        } else {
            ES.getInstance().getEnhancedLogger().severe("MemoryService initialization canceled. Insufficient threads available.");
        }
    }

    public static void shutdownAllServices() {
        for (MemoryService service : services) {
            try {
                service.service.shutdown();
            } catch (Exception e) {
                ES.getInstance().getEnhancedLogger().severe(e.getMessage());
            }
        }
    }

}

package org.szaumoor;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Singleton that encapsulates all the functionalities regarding shutting down according to various conditions.
 */
enum ShutdownManager {
    ;
    private static final RunningOS underlyingOS = RunningOS.resolveOS();


    /**
     * Loads into a collection of your choice the running processes that your computer
     * was running at the moment, using appropriate commands for Linux or Windows.
     *
     * @param collection Collection to fill.
     * @throws RuntimeException if there was any IOException, crashing the program
     */
    static void loadRunningProcesses(final Collection<RunningProcess> collection) {
        try {
            Process exec = Runtime.getRuntime().exec(underlyingOS.taskListCommand);
            InputStream inputStream = exec.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            collection.addAll(reader.lines()
                    .skip(1) // header skip
                    .map(s -> new RunningProcess(s, underlyingOS))
                    .toList());
            reader.close();
            inputStream.close();
        } catch (IOException ex) {
            throw new RuntimeException("There was a problem reading from the task list command...");
        }
    }

    /**
     * Shuts the computer down if a particular process is no longer activate according to the
     * loadRunningProcesses method, with recurring checks based on the user input. It will
     * keep checking in a separate thread until that condition is true, then it will shut down
     * the computer in 60 seconds.
     *
     * @param process               The process that we want to examine
     * @param durationBetweenChecks The amount of time between each check in minutes.
     */
    static void shutdownAfterProcessIsOver(final RunningProcess process, final Duration durationBetweenChecks) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        Set<RunningProcess> processes = new HashSet<>(350);
        service.scheduleAtFixedRate(() -> {
            System.out.print("\nChecking status of task: ");
            loadRunningProcesses(processes);
            boolean processIsRunning = processes.contains(process);
            processes.clear();
            if (!processIsRunning) {
                service.shutdown();
                System.out.println("Process no longer found, shutdown will start in 60 seconds");
                shutdownAfterDelay(Duration.ofSeconds(60));
            } else System.out.println("Process is still going on");
        }, 0, durationBetweenChecks.toMinutes(), TimeUnit.MINUTES);
    }

    /**
     * Sends the shutdown signal after a particular time of a day has passed.
     * Checks every minute if this is true. When it is, it will shut down the computer
     * in 60 seconds.
     *
     * @param timeForShutdown The local date and time at which the computer should start shutting down
     */
    static void shutdownAtATime(final LocalDateTime timeForShutdown) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            var now = LocalDateTime.now();
            var nowDay = now.getDayOfMonth();
            var nowHour = now.getHour();
            var nowMinute = now.getMinute();
            if (nowDay == timeForShutdown.getDayOfMonth()
                    && nowHour == timeForShutdown.getHour() &&
                    nowMinute >= timeForShutdown.getMinute()) {
                service.shutdown();
                shutdownAfterDelay(Duration.ofMinutes(1));
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * Shuts the Windows or Unix system down after one minute or the specified amount of time in minutes.
     * Defaults to a single minute if the duration is below a minute or null.
     *
     * @param duration The time before the shutdown starts.
     */
    static void shutdownAfterDelay(Duration duration) {
        if (duration == null || duration.getSeconds() < 60) duration = Duration.of(1, ChronoUnit.MINUTES);
        long minutes = duration == null ? 1L : duration.toMinutes();
        try {
            Runtime.getRuntime().exec(underlyingOS.shutdownCommand + (underlyingOS == RunningOS.WINDOWS ? minutes * 60 : minutes));
        } catch (IOException ex) {
            System.err.println("There was an error executing the shutdown command...");
        }
        System.exit(0);
    }
}

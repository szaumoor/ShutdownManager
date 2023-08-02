import java.io.*;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public enum ShutdownManager {
    INSTANCE;

    private static OS underlyingOS;

    static {
        resolveOS();
    }

    public List<String> getListOfTasks(boolean numbered) {
        try {
            Process exec = Runtime.getRuntime().exec(underlyingOS.taskListCommand);
            InputStream inputStream = exec.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            AtomicInteger counter = new AtomicInteger(1);
            List<String> listOfTasks = reader.lines()
                    .skip(underlyingOS.linesToSkip)
                    .map(s -> numbered ? counter.getAndIncrement() + ". " + s : s)
                    .toList();
            reader.close();
            inputStream.close();
            return listOfTasks;
        } catch (IOException ex) {
            throw new RuntimeException("There was a problem reading from the task list command...");
        }
    }

    public void shutdownAfterProcessIsOver(final String process, final Duration durationBetweenChecks) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            System.out.println("\nChecking status of task: ");
            boolean processIsRunning = getListOfTasks(false).contains(process);
            if (!processIsRunning) {
                System.out.println("Process no longer found, shutdown will start in 60 seconds");
                shutdownAfterDelay(Duration.ofSeconds(60));
                service.shutdown();
            } else System.out.println("Process is still going on.");
        }, 0, durationBetweenChecks.toMinutes(), TimeUnit.MINUTES);

    }

    public void shutdownAfterDelay(final Duration duration) {
        long minutes = duration == null ? 1L : duration.toMinutes();
        try {
            Runtime.getRuntime().exec(underlyingOS.shutdownCommand + (underlyingOS == OS.WINDOWS ? minutes * 60 : minutes));
        } catch (IOException ex) {
            System.err.println("There was an error executing the shutdown command...");
        }
        System.exit(0);
    }

    private static void resolveOS() {
        final String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) // if it's Windows
            underlyingOS = OS.WINDOWS;
        else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) { // if it's unix
            underlyingOS = OS.UNIX;
        } else {
            throw new RuntimeException("OS not recognized");
        }
    }
}

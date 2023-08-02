import java.time.Duration;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var p = new ProcessInfo("System Idle Process              0 Services                   0          8 K", OS.WINDOWS);
        final ShutdownManager manager = ShutdownManager.INSTANCE;
        final Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to...\n1. Shutdown the computer on a timer\n2. Shutdown after a process stops\n");
        String response;
        while (true) {
            response = scanner.nextLine();
            if (response.equals("1") || response.equals("2"))
                break;
            System.out.println("Sorry, that's not an option, try again.");
        }
        if (response.equals("1")) {
            System.out.print("Type how much time in minutes should pass before the shut down: ");
            int input;
            while (true) {
                try {
                    input = scanner.nextInt();
                } catch (NumberFormatException ex) {
                    System.out.println("That's not a number, try again.");
                    continue;
                }
                if (input < 1 || input > 30) {
                    System.out.println("Only positive durations are valid, try again.");
                } else break;
            }
            manager.shutdownAfterDelay(Duration.ofMinutes(input));
        } else {
            System.out.println("Here's a numbered list of the current tasks: \n");
            List<String> listOfTasks = manager.getListOfTasks(true);
            listOfTasks.forEach(System.out::println);
            System.out.print("\nType the number in this list associated with the task you want to track: ");
            int input;
            while (true){
                try {
                    input = scanner.nextInt();
                } catch (NumberFormatException ex) {
                    System.out.println("That's not a number, try again: ");
                    continue;
                }
                if (input < 1 || input > listOfTasks.size()) {
                    System.out.print("\nSorry, but that's not in the list, try again: ");
                    continue;
                }
                break;
            }
            String choice = listOfTasks.get(input-1);
            String processName = choice.substring(choice.indexOf(".") + 2);
            System.out.println("You selected: '" + processName + "'");
            System.out.print("Monitoring that process now. How many minutes between each check (1-30 minutes)? ");
            int durationBetweenChecks;
            while (true){
                try {
                    durationBetweenChecks = scanner.nextInt();
                } catch (NumberFormatException ex) {
                    System.out.println("That's not a number, try again: ");
                    continue;
                }
                if (durationBetweenChecks < 1 || durationBetweenChecks > 30) {
                    System.out.print("\nSorry, but only values between 1 and 30 minutes (included) are valid, try again: ");
                    continue;
                }
                break;
            }
            manager.shutdownAfterProcessIsOver(processName, Duration.ofMinutes(durationBetweenChecks));
        }
        scanner.close();
    }
}

package org.szaumoor;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.szaumoor.Validator.inInterval;
import static org.szaumoor.Validator.inputIsInteger;

/**
 * Singleton that encapsulates all the code regarding menu functionality a.k.a. the visual part of the program.
 */
enum CmdMenu {
    ;
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Method to start the whole menu flow. Frees up resources automatically.
     */
    static void start() {
        System.out.println("Do you want to...\n1. Shutdown the computer on a timer\n2. Shutdown after a process stops\n3. Shutdown at a particular local time");
        int option = handleNumericResponse(3, null);
        switch (option) {
            case 1 -> handleShutdownOnTimeLimit();
            case 2 -> handleShutdownByProcess();
            case 3 -> handleShutdownAtTime();
        }
        scanner.close();
    }

    /**
     * Handles option 1 of the menu: countdown to a shutdown.
     */
    private static void handleShutdownOnTimeLimit() {
        System.out.print("Type how much time in minutes should pass before the shut down (1-1440): ");
        int minutes = handleNumericResponse(1440, "Can only select between 1 to 1440 minutes (a full day)");
        ShutdownManager.shutdownAfterDelay(Duration.ofMinutes(minutes));
    }

    /**
     * Handles option 2 of the menu: monitors a process and triggers a shutdown after this process dies.
     */
    private static void handleShutdownByProcess() {
        System.out.println("Here's a numbered list of the current tasks: \n");
        final List<RunningProcess> processes = new ArrayList<>(300);
        ShutdownManager.loadRunningProcesses(processes);
        var counter = new AtomicInteger(1);
        processes.forEach(s -> System.out.println(counter.getAndIncrement() + "--> " + s));
        System.out.print("\nType the number in this list associated with the task you want to track: ");
        var process = processes.get(handleNumericResponse(processes.size(), "\nSorry, but that's not in the list, try again: ") - 1);
        System.out.println("You selected: '" + process + "'");
        System.out.print("Monitoring that process now. How many minutes between each check (1-30 minutes)? ");
        int durationBetweenChecks = handleNumericResponse(30, "Checks are only available once every 1 to 30 minutes, try again");
        ShutdownManager.shutdownAfterProcessIsOver(process, Duration.ofMinutes(durationBetweenChecks));
    }

    /**
     * Handles option 3 of the menu: schedules a shutdown at particular hour and minute today or in any n < 100 subsequent days.
     */
    private static void handleShutdownAtTime() {
        System.out.println("What local datetime would you like the the computer to start shutting down?");
        System.out.println("Please use the following format: '<0-99 days since today> HH:mm'");
        System.out.println("Example: 2 05:27. This would make the computer shut down at 5:27 2 days from today.");
        LocalDateTime localTime = handleTextResponse(Validator::parseEndDate);
        System.out.println("Shutting down on the following date time: " + localTime.toLocalDate() + " at " + localTime.toLocalTime());
        System.out.println("Do not close the program until then");
        ShutdownManager.shutdownAtATime(localTime);
    }

    /**
     * Handles user input where a number is expected for the purpose of menu interaction.  In the context of this
     * application's menu, valid numbers start at 1. Invalid inputs will make the program keep asking until a proper
     * one is passed, which is one that must: <br><br>
     * <p>
     * 1. Be parsed as an integer, no more, no less<br>
     * 2. Be inside the interval 1 <= n <= endInclusive
     * <br><br>
     *
     * @param endInclusive The last valid element for handling the input.
     * @param errorMsg     Optional custom error message. Defaults to one provided if a null value is passed.
     * @return The validated integer choice to select any valid item in the menu.
     */
    private static int handleNumericResponse(int endInclusive, String errorMsg) {
        if (1 >= endInclusive)
            throw new RuntimeException("End of interval is incorrect, has to be above 1, it was " + endInclusive);
        errorMsg = Optional.ofNullable(errorMsg).orElse("Sorry, that's not a valid option, try again");
        Optional<Integer> choice;
        while (true) {
            choice = inputIsInteger(scanner);
            if (choice.isEmpty()) continue;
            if (inInterval(choice.get(), endInclusive, errorMsg))
                return choice.get();
        }
    }

    /**
     * Handles user input where a String is expected for the purpose of parsing it and converting it into a
     * different object.
     * <br><br>
     *
     * @param function Function used evaluate a String (a 'text') which will return whatever type of output
     *                 that is desired, wrapped in an optional.
     * @param <T>      Generic object to allow different kinds of return types for the function and this method.
     * @return An object of type T that will be returned from the passed function after it's validated and
     * unwrapped from the Optional.
     */
    private static <T> T handleTextResponse(final Function<String, Optional<T>> function) {
        if (function == null)
            throw new NullPointerException("This method requires a non-null function to be passed to it");
        String response;
        scanner.nextLine(); // nonsense to avoid scanner weirdness messing with the flow in the menu
        while (true) {
            response = scanner.nextLine();
            Optional<T> optTime = function.apply(response);
            if (optTime.isPresent())
                return optTime.get();
        }
    }
}

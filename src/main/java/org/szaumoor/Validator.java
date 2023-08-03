package org.szaumoor;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

/**
 * Singleton that encapsulates static methods for basic evaluation of data in the command line.
 */
enum Validator {
    ;

    /**
     * Checks if a number is between 1 to 'end' included.
     * Additionally it can also print an error message to the standard output
     * if the user wishes to.
     *
     * @param num      Number to check from.
     * @param end      The last valid number in the sequence starting at 1
     * @param errorMsg Optional error message to output if the check fails
     * @return True if the number is valid, false otherwise.
     */
    static boolean inInterval(int num, int end, String errorMsg) {
        boolean check = num >= 1 && num <= end;
        if (!check) System.out.println(errorMsg == null ? "" : errorMsg);
        return check;
    }

    /**
     * Takes a scanner and tries to get some integer input.
     * Returns an Optional of integers, which will be empty if the scanner
     * gets input that is not an integer and nothing but an integer.
     *
     * @param scanner Scanner to use for checking the input
     * @return An Optional of integers, which may be empty if getting correct input was not possible.
     */
    static Optional<Integer> inputIsInteger(final Scanner scanner) {
        try {
            return Optional.of(scanner.nextInt());
        } catch (InputMismatchException ex) {
            System.out.println("Input is not an integer. Try again");
            scanner.nextLine();
            return Optional.empty();
        }
    }

    /**
     * Parses an end date needed for scheduling shutdowns in this particular format: '&lt;number_of_days_from_today&gt HH:mm&gt;',
     * where that number of days is anything between 0 and 99, 'HH' are hours in a 24-hour format, and 'mm' are minutes.
     * <br><br>
     * It requires this date to conform to this format, within the appropriate ranges, and in the future by at least one minute,
     * and it will return an Optional of a LocalDateTime, which may be empty if any of those conditions are unmet.
     * <br><br>
     *
     * @param s The String to be parsed as a possible LocalDateTime
     * @return An Optional of LocalDateTime which will be empty if it's incorrect as defined above, or containing the appropriate
     * LocalDateTime.
     */
    static Optional<LocalDateTime> parseEndDate(final String s) {
        if (!s.matches("^\\d{1,2}\\s\\d{1,2}:\\d{1,2}$")) {
            System.out.println("Format of date is incorrect. Try again");
            return Optional.empty();
        } else {
            var tokens = s.split("[:|\\s]");
            int days = parseInt(tokens[0]);
            int hour = parseInt(tokens[1]);
            int minutes = parseInt(tokens[2]);
            LocalDateTime pickedDateTime;
            LocalDate dateNow = LocalDate.now();
            try {
                pickedDateTime = LocalDateTime.of(dateNow.plusDays(days), LocalTime.of(hour, minutes));
                var pickedTime = pickedDateTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES);
                var today = LocalDateTime.now().toLocalTime().truncatedTo(ChronoUnit.MINUTES);
                System.out.println(pickedTime + " " + today);
                if (days == 0 && pickedTime.isBefore(today.plusMinutes(1))) {
                    System.out.println("The passed date time is not set correctly in the future. Try again");
                    return Optional.empty();
                }
            } catch (DateTimeException ex) {
                System.out.println("The passed time is not within a correct range. Try again");
                return Optional.empty();
            }
            return Optional.of(pickedDateTime);
        }
    }
}

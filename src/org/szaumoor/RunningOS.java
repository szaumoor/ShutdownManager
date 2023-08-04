package org.szaumoor;

/**
 * This enum encapsulates a type of operative system for the purposes of determining commands to use, as well
 * as different parsing algorithms to get task information.
 */
enum RunningOS {
    WINDOWS("shutdown /s /t ", "shutdown /a", "tasklist /fo csv"),
    UNIX("shutdown -h +", "shutdown -c", "ps -e");

    final String shutdownCommand;
    final String cancelShutdownCommand;
    final String taskListCommand;

    RunningOS(String shutdownCommand, String cancelShutdownCommand, String taskListCommand) {
        this.shutdownCommand = shutdownCommand;
        this.cancelShutdownCommand = cancelShutdownCommand;
        this.taskListCommand = taskListCommand;
    }

    /**
     * Returns an instance of the appropriate OS by analyzing the output of the property "os.name" of
     * the system. Could be fragile, but so far I have found no issues.
     *
     * @return The instance of the OS that your system uses, with the associated commands for the purposes
     * of this program.
     */
    static RunningOS resolveOS() {
        final String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) { // if it's a windows system
            return RunningOS.WINDOWS;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) { // if it's unix
            return RunningOS.UNIX;
        } else {
            throw new RuntimeException("OS not recognized");
        }
    }
}
public enum OS {
    WINDOWS("shutdown /s /t ", "shutdown /a", "tasklist /fo csv", 2),
    UNIX("shutdown -h +", "shutdown -c", "ps -e", 1);

    final String shutdownCommand;
    final String cancelShutdownCommand;
    final String taskListCommand;
    final int linesToSkip;

    OS(String shutdownCommand, String cancelShutdownCommand, String taskListCommand, int linesToSkip) {
        this.shutdownCommand = shutdownCommand;
        this.cancelShutdownCommand = cancelShutdownCommand;
        this.taskListCommand = taskListCommand;
        this.linesToSkip = linesToSkip;
    }
}
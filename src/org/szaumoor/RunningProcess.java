package org.szaumoor;

import java.util.Objects;

/**
 * Utility class to encapsulate the information on each process for the purposes of identifying a process.
 * Currently, a process is considered equal to another if it has the same PID and name.
 */
final class RunningProcess {
    private String processName;
    private String pid;

    /**
     * Constructor that will parse a line differently depending on the underlying OS.
     *
     * @param line Line to parse. Expects a very specific format.
     * @param os Underlying OS running this program
     */
    RunningProcess(final String line, final RunningOS os) {
        if (os == RunningOS.WINDOWS) parseWindowsLine(line);
        else parseUnixLine(line);
    }

    /**
     * Parses a line coming from the list of tasks in a Unix system. See the RunningOS class to see
     * what command is used.
     *
     * @param line Line to parse
     */
    private void parseUnixLine(final String line) {
        var splitTokens = line.trim().replaceAll("\\s+", ",").split(",");
        int length = splitTokens.length;
        processName = splitTokens[length-1];
        pid = splitTokens[0];
    }

    /**
     * Parses a line coming from the list of tasks in a Windows system. See the RunningOS class to see
     * what command is used.
     *
     * @param line Line to parse
     */
    private void parseWindowsLine(final String line) {
        var splitTokens = line.trim().replaceAll("\"", "").split(",");
        processName = splitTokens[0];
        pid = splitTokens[1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunningProcess that = (RunningProcess) o;
        return Objects.equals(processName, that.processName) && Objects.equals(pid, that.pid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processName, pid);
    }

    @Override
    public String toString() {
        return "PID: " + pid + ", " + "Name: " + processName;
    }
}

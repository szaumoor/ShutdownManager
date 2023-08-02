import java.util.List;
import java.util.Objects;

public final class ProcessInfo {
    private String processName;
    private String pid;

    public ProcessInfo(final String line, final OS os) {
        if (os == OS.WINDOWS) parseWindowsLine(line);
        else parseUnixLine(line);
    }

    private void parseUnixLine(final String line) {
        var splitTokens = line.trim().replaceAll("\\s+", ",").split(",");
        int length = splitTokens.length;
        processName = splitTokens[length-1];
        pid = splitTokens[0];
    }

    private void parseWindowsLine(final String line) {
        var splitTokens = line.trim().replaceAll("\"", "").split(",");
        processName = splitTokens[0];
        pid = splitTokens[1];
    }

    public String getProcessName() {
        return processName;
    }

    public String getPid() {
        return pid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessInfo that = (ProcessInfo) o;
        return Objects.equals(processName, that.processName) && Objects.equals(pid, that.pid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processName, pid);//
    }

    @Override
    public String toString() {
        return "PID: " + pid + ", " + "Name: " + processName;
    }
}

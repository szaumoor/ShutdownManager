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
        var splitTokens = line.trim().split(",");
       // processName = splitTokens[0];
       // pid = splitTokens[1];
        System.out.println(List.of(splitTokens));
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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
        return Objects.hash(processName, pid);
    }
}

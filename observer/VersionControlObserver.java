import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VersionControlObserver implements DocumentObserver {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static class Snapshot {
        public final int version; public final String event, timestamp;
        Snapshot(int v, String e) { version=v; event=e; timestamp=LocalDateTime.now().format(FMT); }
    }

    private final List<Snapshot> snapshots = new ArrayList<>();

    @Override public void onEvent(String event, Object data) {
        Snapshot s = new Snapshot(snapshots.size() + 1, event);
        snapshots.add(s);
        System.out.printf("  [VersionCtrl] event=%-26s -> snapshot v%d created%n", "'" + event + "'", s.version);
    }
    public List<Snapshot> getSnapshots() { return List.copyOf(snapshots); }
}

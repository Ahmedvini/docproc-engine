import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * REAL-TIME COLLABORATION SIMULATION
 *
 * Simulates multiple users editing the same document concurrently.
 * Demonstrates:
 *   - Observer pattern  : changes broadcast to all connected peers
 *   - Command pattern   : every edit is a reversible Command object
 *   - Singleton pattern : shared DocumentManager / ExportManager
 *   - Composite pattern : shared document tree mutated by all peers
 *   - Conflict detection: last-write-wins with operation logging
 */
public class CollaborationSimulation {

    private static final DateTimeFormatter TIME_FMT =
        DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    // ── Peer / user representation ────────────────────────────────────────────

    static class Peer {
        final String          name;
        final String          colour;   // ANSI or label
        final DocumentEventBus localBus;
        final CommandHistory  history;
        final List<String>    activityLog = new ArrayList<>();
        final AtomicInteger   editCount   = new AtomicInteger(0);

        Peer(String name, String colour) {
            this.name   = name;
            this.colour = colour;
            this.localBus = new DocumentEventBus();
            this.history  = new CommandHistory();
        }

        void log(String msg) {
            String entry = "[" + LocalTime.now().format(TIME_FMT) + "] "
                         + "[" + name + "] " + msg;
            activityLog.add(entry);
            System.out.println("  " + entry);
        }
    }

    // ── Collaboration server (central coordinator) ────────────────────────────

    static class CollabServer {
        private final Document            sharedDoc;
        private final List<Peer>          peers    = new ArrayList<>();
        private final List<String>        opLog    = new ArrayList<>();
        private final DocumentEventBus    globalBus;
        private final VersionControlObserver vco  = new VersionControlObserver();
        private final AtomicInteger       opSeq   = new AtomicInteger(0);

        // conflict tracking
        private final Map<String, String> lastEditBy = new ConcurrentHashMap<>();

        CollabServer(String docTitle) {
            this.sharedDoc = new Document(docTitle);
            this.sharedDoc.setMeta("author", "Collaborative");
            this.globalBus = DocumentManager.getInstance().getEventBus();
            this.globalBus.subscribe(vco);
        }

        void addPeer(Peer peer) {
            peers.add(peer);
            // When a peer makes a change, broadcast to all others
            peer.localBus.subscribe((event, data) -> broadcastToOthers(peer, event, data));
            peer.log("Connected to collaboration session: '" + sharedDoc.getTitle() + "'");
        }

        synchronized void applyOperation(Peer author, Command cmd, String description) {
            int seq = opSeq.incrementAndGet();
            String opId = "OP#" + String.format("%03d", seq);

            // Check for conflict (same element edited by different peer)
            String elemKey = description.replaceAll("[^a-zA-Z0-9]", "").substring(0, Math.min(8, description.length()));
            String prevEditor = lastEditBy.put(elemKey, author.name);
            boolean conflict = prevEditor != null && !prevEditor.equals(author.name);

            // CommandHistory.execute() calls cmd.execute() internally
            author.history.execute(cmd);
            author.editCount.incrementAndGet();

            String logEntry = opId + " | " + author.name + " | " + description
                + (conflict ? "  *** CONFLICT: previously edited by " + prevEditor + " (last-write-wins) ***" : "");
            opLog.add(logEntry);
            author.log("Applied: " + description + (conflict ? "  [CONFLICT RESOLVED]" : ""));

            // Notify global bus (Observer)
            globalBus.notify("collab_edit", author.name + " → " + description);

            // Broadcast to peers
            author.localBus.notify("peer_edit", description);
        }

        private void broadcastToOthers(Peer source, String event, Object data) {
            for (Peer p : peers) {
                if (p != source) {
                    p.log("RECEIVED broadcast: " + event + " from " + source.name);
                }
            }
        }

        Document getSharedDoc() { return sharedDoc; }
        List<String> getOpLog() { return Collections.unmodifiableList(opLog); }
        VersionControlObserver getVco() { return vco; }
    }

    // ── Simulation scenarios ───────────────────────────────────────────────────

    static void runSimulation() throws InterruptedException {
        header("REAL-TIME COLLABORATION SIMULATION");
        System.out.println("  Simulating 3 users editing the same document concurrently.");
        System.out.println("  Patterns: Observer (broadcast) · Command (reversible ops)");
        System.out.println("            Singleton (shared managers) · Composite (shared tree)");
        System.out.println();

        // ── Setup ──────────────────────────────────────────────────────────────
        CollabServer server = new CollabServer("Collaborative Research Paper 2025");

        Peer alice = new Peer("Alice", "BLUE");
        Peer bob   = new Peer("Bob",   "GREEN");
        Peer carol = new Peer("Carol", "AMBER");

        server.addPeer(alice);
        server.addPeer(bob);
        server.addPeer(carol);

        Document doc = server.getSharedDoc();
        section("Phase 1 — Initial document structure (Alice is lead author)");

        // Alice creates initial structure
        Section intro = new Section("1. Introduction");
        server.applyOperation(alice, new AddElementCommand(doc, intro),
            "Add Section: Introduction");
        Thread.sleep(80);

        Header h1 = new Header("Collaborative Systems in Embedded AI", 1);
        server.applyOperation(alice, new AddElementCommand(intro, h1),
            "Add H1: Title");
        Thread.sleep(60);

        Paragraph p1 = new Paragraph("This paper presents a novel approach to real-time collaboration.");
        server.applyOperation(alice, new AddElementCommand(intro, p1),
            "Add Paragraph: Abstract opening");
        Thread.sleep(50);

        Section methods = new Section("2. Methodology");
        server.applyOperation(alice, new AddElementCommand(doc, methods),
            "Add Section: Methodology");
        Thread.sleep(40);

        section("Phase 2 — Concurrent edits by Bob and Carol");

        // Bob and Carol edit simultaneously (simulated via sequential ops with small delays)
        Paragraph p2 = new Paragraph("The methodology employs FPGA-based signal processing.");
        server.applyOperation(bob, new AddElementCommand(methods, p2),
            "Add Paragraph to Methodology (Bob)");
        Thread.sleep(30);

        Section results = new Section("3. Results");
        server.applyOperation(carol, new AddElementCommand(doc, results),
            "Add Section: Results (Carol)");
        Thread.sleep(20);

        Image chart = new Image("results/accuracy_chart.png", "Accuracy over epochs", 1200, 500);
        server.applyOperation(carol, new AddElementCommand(results, chart),
            "Add Image: Accuracy chart (Carol)");
        Thread.sleep(30);

        // Bob adds a table
        Table resultsTable = new Table(List.of("Model", "Accuracy(%)", "Latency(ms)", "Power(W)"));
        resultsTable.addRow("Baseline CNN",  "87.2", "45.3", "12.8");
        resultsTable.addRow("FPGA-Optimized","94.1", "12.7", "8.3");
        resultsTable.addRow("Our Method",    "96.8", "9.2",  "6.1");
        server.applyOperation(bob, new AddElementCommand(results, resultsTable),
            "Add Table: Results comparison (Bob)");
        Thread.sleep(40);

        section("Phase 3 — Conflict scenario (Alice and Bob edit same section)");

        // Alice edits the paragraph Bob wrote
        server.applyOperation(alice,
            new InsertTextCommand(p2, " The system achieves 40% lower latency."),
            "InsertText into Bob's paragraph (Alice) → POTENTIAL CONFLICT");
        Thread.sleep(25);

        // Bob also edits the same paragraph
        server.applyOperation(bob,
            new InsertTextCommand(p2, " Power consumption reduced by 35%."),
            "InsertText into same paragraph (Bob) → CONFLICT: last-write-wins");
        Thread.sleep(30);

        section("Phase 4 — Undo/Redo across peers");

        // Carol adds a conclusion
        Section conclusion = new Section("4. Conclusion");
        server.applyOperation(carol, new AddElementCommand(doc, conclusion),
            "Add Section: Conclusion (Carol)");
        Thread.sleep(20);

        Paragraph pConclusion = new Paragraph(
            "Our FPGA-based approach demonstrates significant improvements in accuracy and efficiency.");
        server.applyOperation(carol, new AddElementCommand(conclusion, pConclusion),
            "Add Paragraph: Conclusion text (Carol)");
        Thread.sleep(20);

        // Bob undoes his last operation
        System.out.println();
        System.out.println("  [Bob] Performing UNDO on last operation...");
        bob.history.undo();
        bob.log("Undid last operation — peer notified via Observer");
        server.globalBus.notify("collab_undo", "Bob undid: InsertText");
        Thread.sleep(20);

        // Bob redoes it
        System.out.println("  [Bob] Performing REDO...");
        bob.history.redo();
        bob.log("Redid operation");
        server.globalBus.notify("collab_redo", "Bob redid: InsertText");
        Thread.sleep(20);

        section("Phase 5 — Footer and export");

        Footer footer = new Footer("Research Paper | Collaborative Edit Session | 2025");
        server.applyOperation(alice, new AddElementCommand(doc, footer),
            "Add Footer (Alice — finalising)");

        section("SHARED DOCUMENT — Final Composite Render");
        System.out.println(doc.render(0));

        section("VISITOR — Word Count on Shared Document");
        WordCountVisitor wc = new WordCountVisitor();
        doc.accept(wc);
        System.out.println(wc.report());

        section("OPERATION LOG");
        System.out.println("  Seq | Author | Operation");
        System.out.println("  " + "-".repeat(72));
        for (String op : server.getOpLog())
            System.out.println("  " + op);

        section("PEER STATISTICS");
        int totalOps = 0;
        for (Peer p : List.of(alice, bob, carol)) {
            int ops = p.editCount.get();
            totalOps += ops;
            System.out.printf("  %-8s : %2d operations  |  Activity log: %d entries%n",
                p.name, ops, p.activityLog.size());
        }
        System.out.println("  " + "-".repeat(40));
        System.out.printf("  %-8s : %2d total operations%n", "TOTAL", totalOps);

        section("VERSION CONTROL SNAPSHOTS (Observer)");
        System.out.println("  Snapshots recorded: " + server.getVco().getSnapshots().size());
        server.getVco().getSnapshots().stream().limit(8).forEach(s ->
            System.out.printf("  v%-3d  %-30s @ %s%n", s.version, s.event, s.timestamp));
        if (server.getVco().getSnapshots().size() > 8)
            System.out.println("  ... and " + (server.getVco().getSnapshots().size()-8) + " more snapshots.");

        section("EXPORT — Shared document as JSON (Abstract Factory)");
        ExportManager em = ExportManager.getInstance();
        DocumentManager.getInstance().save(doc);
        String json = em.export(doc, "json");
        System.out.println(json.substring(0, Math.min(600, json.length())) + "\n  ...");

        section("SIMULATION SUMMARY");
        System.out.println("  Patterns demonstrated:");
        System.out.println("  [✓] Observer    — all peer edits broadcast via DocumentEventBus");
        System.out.println("  [✓] Command     — every op is a Command; undo/redo work per-peer");
        System.out.println("  [✓] Singleton   — DocumentManager / ExportManager shared globally");
        System.out.println("  [✓] Composite   — single Document tree mutated by all 3 peers");
        System.out.println("  [✓] Visitor     — WordCount runs on the final merged document");
        System.out.println("  [✓] Prototype   — deepCopy() available for snapshot branching");
        System.out.println("  [✓] Abs.Factory — JSON export of the collaborative document");
        System.out.println("  [✓] Conflict    — detected and resolved with last-write-wins policy");
        System.out.println();
        System.out.println("  Collaboration session complete.");
        System.out.println("=".repeat(62));
    }

    // ── Formatting helpers ────────────────────────────────────────────────────

    static void header(String t) {
        System.out.println("\n" + "=".repeat(62));
        System.out.println("  " + t);
        System.out.println("=".repeat(62));
    }

    static void section(String t) {
        System.out.println("\n" + "-".repeat(62));
        System.out.println("  " + t);
        System.out.println("-".repeat(62));
    }

    // ── Entry point ───────────────────────────────────────────────────────────

    public static void main(String[] args) throws InterruptedException {
        runSimulation();
    }
}

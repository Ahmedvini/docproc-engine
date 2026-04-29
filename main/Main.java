import java.util.List;

public class Main {

    // ── helpers ──────────────────────────────────────────────────────────────
    static void bar(String t)  { System.out.println("\n" + "#".repeat(62) + "\n  " + t + "\n" + "#".repeat(62)); }
    static void sec(String t)  { System.out.println("\n" + "-".repeat(62) + "\n  " + t + "\n" + "-".repeat(62)); }

    static Paragraph findFirstParagraph(DocumentElement el) {
        if (el instanceof Paragraph) return (Paragraph) el;
        if (el instanceof CompositeElement)
            for (DocumentElement c : ((CompositeElement) el).getChildren()) {
                Paragraph p = findFirstParagraph(c); if (p != null) return p;
            }
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {

        bar("PLUGIN-BASED EXTENSIBLE DOCUMENT PROCESSING SYSTEM");

        // ── (1) SINGLETON ──────────────────────────────────────────────────────
        sec("(1) SINGLETON");
        DocumentManager dm = DocumentManager.getInstance();
        PluginManager   pm = PluginManager.getInstance();
        ExportManager   em = ExportManager.getInstance();
        System.out.println("  DocumentManager singleton: " + (dm == DocumentManager.getInstance()));
        System.out.println("  PluginManager   singleton: " + (pm == PluginManager.getInstance()));
        System.out.println("  ExportManager   singleton: " + (em == ExportManager.getInstance()));
        System.out.println("  Export formats : " + em.formats());

        // ── (10) OBSERVER ──────────────────────────────────────────────────────
        sec("(10) OBSERVER - subscribe to document events");
        DocumentEventBus       bus   = dm.getEventBus();
        AutoSaveObserver       as    = new AutoSaveObserver();
        LivePreviewObserver    lp    = new LivePreviewObserver();
        VersionControlObserver vc    = new VersionControlObserver();
        bus.subscribe(as); bus.subscribe(lp); bus.subscribe(vc);
        System.out.println("  Subscribed: AutoSave | LivePreview | VersionControl");

        // ── (2) FLYWEIGHT ──────────────────────────────────────────────────────
        sec("(2) FLYWEIGHT - shared font/color objects");
        FontStyle  fArial12     = StyleFactory.getFont("Arial", 12);
        FontStyle  fArial12Dup  = StyleFactory.getFont("Arial", 12);           // reuse
        FontStyle  fArial14Bold = StyleFactory.getFont("Arial", 14, true, false);
        FontStyle  fTimesItal   = StyleFactory.getFont("Times New Roman", 12, false, true);
        FontStyle  fArial12Dup2 = StyleFactory.getFont("Arial", 12);           // reuse again
        ColorStyle cBlack = StyleFactory.getColor(0, 0, 0);
        ColorStyle cBlue  = StyleFactory.getColor(30, 100, 200);
        ColorStyle cRed   = StyleFactory.getColor(200, 30, 30);
        System.out.println("\n  Flyweight pool       : " + StyleFactory.poolInfo());
        System.out.println("  fArial12 == fArial12Dup  : " + (fArial12 == fArial12Dup)  + "  (same object - reused)");
        System.out.println("  fArial12 == fArial12Dup2 : " + (fArial12 == fArial12Dup2) + "  (same object - reused)");

        // ── (3) FACTORY METHOD ─────────────────────────────────────────────────
        sec("(3) FACTORY METHOD - element creation");
        StandardElementFactory stdFac    = new StandardElementFactory();
        StyledElementFactory   stylFac   = new StyledElementFactory(fArial12, cBlue);
        Paragraph plainP  = stdFac.create_paragraph("Plain text paragraph.");
        Paragraph styledP = stylFac.create_paragraph("Styled paragraph with font + color.");
        System.out.println("  Plain  font  : " + plainP.getFontStyle());
        System.out.println("  Styled font  : " + styledP.getFontStyle());
        System.out.println("  Styled color : " + styledP.getColorStyle());

        // ── (7) COMPOSITE + (5) BUILDER + (8) COMMAND ──────────────────────────
        sec("(7) COMPOSITE + (5) BUILDER + (8) COMMAND - document assembly");
        DocumentBuilder builder = dm.newBuilder();
        Document doc = builder
            .newDocument("Annual Technology Report 2025", "Ahmed Elsheikh")
            .addHeader("Annual Technology Report 2025", 1)

            .addSection("Executive Summary")
            .addParagraph("This report summarises technology developments across 2025.")
            .addParagraph("Key areas include AI integration, embedded systems, and IoT.")
            .addImage("assets/overview.png", "Performance Overview", 1200, 400)

            .addSection("Financial Overview")
            .addTable(
                List.of("Quarter", "Revenue ($M)", "Growth (%)"),
                List.of(
                    new String[]{"Q1", "12.4", "+8.2"},
                    new String[]{"Q2", "14.1", "+13.7"},
                    new String[]{"Q3", "15.8", "+12.1"},
                    new String[]{"Q4", "18.2", "+15.2"}
                )
            )

            .addSection("Technical Highlights")
            .addParagraph("Neural signal processing achieved 94% accuracy on the BCI dataset.")
            .addParagraph("Zynq-7000 FPGA deployment reduced inference latency by 40%.")
            .addImage("assets/fpga.png", "FPGA Block Diagram", 800, 500)

            .addSection("Conclusion")
            .addParagraph("The year demonstrated outstanding cross-disciplinary performance.")

            .addFooter("(c) 2025 Tech Corp - Confidential | Page {PAGE}")
            .build();

        System.out.println("\n  Document built  : '" + doc.getTitle() + "'");
        List<String> log = builder.commandLog();
        System.out.println("  Command log[0-3]: " + log.subList(0, Math.min(4, log.size())) + " ...");

        // ── COMPOSITE render ────────────────────────────────────────────────────
        sec("(7) COMPOSITE - full tree render");
        System.out.println(doc.render(0));

        // ── (6) PROTOTYPE ──────────────────────────────────────────────────────
        sec("(6) PROTOTYPE - deep copy an element independently");
        Table origTable = null;
        outer:
        for (DocumentElement c : doc.getChildren()) {
            if (c instanceof Section)
                for (DocumentElement e : ((Section) c).getChildren())
                    if (e instanceof Table) { origTable = (Table) e; break outer; }
        }
        if (origTable != null) {
            Table cloned = origTable.deepCopy();
            cloned.addRow("Q5 (proj.)", "20.0", "+9.9");
            System.out.println("  Original ID : " + origTable.getElementId() + "  rows=" + origTable.getRows().size());
            System.out.println("  Cloned   ID : " + cloned.getElementId()   + "  rows=" + cloned.getRows().size());
            System.out.println("  Original unchanged: " + (origTable.getRows().size() == 4) + "  <- deep copy confirmed");
        }

        // ── (8) COMMAND - undo / redo ───────────────────────────────────────────
        sec("(8) COMMAND - undo / redo");
        Paragraph para = findFirstParagraph(doc);
        if (para != null) {
            CommandHistory history = new CommandHistory();
            System.out.println("\n  Initial text : '" + para.getText() + "'");
            history.execute(new InsertTextCommand(para, " [AMENDED]"));
            System.out.println("  After insert : '" + para.getText() + "'");
            history.execute(new FormatChangeCommand(para, fArial14Bold, cRed));
            System.out.println("  After format : font=" + para.getFontStyle() + ", color=" + para.getColorStyle());
            history.undo();
            System.out.println("  Undo format  : font=" + para.getFontStyle());
            history.undo();
            System.out.println("  Undo insert  : '" + para.getText() + "'");
            history.redo();
            System.out.println("  Redo insert  : '" + para.getText() + "'");
            history.execute(new DeleteTextCommand(para, 0, 4));
            System.out.println("  Delete [0:4] : '" + para.getText() + "'");
            history.undo();
            System.out.println("  Undo delete  : '" + para.getText() + "'");
        }

        // ── (9) STRATEGY ───────────────────────────────────────────────────────
        sec("(9) STRATEGY - swappable formatting algorithms");
        TextFormatter fmt  = new TextFormatter();
        String        text = "hello world this is a TEST document";
        Object[][] strategies = {
            {"Plain",        new PlainTextStrategy()},
            {"UpperCase",    new UpperCaseStrategy()},
            {"TitleCase",    new TitleCaseStrategy()},
            {"SentenceCase", new SentenceCaseStrategy()},
            {"MdStrip",      new MarkdownStripStrategy()}
        };
        for (Object[] pair : strategies) {
            fmt.setStrategy((FormattingStrategy) pair[1]);
            System.out.printf("  %-13s : '%s'%n", pair[0], fmt.format(text));
        }
        fmt.setStrategy(new MarkdownStripStrategy());
        System.out.printf("  %-13s : '%s'%n", "MdStrip2", fmt.format("**Hello** _World_ ~~strikethrough~~"));

        // ── (4) ABSTRACT FACTORY - multi-format export ──────────────────────────
        sec("(4) ABSTRACT FACTORY - multi-format export");
        String docId = dm.save(doc);
        System.out.println("  Saved document ID: " + docId + "\n");
        for (String format : List.of("html", "pdf", "docx", "json")) {
            pm.fire("before_export", doc);
            String output  = em.export(doc, format);
            String snippet = output.substring(0, Math.min(110, output.length())).replace("\n", " ").trim();
            System.out.println("  " + format.toUpperCase() + " preview: " + snippet + " ...\n");
        }

        // ── (11) VISITOR ────────────────────────────────────────────────────────
        sec("(11) VISITOR - analytics without modifying element classes");
        WordCountVisitor wc = new WordCountVisitor();
        doc.accept(wc);
        System.out.println(wc.report());

        DocumentBuilder sb2 = dm.newBuilder();
        Document spellDoc = sb2
            .newDocument("Spell Test Draft")
            .addParagraph("The goverment policy on accomodation will take effect untill December.")
            .addParagraph("We beleive the recieve order has occured seperately.")
            .build();
        SpellCheckVisitor sc = new SpellCheckVisitor();
        spellDoc.accept(sc);
        System.out.println("\n" + sc.report());

        // ── PLUGIN SYSTEM ──────────────────────────────────────────────────────
        sec("PLUGIN SYSTEM - runtime extensibility");
        pm.load(new SpellCheckPlugin());
        pm.load(new WordCountPlugin());
        pm.load(new AutoNumberPlugin());
        System.out.println("\n  Active plugins: " + pm.listPlugins());
        pm.fire("before_export", doc);
        System.out.println("\n" + ((SpellCheckPlugin)  pm.get("SpellChecker")).check(spellDoc));
        System.out.println("\n" + ((WordCountPlugin)   pm.get("WordCounter")).count(doc));
        ((AutoNumberPlugin) pm.get("AutoNumberer")).numberHeaders(doc);

        // ── EXPORT LOG + VERSION HISTORY ────────────────────────────────────────
        sec("EXPORT LOG & VERSION HISTORY");
        System.out.println("  Export log:");
        for (String entry : em.getLog()) System.out.println("    " + entry);
        System.out.println("\n  Version snapshots: " + vc.getSnapshots().size());
        vc.getSnapshots().stream().limit(6).forEach(s ->
            System.out.printf("    v%-3d %-28s @ %s%n", s.version, s.event, s.timestamp));

        // ── SUMMARY ────────────────────────────────────────────────────────────
        sec("SYSTEM SUMMARY");
        System.out.println("  Documents in registry : " + dm.count());
        System.out.println("  Plugins active        : " + pm.listPlugins().size());
        System.out.println("  Flyweight pool        : " + StyleFactory.poolInfo());
        System.out.println("  Export formats        : " + em.formats());
        System.out.println("  Version snapshots     : " + vc.getSnapshots().size());
        System.out.println("\n  All 11 design patterns demonstrated successfully.");
        System.out.println("#".repeat(62));
    }
}

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * SWING GUI — Plugin-Based Extensible Document Processing System
 *
 * Features demonstrated visually:
 *   ① Singleton  — managers accessed via getInstance()
 *   ② Flyweight  — style pool shown in status bar
 *   ③ Factory    — element type chosen from toolbar combo
 *   ④ Abs.Factory— export format chosen from menu
 *   ⑤ Builder    — document assembled step by step
 *   ⑥ Prototype  — Clone button on selected element
 *   ⑦ Composite  — JTree mirrors Document → Section → leaf
 *   ⑧ Command    — Undo / Redo buttons wired to CommandHistory
 *   ⑨ Strategy   — Format combo changes TextFormatter strategy
 *   ⑩ Observer   — Event log panel shows live notifications
 *   ⑪ Visitor    — Word Count / Spell Check buttons
 */
public class DocumentEditorGUI extends JFrame {

    // ── Colours (matching documentation palette) ──────────────────────────────
    private static final Color C_DARK   = new Color(0x1a, 0x1a, 0x2e);
    private static final Color C_BLUE   = new Color(0x0f, 0x34, 0x60);
    private static final Color C_TEAL   = new Color(0x1a, 0x7a, 0x6e);
    private static final Color C_AMBER  = new Color(0xe8, 0xa0, 0x20);
    private static final Color C_LIGHT  = new Color(0xf0, 0xf4, 0xf8);
    private static final Color C_WHITE  = Color.WHITE;
    private static final Color C_LGRAY  = new Color(0xe5, 0xe7, 0xeb);
    private static final Color C_GREEN  = new Color(0x1a, 0x7a, 0x3e);
    private static final Color C_RED    = new Color(0xc0, 0x39, 0x2b);

    // ── Singletons ────────────────────────────────────────────────────────────
    private final DocumentManager dm = DocumentManager.getInstance();
    private final ExportManager   em = ExportManager.getInstance();
    private final PluginManager   pm = PluginManager.getInstance();

    // ── State ─────────────────────────────────────────────────────────────────
    private Document        currentDoc;
    private Section         currentSection;
    private CommandHistory  history = new CommandHistory() {
        @Override public Object execute(Command cmd) {
            Object r = super.execute(cmd); refreshAll(); return r;
        }
        @Override public boolean undo() {
            boolean r = super.undo(); refreshAll(); return r;
        }
        @Override public boolean redo() {
            boolean r = super.redo(); refreshAll(); return r;
        }
    };

    // ── UI components ─────────────────────────────────────────────────────────
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private JTree docTree;
    private JTextPane previewPane;
    private JTextArea eventLog;
    private JLabel statusBar;
    private JComboBox<String> formatStrategyCombo;
    private JButton btnUndo, btnRedo, btnClone;
    private JLabel flyweightLabel;

    // ── Event bus (Observer) ──────────────────────────────────────────────────
    private final DocumentEventBus bus;

    public DocumentEditorGUI() {
        super("Document Processing System  —  Design Patterns Demo");

        // Set up bus + observers
        bus = dm.getEventBus();
        bus.subscribe((event, data) -> SwingUtilities.invokeLater(() -> {
            String msg = "[" + java.time.LocalTime.now().toString().substring(0, 8)
                       + "]  " + event + (data != null ? "  →  " + data : "");
            eventLog.append(msg + "\n");
            eventLog.setCaretPosition(eventLog.getDocument().getLength());
        }));

        // Load plugins
        pm.load(new SpellCheckPlugin());
        pm.load(new WordCountPlugin());
        pm.load(new AutoNumberPlugin());

        buildUI();
        newDocument();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 800);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  UI CONSTRUCTION
    // ─────────────────────────────────────────────────────────────────────────

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

        // Top menu + toolbar
        setJMenuBar(buildMenuBar());
        add(buildToolBar(), BorderLayout.NORTH);

        // Centre split: tree | preview+log
        JSplitPane centre = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildTreePanel(), buildRightPanel());
        centre.setDividerLocation(280);
        centre.setDividerSize(4);
        add(centre, BorderLayout.CENTER);

        // Status bar
        statusBar = new JLabel();
        statusBar.setBorder(new EmptyBorder(3, 10, 3, 10));
        statusBar.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statusBar.setBackground(C_DARK); statusBar.setForeground(C_WHITE);
        statusBar.setOpaque(true);
        add(statusBar, BorderLayout.SOUTH);
    }

    // ── Menu bar ──────────────────────────────────────────────────────────────
    private JMenuBar buildMenuBar() {
        JMenuBar mb = new JMenuBar();
        mb.setBackground(C_DARK);

        // File
        JMenu mFile = darkMenu("File");
        mFile.add(menuItem("New Document",     "Ctrl+N", e -> newDocument()));
        mFile.addSeparator();
        for (String fmt : List.of("HTML","PDF","DOCX","JSON")) {
            String f = fmt;
            mFile.add(menuItem("Export as " + fmt, null, e -> exportDoc(f.toLowerCase())));
        }
        mFile.addSeparator();
        mFile.add(menuItem("Exit", "Ctrl+Q", e -> System.exit(0)));

        // Edit
        JMenu mEdit = darkMenu("Edit");
        JMenuItem miUndo = menuItem("Undo", "Ctrl+Z", e -> history.undo());
        JMenuItem miRedo = menuItem("Redo", "Ctrl+Y", e -> history.redo());
        mEdit.add(miUndo); mEdit.add(miRedo);
        mEdit.addSeparator();
        mEdit.add(menuItem("Clone Selected Element", null, e -> cloneSelected()));

        // Insert
        JMenu mInsert = darkMenu("Insert");
        mInsert.add(menuItem("Section",   null, e -> addSection()));
        mInsert.add(menuItem("Header H1", null, e -> addElement("Header H1")));
        mInsert.add(menuItem("Header H2", null, e -> addElement("Header H2")));
        mInsert.add(menuItem("Paragraph", null, e -> addElement("Paragraph")));
        mInsert.add(menuItem("Image",     null, e -> addElement("Image")));
        mInsert.add(menuItem("Table",     null, e -> addElement("Table")));
        mInsert.add(menuItem("Footer",    null, e -> addElement("Footer")));

        // Plugins / Tools
        JMenu mTools = darkMenu("Tools");
        mTools.add(menuItem("Word Count",   null, e -> wordCount()));
        mTools.add(menuItem("Spell Check",  null, e -> spellCheck()));
        mTools.add(menuItem("Auto-Number Headers", null, e -> autoNumber()));
        mTools.addSeparator();
        mTools.add(menuItem("Show Flyweight Pool", null, e -> showFlyweightPool()));
        mTools.add(menuItem("Show Command Log", null, e -> showCommandLog()));

        mb.add(mFile); mb.add(mEdit); mb.add(mInsert); mb.add(mTools);
        return mb;
    }

    private JMenu darkMenu(String name) {
        JMenu m = new JMenu(name);
        m.setForeground(C_WHITE); m.setFont(new Font("Helvetica", Font.PLAIN, 13));
        return m;
    }

    private JMenuItem menuItem(String name, String accel, ActionListener al) {
        JMenuItem mi = new JMenuItem(name);
        if (accel != null) mi.setAccelerator(KeyStroke.getKeyStroke(accel));
        mi.addActionListener(al);
        return mi;
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private JPanel buildToolBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        bar.setBackground(C_BLUE); bar.setBorder(new EmptyBorder(2, 4, 2, 4));

        bar.add(tBtn("+ Section",   C_TEAL,  e -> addSection()));
        bar.add(tBtn("+ Paragraph", C_GREEN, e -> addElement("Paragraph")));
        bar.add(tBtn("+ Header",    C_GREEN, e -> addElement("Header H1")));
        bar.add(tBtn("+ Image",     C_GREEN, e -> addElement("Image")));
        bar.add(tBtn("+ Table",     C_GREEN, e -> addElement("Table")));
        bar.add(tBtn("+ Footer",    C_GREEN, e -> addElement("Footer")));

        bar.add(Box.createHorizontalStrut(16));

        btnUndo = tBtn("↩ Undo", C_AMBER, e -> history.undo());
        btnRedo = tBtn("↪ Redo", C_AMBER, e -> history.redo());
        btnClone = tBtn("⧉ Clone", C_AMBER, e -> cloneSelected());
        bar.add(btnUndo); bar.add(btnRedo); bar.add(btnClone);

        bar.add(Box.createHorizontalStrut(16));

        // Strategy selector
        bar.add(label("Format:", C_WHITE));
        formatStrategyCombo = new JComboBox<>(new String[]{
            "Plain","UpperCase","TitleCase","SentenceCase","MarkdownStrip"
        });
        formatStrategyCombo.setPreferredSize(new Dimension(130, 26));
        formatStrategyCombo.setToolTipText("Strategy Pattern — swap formatting algorithm");
        bar.add(formatStrategyCombo);

        bar.add(Box.createHorizontalStrut(16));
        bar.add(tBtn("✓ Word Count",  C_TEAL, e -> wordCount()));
        bar.add(tBtn("✗ Spell Check", C_TEAL, e -> spellCheck()));

        bar.add(Box.createHorizontalStrut(16));
        flyweightLabel = label("Flyweight pool: ...", new Color(0xf0,0xd0,0x80));
        bar.add(flyweightLabel);

        return bar;
    }

    private JButton tBtn(String text, Color bg, ActionListener al) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(C_WHITE);
        b.setFont(new Font("Helvetica", Font.BOLD, 11));
        b.setBorder(new EmptyBorder(4, 10, 4, 10));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(al);
        return b;
    }

    private JLabel label(String text, Color fg) {
        JLabel l = new JLabel(text);
        l.setForeground(fg); l.setFont(new Font("Helvetica", Font.PLAIN, 11));
        return l;
    }

    // ── Tree panel ────────────────────────────────────────────────────────────
    private JPanel buildTreePanel() {
        rootNode = new DefaultMutableTreeNode("Document");
        treeModel = new DefaultTreeModel(rootNode);
        docTree   = new JTree(treeModel);
        docTree.setBackground(C_LIGHT);
        docTree.setFont(new Font("Monospaced", Font.PLAIN, 11));
        docTree.setRowHeight(22);
        docTree.setCellRenderer(new DocumentTreeRenderer());
        docTree.addTreeSelectionListener(e -> refreshSelected());

        JScrollPane sp = new JScrollPane(docTree);
        sp.setBorder(BorderFactory.createEmptyBorder());

        JLabel header = new JLabel("  Document Structure  (Composite Tree)", SwingConstants.LEFT);
        header.setBackground(C_DARK); header.setForeground(C_WHITE); header.setOpaque(true);
        header.setFont(new Font("Helvetica", Font.BOLD, 12));
        header.setBorder(new EmptyBorder(6, 8, 6, 8));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(header, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    // ── Right panel (preview + event log) ────────────────────────────────────
    private JSplitPane buildRightPanel() {
        // Preview
        previewPane = new JTextPane();
        previewPane.setEditable(false);
        previewPane.setBackground(C_WHITE);
        previewPane.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane previewScroll = new JScrollPane(previewPane);

        JLabel previewHeader = new JLabel("  Document Preview  (Composite render)", SwingConstants.LEFT);
        previewHeader.setBackground(C_BLUE); previewHeader.setForeground(C_WHITE); previewHeader.setOpaque(true);
        previewHeader.setFont(new Font("Helvetica", Font.BOLD, 12));
        previewHeader.setBorder(new EmptyBorder(6, 8, 6, 8));

        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.add(previewHeader, BorderLayout.NORTH);
        previewPanel.add(previewScroll, BorderLayout.CENTER);

        // Event log (Observer output)
        eventLog = new JTextArea();
        eventLog.setEditable(false);
        eventLog.setBackground(new Color(0x0d, 0x1b, 0x2a));
        eventLog.setForeground(new Color(0x80, 0xff, 0xb0));
        eventLog.setFont(new Font("Monospaced", Font.PLAIN, 10));
        JScrollPane logScroll = new JScrollPane(eventLog);

        JLabel logHeader = new JLabel("  Observer Event Log  (live notifications)", SwingConstants.LEFT);
        logHeader.setBackground(C_TEAL); logHeader.setForeground(C_WHITE); logHeader.setOpaque(true);
        logHeader.setFont(new Font("Helvetica", Font.BOLD, 12));
        logHeader.setBorder(new EmptyBorder(6, 8, 6, 8));

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.add(logHeader, BorderLayout.NORTH);
        logPanel.add(logScroll, BorderLayout.CENTER);
        logPanel.setPreferredSize(new Dimension(0, 180));

        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, previewPanel, logPanel);
        sp.setResizeWeight(0.65);
        sp.setDividerSize(4);
        return sp;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  ACTIONS
    // ─────────────────────────────────────────────────────────────────────────

    private void newDocument() {
        String title = JOptionPane.showInputDialog(this,
            "Document title:", "New Document", JOptionPane.PLAIN_MESSAGE);
        if (title == null || title.isBlank()) title = "Untitled Document";
        currentDoc     = new Document(title);
        currentSection = null;
        history        = new CommandHistory() {
            @Override public Object execute(Command cmd) { Object r=super.execute(cmd); refreshAll(); return r; }
            @Override public boolean undo() { boolean r=super.undo(); refreshAll(); return r; }
            @Override public boolean redo() { boolean r=super.redo(); refreshAll(); return r; }
        };
        bus.notify("document_created", title);
        refreshAll();
    }

    private void addSection() {
        if (currentDoc == null) return;
        String title = JOptionPane.showInputDialog(this, "Section title:", "Add Section", JOptionPane.PLAIN_MESSAGE);
        if (title == null || title.isBlank()) return;
        Section sec = new Section(title);
        history.execute(new AddElementCommand(currentDoc, sec));
        currentSection = sec;
        bus.notify("section_added", title);
    }

    private void addElement(String type) {
        if (currentDoc == null) return;
        CompositeElement target = currentSection != null ? currentSection : currentDoc;

        FormattingStrategy strategy = getStrategy();
        TextFormatter fmt = new TextFormatter(strategy);

        DocumentElement el = null;
        switch (type) {
            case "Paragraph": {
                String text = JOptionPane.showInputDialog(this, "Paragraph text:", "Add Paragraph", JOptionPane.PLAIN_MESSAGE);
                if (text == null || text.isBlank()) return;
                el = new Paragraph(fmt.format(text));
                // Apply Flyweight style
                FontStyle  font  = StyleFactory.getFont("Arial", 12);
                ColorStyle color = StyleFactory.getColor(26, 26, 46);
                el.setStyle(font, color);
                bus.notify("text_added", text.substring(0, Math.min(30, text.length())));
                break;
            }
            case "Header H1": case "Header H2": {
                String text = JOptionPane.showInputDialog(this, "Header text:", "Add Header", JOptionPane.PLAIN_MESSAGE);
                if (text == null || text.isBlank()) return;
                int level = type.equals("Header H1") ? 1 : 2;
                el = new Header(fmt.format(text), level);
                FontStyle font = StyleFactory.getFont("Arial", level == 1 ? 18 : 14, true, false);
                el.setStyle(font, StyleFactory.getColor(15, 52, 96));
                break;
            }
            case "Image": {
                String src = JOptionPane.showInputDialog(this, "Image source (path/URL):", "Add Image", JOptionPane.PLAIN_MESSAGE);
                if (src == null || src.isBlank()) return;
                String alt = JOptionPane.showInputDialog(this, "Alt text:", "Add Image", JOptionPane.PLAIN_MESSAGE);
                el = new Image(src, alt != null ? alt : "", 800, 600);
                break;
            }
            case "Table": {
                String hdrs = JOptionPane.showInputDialog(this, "Column headers (comma-separated):", "Add Table", JOptionPane.PLAIN_MESSAGE);
                if (hdrs == null || hdrs.isBlank()) return;
                List<String> headers = List.of(hdrs.split(","));
                Table tbl = new Table(new java.util.ArrayList<>(headers));
                String moreRows = JOptionPane.showInputDialog(this, "Add a data row? (comma-separated values, or blank to skip):", "Add Table Row", JOptionPane.PLAIN_MESSAGE);
                if (moreRows != null && !moreRows.isBlank()) tbl.addRow(moreRows.split(","));
                el = tbl;
                break;
            }
            case "Footer": {
                String text = JOptionPane.showInputDialog(this, "Footer text:", "Add Footer", JOptionPane.PLAIN_MESSAGE);
                if (text == null || text.isBlank()) return;
                el = new Footer(text);
                break;
            }
        }
        if (el != null) history.execute(new AddElementCommand(target, el));
    }

    private void cloneSelected() {
        // PROTOTYPE
        TreePath path = docTree.getSelectionPath();
        if (path == null) { showInfo("Select an element in the tree first."); return; }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object uo = node.getUserObject();
        if (!(uo instanceof DocumentElement)) { showInfo("Select a document element to clone."); return; }

        DocumentElement original = (DocumentElement) uo;
        DocumentElement clone    = original.deepCopy();

        CompositeElement parent  = currentSection != null ? currentSection : currentDoc;
        history.execute(new AddElementCommand(parent, clone));
        bus.notify("element_cloned", original.getClass().getSimpleName());
        JOptionPane.showMessageDialog(this,
            "Cloned!\nOriginal ID: " + original.getElementId() +
            "\nClone    ID: " + clone.getElementId(),
            "Prototype — Deep Copy", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportDoc(String format) {
        if (currentDoc == null) return;
        dm.save(currentDoc);
        try {
            String result = em.export(currentDoc, format);
            JTextArea ta = new JTextArea(result);
            ta.setFont(new Font("Monospaced", Font.PLAIN, 11));
            ta.setEditable(false);
            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(700, 460));
            JOptionPane.showMessageDialog(this, sp,
                "Export — " + format.toUpperCase() + "  (Abstract Factory)", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void wordCount() {
        if (currentDoc == null) return;
        WordCountPlugin wcp = (WordCountPlugin) pm.get("WordCounter");
        showInfo(wcp.count(currentDoc));
    }

    private void spellCheck() {
        if (currentDoc == null) return;
        SpellCheckPlugin scp = (SpellCheckPlugin) pm.get("SpellChecker");
        showInfo(scp.check(currentDoc));
    }

    private void autoNumber() {
        if (currentDoc == null) return;
        ((AutoNumberPlugin) pm.get("AutoNumberer")).numberHeaders(currentDoc);
        bus.notify("headers_numbered", currentDoc.getTitle());
        refreshAll();
    }

    private void showFlyweightPool() {
        showInfo("Flyweight Style Pool\n\n" + StyleFactory.poolInfo() +
            "\n\nThe same FontStyle / ColorStyle object is reused across\n" +
            "all elements that share that style — saving memory.\n\n" +
            "Identity check: StyleFactory.getFont(\"Arial\",12) ==\n" +
            "                StyleFactory.getFont(\"Arial\",12)  →  true");
    }

    private void showCommandLog() {
        if (history.getSummary().isEmpty()) { showInfo("No commands executed yet."); return; }
        StringBuilder sb = new StringBuilder("Command History (most recent first):\n\n");
        for (String s : history.getSummary()) sb.append("  • ").append(s).append("\n");
        showInfo(sb.toString());
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  REFRESH
    // ─────────────────────────────────────────────────────────────────────────

    private void refreshAll() {
        refreshTree();
        refreshPreview();
        refreshStatus();
        flyweightLabel.setText("Flyweight pool: " + StyleFactory.poolInfo());
    }

    private void refreshTree() {
        rootNode.removeAllChildren();
        if (currentDoc == null) { treeModel.reload(); return; }
        rootNode.setUserObject(currentDoc);
        buildTreeNode(rootNode, currentDoc);
        treeModel.reload();
        expandAll();
    }

    private void buildTreeNode(DefaultMutableTreeNode parent, CompositeElement composite) {
        for (DocumentElement child : composite.getChildren()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(child);
            parent.add(node);
            if (child instanceof CompositeElement)
                buildTreeNode(node, (CompositeElement) child);
        }
    }

    private void expandAll() {
        for (int i = 0; i < docTree.getRowCount(); i++) docTree.expandRow(i);
    }

    private void refreshPreview() {
        if (currentDoc == null) { previewPane.setText(""); return; }
        previewPane.setText(currentDoc.render(0));
        previewPane.setCaretPosition(0);
    }

    private void refreshStatus() {
        String sec = currentSection != null ? "  |  Section: " + currentSection.getTitle() : "";
        String doc = currentDoc     != null ? "Document: " + currentDoc.getTitle() : "No document";
        String plugins = "  |  Plugins: " + pm.listPlugins().size();
        statusBar.setText("  " + doc + sec + plugins + "  |  " + StyleFactory.poolInfo());
    }

    private void refreshSelected() {
        TreePath path = docTree.getSelectionPath();
        if (path == null) return;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object uo = node.getUserObject();
        if (uo instanceof Section) currentSection = (Section) uo;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private FormattingStrategy getStrategy() {
        switch (formatStrategyCombo.getSelectedIndex()) {
            case 1: return new UpperCaseStrategy();
            case 2: return new TitleCaseStrategy();
            case 3: return new SentenceCaseStrategy();
            case 4: return new MarkdownStripStrategy();
            default: return new PlainTextStrategy();
        }
    }

    private void showInfo(String msg) {
        JTextArea ta = new JTextArea(msg);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setEditable(false); ta.setBackground(C_LIGHT);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(520, 260));
        JOptionPane.showMessageDialog(this, sp, "Result", JOptionPane.PLAIN_MESSAGE);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  TREE CELL RENDERER
    // ─────────────────────────────────────────────────────────────────────────

    private class DocumentTreeRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            setFont(new Font("Monospaced", Font.PLAIN, 11));
            setBackground(selected ? C_BLUE : C_LIGHT);
            setForeground(selected ? C_WHITE : C_DARK);
            setOpaque(true);

            Object uo = ((DefaultMutableTreeNode) value).getUserObject();
            if (uo instanceof Document)   { setText("📄  " + ((Document)uo).getTitle());  setForeground(selected ? C_WHITE : C_BLUE); }
            else if (uo instanceof Section)    setText("📁  " + ((Section)uo).getTitle());
            else if (uo instanceof Header)     setText("H" + ((Header)uo).getLevel() + "  " + truncate(((Header)uo).getText()));
            else if (uo instanceof Paragraph)  setText("¶   " + truncate(((Paragraph)uo).getText()));
            else if (uo instanceof Image)      setText("🖼  " + truncate(((Image)uo).getAlt()));
            else if (uo instanceof Table)      setText("⊞  Table (" + ((Table)uo).getRows().size() + " rows)");
            else if (uo instanceof Footer)     setText("—  " + truncate(((Footer)uo).getText()));
            setBorder(new EmptyBorder(2, 4, 2, 4));
            return this;
        }
        private String truncate(String s) { return s != null && s.length() > 38 ? s.substring(0, 36) + "…" : s; }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  MAIN
    // ─────────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(DocumentEditorGUI::new);
    }
}

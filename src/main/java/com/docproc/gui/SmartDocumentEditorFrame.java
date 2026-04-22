package com.docproc.gui;

import com.docproc.command.InsertTextCommand;
import com.docproc.core.DocumentManager;
import com.docproc.core.ExportManager;
import com.docproc.model.Document;
import com.docproc.model.Paragraph;
import com.docproc.visitor.SpellCheckVisitor;
import com.docproc.visitor.WordCountVisitor;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SmartDocumentEditorFrame extends JFrame {
    private final DocumentManager manager = DocumentManager.getInstance();
    private final JTextArea editorArea = new JTextArea(20, 60);
    private final JTextArea previewArea = new JTextArea(20, 60);
    private final JLabel statusLabel = new JLabel("Ready");

    public SmartDocumentEditorFrame() {
        super("Smart Document Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        header.add(new JLabel("Smart Document Editor - Desktop"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        previewArea.setEditable(false);
        JScrollPane editorScrollPane = new JScrollPane(editorArea);
        JScrollPane previewScrollPane = new JScrollPane(previewArea);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorScrollPane, previewScrollPane);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton uploadButton = new JButton("Upload Text File");
        JButton applyButton = new JButton("Apply Text");
        JButton undoButton = new JButton("Undo");
        JButton redoButton = new JButton("Redo");
        JButton wordCountButton = new JButton("Word Count");
        JButton spellCheckButton = new JButton("Spell Check");
        JButton exportHtmlButton = new JButton("Export HTML");

        controls.add(uploadButton);
        controls.add(applyButton);
        controls.add(undoButton);
        controls.add(redoButton);
        controls.add(wordCountButton);
        controls.add(spellCheckButton);
        controls.add(exportHtmlButton);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(new EmptyBorder(0, 10, 10, 10));
        footer.add(controls, BorderLayout.CENTER);
        footer.add(statusLabel, BorderLayout.SOUTH);
        add(footer, BorderLayout.SOUTH);

        uploadButton.addActionListener(e -> uploadText());
        applyButton.addActionListener(e -> applyText());
        undoButton.addActionListener(e -> {
            manager.undo();
            refreshView();
            setStatus("Undo applied");
        });
        redoButton.addActionListener(e -> {
            manager.redo();
            refreshView();
            setStatus("Redo applied");
        });
        wordCountButton.addActionListener(e -> showWordCount());
        spellCheckButton.addActionListener(e -> showSpellCheck());
        exportHtmlButton.addActionListener(e -> exportHtml());

        setSize(1100, 650);
        setLocationRelativeTo(null);
        refreshView();
    }

    private void applyText() {
        Paragraph paragraph = firstParagraph();
        String desired = editorArea.getText();
        String current = paragraph.getText();
        if (desired.length() >= current.length()) {
            String extra = desired.substring(current.length());
            manager.execute(new InsertTextCommand(manager, paragraph.getId(), extra));
        } else {
            paragraph.setText(desired);
        }
        refreshView();
        setStatus("Text applied to paragraph");
    }

    private void refreshView() {
        editorArea.setText(firstParagraph().getText());
        previewArea.setText(manager.getCurrentDocument().render());
    }

    private void showWordCount() {
        Document document = manager.getCurrentDocument();
        WordCountVisitor visitor = new WordCountVisitor();
        document.accept(visitor);
        JOptionPane.showMessageDialog(this, "Word count: " + visitor.getCount());
        setStatus("Word count computed: " + visitor.getCount());
    }

    private void showSpellCheck() {
        Document document = manager.getCurrentDocument();
        SpellCheckVisitor visitor = new SpellCheckVisitor();
        document.accept(visitor);
        String message = visitor.getUnknownWords().isEmpty()
            ? "No unknown words found"
            : String.join(", ", visitor.getUnknownWords());
        JOptionPane.showMessageDialog(this, message);
        setStatus("Spell check completed");
    }

    private void exportHtml() {
        ExportManager.getInstance().export("html", manager.getCurrentDocument(), Path.of("exports/gui-preview.html"));
        JOptionPane.showMessageDialog(this, "Exported to exports/gui-preview.html");
        setStatus("Exported HTML preview");
    }

    private void uploadText() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Upload Text File");
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "md", "text"));
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            String content = Files.readString(chooser.getSelectedFile().toPath());
            editorArea.setText(content);
            applyText();
            setStatus("Uploaded: " + chooser.getSelectedFile().getName());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to upload file", "Error", JOptionPane.ERROR_MESSAGE);
            setStatus("Upload failed");
        }
    }

    private Paragraph firstParagraph() {
        Document document = manager.getCurrentDocument();
        for (var child : document.getChildren()) {
            if (child instanceof Paragraph paragraph) {
                return paragraph;
            }
            for (var nested : child.getChildren()) {
                if (nested instanceof Paragraph paragraph) {
                    return paragraph;
                }
            }
        }
        throw new IllegalStateException("No paragraph in document");
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> new SmartDocumentEditorFrame().setVisible(true));
    }

    private void setStatus(String status) {
        statusLabel.setText(status);
    }
}

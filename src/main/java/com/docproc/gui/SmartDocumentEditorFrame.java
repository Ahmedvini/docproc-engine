package com.docproc.gui;

import com.docproc.command.InsertTextCommand;
import com.docproc.core.DocumentManager;
import com.docproc.core.ExportManager;
import com.docproc.model.Document;
import com.docproc.model.Paragraph;
import com.docproc.visitor.WordCountVisitor;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.nio.file.Path;

public class SmartDocumentEditorFrame extends JFrame {
    private final DocumentManager manager = DocumentManager.getInstance();
    private final JTextArea editorArea = new JTextArea(20, 60);

    public SmartDocumentEditorFrame() {
        super("Smart Document Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(editorArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton applyButton = new JButton("Apply Text");
        JButton undoButton = new JButton("Undo");
        JButton redoButton = new JButton("Redo");
        JButton wordCountButton = new JButton("Word Count");
        JButton exportHtmlButton = new JButton("Export HTML");

        controls.add(applyButton);
        controls.add(undoButton);
        controls.add(redoButton);
        controls.add(wordCountButton);
        controls.add(exportHtmlButton);
        add(controls, BorderLayout.SOUTH);

        applyButton.addActionListener(e -> applyText());
        undoButton.addActionListener(e -> {
            manager.undo();
            refreshText();
        });
        redoButton.addActionListener(e -> {
            manager.redo();
            refreshText();
        });
        wordCountButton.addActionListener(e -> showWordCount());
        exportHtmlButton.addActionListener(e -> exportHtml());

        pack();
        setLocationRelativeTo(null);
        refreshText();
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
        refreshText();
    }

    private void refreshText() {
        editorArea.setText(firstParagraph().getText());
    }

    private void showWordCount() {
        Document document = manager.getCurrentDocument();
        WordCountVisitor visitor = new WordCountVisitor();
        document.accept(visitor);
        JOptionPane.showMessageDialog(this, "Word count: " + visitor.getCount());
    }

    private void exportHtml() {
        ExportManager.getInstance().export("html", manager.getCurrentDocument(), Path.of("exports/gui-preview.html"));
        JOptionPane.showMessageDialog(this, "Exported to exports/gui-preview.html");
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
}

package com.docproc.hosting;

import com.docproc.core.DocumentManager;
import com.docproc.model.Document;
import com.docproc.model.Paragraph;
import com.docproc.model.Section;
import com.docproc.visitor.SpellCheckVisitor;
import com.docproc.visitor.WordCountVisitor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DocumentHttpServer {
    private final DocumentManager manager;

    public DocumentHttpServer(DocumentManager manager) {
        this.manager = manager;
    }

    public void start(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new RootHandler());
            server.createContext("/health", exchange -> write(exchange, 200, "OK", "text/plain"));
            server.createContext("/update-title", new UpdateTitleHandler());
            server.createContext("/update-paragraph", new UpdateParagraphHandler());
            server.createContext("/add-paragraph", new AddParagraphHandler());
            server.createContext("/export-html", new ExportHtmlHandler());
            server.createContext("/document", new DocumentHandler());
            server.createContext("/word-count", new WordCountHandler());
            server.createContext("/spell-check", new SpellCheckHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("HTTP server started on port " + port);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to start HTTP server", e);
        }
    }

    private class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
                        Document document = manager.getCurrentDocument();
                        if (document == null) {
                                write(exchange, 404, "<html><body><h1>No active document</h1></body></html>", "text/html");
                                return;
                        }

                        WordCountVisitor wordCountVisitor = new WordCountVisitor();
                        document.accept(wordCountVisitor);

                        SpellCheckVisitor spellCheckVisitor = new SpellCheckVisitor();
                        document.accept(spellCheckVisitor);

                        String unknownWords = spellCheckVisitor.getUnknownWords().isEmpty()
                                ? "None"
                                : spellCheckVisitor.getUnknownWords().stream().collect(Collectors.joining(", "));

            String body = """
                <html>
                                    <head>
                                        <title>Smart Document Editor</title>
                                        <style>
                                            body { font-family: Georgia, serif; margin: 2rem; background: #f7f5ef; color: #1f2933; }
                                            h1 { margin-bottom: 0.4rem; }
                                            .muted { color: #52606d; margin-top: 0; }
                                            .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 1rem; margin: 1.2rem 0; }
                                            .card { background: #ffffff; border: 1px solid #d9e2ec; border-radius: 10px; padding: 1rem; }
                                            pre { white-space: pre-wrap; background: #fff; border: 1px solid #d9e2ec; border-radius: 10px; padding: 1rem; }
                                            a { color: #0b69a3; text-decoration: none; }
                                            a:hover { text-decoration: underline; }
                                        </style>
                                    </head>
                  <body>
                    <h1>Smart Document Editor</h1>
                                        <p class="muted">Homepage editor with preview and quick metrics.</p>

                                        <div class="grid">
                                            <div class="card">
                                                <h3>Title</h3>
                                                <p>%s</p>
                                            </div>
                                            <div class="card">
                                                <h3>Word Count</h3>
                                                <p>%d</p>
                                            </div>
                                            <div class="card">
                                                <h3>Unknown Words</h3>
                                                <p>%s</p>
                                            </div>
                                        </div>

                                        <h3>Document Preview</h3>
                                        <pre>%s</pre>

                                        <div class="grid">
                                            <div class="card">
                                                <h3>Edit Title</h3>
                                                <form method="post" action="/update-title">
                                                    <input type="text" name="title" value="%s" style="width:100%%;padding:0.5rem;" />
                                                    <button type="submit" style="margin-top:0.7rem;">Save Title</button>
                                                </form>
                                            </div>
                                            <div class="card">
                                                <h3>Edit First Paragraph</h3>
                                                <form method="post" action="/update-paragraph">
                                                    <textarea name="text" rows="6" style="width:100%%;padding:0.5rem;">%s</textarea>
                                                    <button type="submit" style="margin-top:0.7rem;">Save Paragraph</button>
                                                </form>
                                            </div>
                                            <div class="card">
                                                <h3>Add Paragraph</h3>
                                                <form method="post" action="/add-paragraph">
                                                    <textarea name="text" rows="6" style="width:100%%;padding:0.5rem;" placeholder="Write new paragraph"></textarea>
                                                    <button type="submit" style="margin-top:0.7rem;">Add</button>
                                                </form>
                                            </div>
                                        </div>

                                        <p>
                                            <a href="/export-html">Export HTML Preview</a>
                                        </p>

                                        <h3>Raw Endpoints</h3>
                    <ul>
                                            <li><a href="/health">/health</a></li>
                                            <li><a href="/document">/document</a></li>
                                            <li><a href="/word-count">/word-count</a></li>
                                            <li><a href="/spell-check">/spell-check</a></li>
                    </ul>
                  </body>
                </html>
                                """.formatted(
                                escapeHtml(document.getTitle()),
                                wordCountVisitor.getCount(),
                                escapeHtml(unknownWords),
                                escapeHtml(document.render()),
                                escapeHtml(document.getTitle()),
                                escapeHtml(firstParagraphText(document))
                        );
            write(exchange, 200, body, "text/html");
        }
    }

    private class UpdateTitleHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                write(exchange, 405, "Method Not Allowed", "text/plain");
                return;
            }
            Document document = manager.getCurrentDocument();
            if (document == null) {
                write(exchange, 404, "No active document", "text/plain");
                return;
            }
            Map<String, String> form = parseForm(exchange);
            String title = form.getOrDefault("title", "").trim();
            if (!title.isBlank()) {
                document.setTitle(title);
            }
            redirectToHome(exchange);
        }
    }

    private class UpdateParagraphHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                write(exchange, 405, "Method Not Allowed", "text/plain");
                return;
            }
            Document document = manager.getCurrentDocument();
            if (document == null) {
                write(exchange, 404, "No active document", "text/plain");
                return;
            }
            Map<String, String> form = parseForm(exchange);
            Paragraph paragraph = firstParagraph(document);
            paragraph.setText(form.getOrDefault("text", ""));
            redirectToHome(exchange);
        }
    }

    private class AddParagraphHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                write(exchange, 405, "Method Not Allowed", "text/plain");
                return;
            }
            Document document = manager.getCurrentDocument();
            if (document == null) {
                write(exchange, 404, "No active document", "text/plain");
                return;
            }
            Map<String, String> form = parseForm(exchange);
            String text = form.getOrDefault("text", "").trim();
            if (!text.isBlank()) {
                Paragraph paragraph = new Paragraph(text);
                Section section = firstSection(document);
                if (section != null) {
                    section.add(paragraph);
                } else {
                    document.add(paragraph);
                }
            }
            redirectToHome(exchange);
        }
    }

    private class ExportHtmlHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Document document = manager.getCurrentDocument();
            if (document == null) {
                write(exchange, 404, "No active document", "text/plain");
                return;
            }
            String html = "<html><body><pre>" + escapeHtml(document.render()) + "</pre></body></html>";
            java.nio.file.Files.createDirectories(Path.of("exports"));
            java.nio.file.Files.writeString(Path.of("exports/web-preview.html"), html);
            redirectToHome(exchange);
        }
    }

    private class DocumentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Document document = manager.getCurrentDocument();
            if (document == null) {
                write(exchange, 404, "No active document", "text/plain");
                return;
            }
            write(exchange, 200, document.render(), "text/plain");
        }
    }

    private class WordCountHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Document document = manager.getCurrentDocument();
            if (document == null) {
                write(exchange, 404, "{\"error\":\"No active document\"}", "application/json");
                return;
            }
            WordCountVisitor visitor = new WordCountVisitor();
            document.accept(visitor);
            write(exchange, 200, "{\"wordCount\":" + visitor.getCount() + "}", "application/json");
        }
    }

    private class SpellCheckHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Document document = manager.getCurrentDocument();
            if (document == null) {
                write(exchange, 404, "{\"error\":\"No active document\"}", "application/json");
                return;
            }
            SpellCheckVisitor visitor = new SpellCheckVisitor();
            document.accept(visitor);
            String words = visitor.getUnknownWords().stream()
                .map(word -> "\"" + word + "\"")
                .collect(Collectors.joining(","));
            write(exchange, 200, "{\"unknownWords\":[" + words + "]}", "application/json");
        }
    }

    private void write(HttpExchange exchange, int statusCode, String body, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=utf-8");
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private Map<String, String> parseForm(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> values = new HashMap<>();
        if (body.isBlank()) {
            return values;
        }
        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String value = parts.length > 1 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
            values.put(key, value);
        }
        return values;
    }

    private void redirectToHome(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Location", "/");
        exchange.sendResponseHeaders(303, -1);
        exchange.close();
    }

    private Section firstSection(Document document) {
        for (var child : document.getChildren()) {
            if (child instanceof Section section) {
                return section;
            }
        }
        return null;
    }

    private Paragraph firstParagraph(Document document) {
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
        Section section = firstSection(document);
        Paragraph paragraph = new Paragraph("");
        if (section != null) {
            section.add(paragraph);
        } else {
            document.add(paragraph);
        }
        return paragraph;
    }

    private String firstParagraphText(Document document) {
        return firstParagraph(document).getText();
    }

    private String escapeHtml(String value) {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }
}

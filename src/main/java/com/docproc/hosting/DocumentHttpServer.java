package com.docproc.hosting;

import com.docproc.core.DocumentManager;
import com.docproc.model.Document;
import com.docproc.visitor.SpellCheckVisitor;
import com.docproc.visitor.WordCountVisitor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
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
                                        <p class="muted">Homepage dashboard with preview and quick metrics.</p>

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
                                escapeHtml(document.render())
                        );
            write(exchange, 200, body, "text/html");
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

    private String escapeHtml(String value) {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }
}

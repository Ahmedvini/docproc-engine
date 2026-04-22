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
            String body = """
                <html>
                  <head><title>Smart Document Editor</title></head>
                  <body>
                    <h1>Smart Document Editor</h1>
                    <p>Available endpoints:</p>
                    <ul>
                      <li>/health</li>
                      <li>/document</li>
                      <li>/word-count</li>
                      <li>/spell-check</li>
                    </ul>
                  </body>
                </html>
                """;
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
}

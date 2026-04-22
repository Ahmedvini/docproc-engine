package com.docproc.versioning;

import com.docproc.model.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class VersionControlService {
    private final List<DocumentVersion> versions = new ArrayList<>();

    public DocumentVersion commit(Document document, String message) {
        int id = versions.size() + 1;
        Document snapshot = (Document) document.deepCopy();
        DocumentVersion version = new DocumentVersion(id, message, Instant.now(), snapshot);
        versions.add(version);
        return version;
    }

    public List<DocumentVersion> history() {
        return List.copyOf(versions);
    }

    public Document checkout(int versionId) {
        return versions.stream()
            .filter(v -> v.id() == versionId)
            .findFirst()
            .map(v -> (Document) v.snapshot().deepCopy())
            .orElseThrow(() -> new IllegalArgumentException("Version not found: " + versionId));
    }
}

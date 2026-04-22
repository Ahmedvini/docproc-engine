package com.docproc.versioning;

import com.docproc.model.Document;

import java.time.Instant;

public record DocumentVersion(int id, String message, Instant timestamp, Document snapshot) {
}

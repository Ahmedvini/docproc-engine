package com.docproc.observer;

import java.time.Instant;

public record DocumentEvent(String source, String action, Instant timestamp) {
}

package com.docproc.plugin;

import com.docproc.model.Document;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class WordFrequencyPlugin implements Plugin {
    @Override
    public String name() {
        return "word-frequency";
    }

    @Override
    public void initialize(PluginContext context) {
    }

    @Override
    public void execute(Document document) {
        Map<String, Integer> counts = new HashMap<>();
        Arrays.stream(document.render().toLowerCase().replaceAll("[^a-z0-9\\s]", " ").split("\\s+"))
            .filter(word -> !word.isBlank())
            .forEach(word -> counts.merge(word, 1, Integer::sum));
        System.out.println("[Plugin:word-frequency] Top words:");
        counts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
            .limit(10)
            .forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));
    }
}

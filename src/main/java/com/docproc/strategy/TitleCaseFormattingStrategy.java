package com.docproc.strategy;

import com.docproc.model.Paragraph;

public class TitleCaseFormattingStrategy implements FormattingStrategy {
    @Override
    public void apply(Paragraph paragraph) {
        String[] words = paragraph.getText().trim().split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }
            builder.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                builder.append(word.substring(1).toLowerCase());
            }
            builder.append(' ');
        }
        paragraph.setText(builder.toString().trim());
    }
}

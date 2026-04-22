package com.docproc.strategy;

import com.docproc.model.Paragraph;

public class UpperCaseFormattingStrategy implements FormattingStrategy {
    @Override
    public void apply(Paragraph paragraph) {
        paragraph.setText(paragraph.getText().toUpperCase());
    }
}

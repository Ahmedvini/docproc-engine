package com.docproc.strategy;

import com.docproc.model.Paragraph;

public interface FormattingStrategy {
    void apply(Paragraph paragraph);
}

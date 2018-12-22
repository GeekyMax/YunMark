package com.geekymax.ot;


import java.io.Serializable;

import static com.geekymax.ot.Transform.transformMarkupAgainstInsert;

/**
 * Created by Stas on 3/12/16.
 */
public class Insert extends TextChange implements Serializable {
    final String text;

    public Insert(String text) {
        this.text = text;
    }

    @Override
    Text apply(int pos, Text text) throws ValidationException {
        text.buffer.insert(pos, this.text);
        transformMarkupAgainstInsert(text.markup, pos, this.text.length());
        return text;
    }

    @Override
    int offset() {
        return text.length();
    }

    @Override
    public String toString() {
        return  text;
    }
}

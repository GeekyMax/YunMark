package com.geekymax.ot;


import java.io.Serializable;

import static com.geekymax.ot.Transform.transformMarkupAgainstDelete;

/**
 * Created by Stas on 3/12/16.
 */
public class Delete extends TextChange implements Serializable {
    final int len;

    public Delete(int len) {
        this.len = len;
    }

    @Override
    Text apply(int lo, Text text) throws ValidationException {
        text.buffer.delete(lo, lo + len);
        transformMarkupAgainstDelete(text.markup, lo, len);
        return text;
    }

    @Override
    int offset() {
        return 0;
    }

    @Override
    public String toString() {
        return ""+len;
    }

}

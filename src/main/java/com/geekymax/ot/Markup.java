package com.geekymax.ot;

import java.io.Serializable;

/**
 * Created by Stas on 3/21/16.
 */
abstract class Markup extends Change implements Serializable {
    @Override
    int offset() {
        return 0;
    }

    @Override
    Text apply(int pos, Text text) throws ValidationException {
        text.markup.put(pos, this);
        return text;
    }

    //fixme: Compare
    @Override
    public boolean equals(Object obj) {
        return true;
    }

    /**
     * Override to handle notification regarding offset change
     */
    public void fireShift(int delta, int pos) {
        // do nothing
    }
}

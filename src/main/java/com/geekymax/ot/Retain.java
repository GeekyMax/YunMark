package com.geekymax.ot;

import java.io.Serializable;

/**
 * Change一个实现类, 表示一个移动光标操作
 * @author Stas
 * @date 3/12/16
 */
public class Retain extends TextChange implements Serializable {
    private final int length;

    public Retain(int length) {
        this.length = length;
    }

    @Override
    Text apply(int pos, Text text) throws ValidationException {
        return text;
    }

    @Override
    int offset() {
        return length;
    }

    @Override
    public String toString() {
        return ""+length;
    }
}

package com.geekymax.ot;


import java.io.Serializable;

import static com.geekymax.ot.Transform.transformMarkupAgainstDelete;

/**
 * Change的实现类,表示一个删除操作
 * @author Stas
 * @date 3/12/16
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

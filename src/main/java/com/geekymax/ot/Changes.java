package com.geekymax.ot;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * AbstractChanged的实现类,是多个Change的集合
 *
 * @author Max Huang
 * @author Stas
 */
public class Changes extends AbstractChange implements Serializable {
    final List<AbstractChange> changes;

    public Changes(AbstractChange... changes) {
        this(Arrays.asList(changes));
    }

    public Changes(Iterable<AbstractChange> changes) {
        this.changes = Lists.newArrayList(changes);
    }

    @Override
    public Text apply(int pos, Text text) throws ValidationException {
        for (AbstractChange change : changes) {
            change.apply(pos, text);
            pos += change.offset();
        }
        return text;
    }

    @Override
    public int offset() {
        int pos = 0;
        for (AbstractChange change : changes) {
            pos += change.offset();
        }
        return pos;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int pos = 0;
        for (AbstractChange c : changes) {
            if (c instanceof Retain) {
                pos = Integer.parseInt(c.toString());
            } else if (c instanceof Insert) {
                stringBuilder.append("ins[").append(pos).append(";\"").append(c.toString()).append("\"]\n");
            } else if (c instanceof Delete) {
                stringBuilder.append("del[").append(pos).append(";\"").append(c.toString()).append("\"]\n");
            }
        }
        return stringBuilder.toString();
    }

    public int calculateCaret(int oldCaret) {
        int nowPosition = 0;
        int nowCaret = oldCaret;
        for (AbstractChange c : changes) {
            if (c instanceof Retain) {
                nowPosition += c.offset();
            } else if (c instanceof Insert) {
                if (nowPosition <= nowCaret) {
                    nowCaret += c.offset();
                }
                nowPosition += c.offset();
            } else if (c instanceof Delete) {
                if (((Delete) c).len + nowPosition <= nowCaret) {
                    nowCaret -= ((Delete) c).len;
                } else if (nowPosition < nowCaret) {
                    nowCaret = nowPosition;
                }
            }
        }
        return nowCaret;
    }
}

package com.geekymax.ot;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Stas on 3/12/16.
 */
public class Changes extends Change implements Serializable {
    final List<Change> changes;

    public Changes(Change... changes) {
        this(Arrays.asList(changes));
    }

    public Changes(Iterable<Change> changes) {
        this.changes = Lists.newArrayList(changes);
    }

    @Override
    public Text apply(int pos, Text text) throws ValidationException {
        for (Change change : changes) {
            change.apply(pos, text);
            pos += change.offset();
        }
        return text;
    }

    @Override
    public int offset() {
        int pos = 0;
        for (Change change : changes) {
            pos += change.offset();
        }
        return pos;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int pos = 0;
        for (Change c : changes) {
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
        for (Change c : changes) {
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
